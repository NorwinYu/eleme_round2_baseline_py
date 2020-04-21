package dispatch

import (
	"context"
	"github.com/gin-gonic/gin"
	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"
	"net/http"
	"os"
	"sync"
	"time"
)

type Server struct {
	closeMux     *sync.Mutex
	port         int
	debug        bool
	shutdownWait int
	closed       bool
	logger       *logrus.Entry
	srv          *http.Server
	dispatcher   Dispatcher
}

func NewServer(shutdownWait int, debug bool, clientId string) (*Server, error) {
	server := &Server{
		closeMux:     &sync.Mutex{},
		debug:        debug,
		shutdownWait: shutdownWait,
		closed:       false,
		logger:       logrus.WithField("client", clientId).WithField("time", time.Now().String()),
		dispatcher:   NewGreedyDispatcher(),
	}
	return server, nil
}

func (server *Server) Start() error {
	server.closeMux.Lock()
	defer server.closeMux.Unlock()
	if server.closed {
		return errors.New("Server is closed")
	}
	r := gin.New()
	// Add Log
	r.Use(NewLogger(os.Stdout, server.debug))
	//Add Recovery
	r.Use(gin.Recovery())

	v1 := r.Group("/api/v1")
	v1.GET("/ping", func(c *gin.Context) {
		server.logger.Info("")
		c.JSON(http.StatusOK, server.dispatcher.Ping())
	})

	v1.POST("/dispatch", func(c *gin.Context) {
		request := &DispatchRequest{}
		if err := c.ShouldBind(&request); err == nil {
			ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
			defer cancel()
			ch := make(chan *Response)
			go func() {
				response, errs := server.dispatcher.Dispatch(request)
				if errs == nil {
					ch <- NewResponse(response)
				} else {
					ch <- NewErrorResponse(Status_UNKNOWN, errs.Error())
				}
			}()
			var response *Response
			for {
				select {
				case <-ctx.Done():
					server.logger.Errorf("Request timeout %v",*request)
					c.JSON(http.StatusOK, NewErrorResponse(Status_TIMEOUT, "Dispatcher timeout"))
					return
				case response = <-ch:
					server.logger.Infof("Request:%s,Response:%s", *request, *response)
					c.JSON(http.StatusOK, response)
					return
				}
			}
		} else {
			response := NewErrorResponse(
				Status_BAD_REQUEST, "Could not resolve request body.",
			)
			server.logger.Infof("Request:%s,Response:%s", &request, &response)
			c.JSON(http.StatusOK, &response)
		}
	})
	srv := &http.Server{
		Addr:    ":8080",
		Handler: r,
	}
	server.srv = srv
	go func() {
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			server.logger.Fatalf("listen: %s\n", err)
		}
	}()
	return nil
}

func (server *Server) Shutdown() error {
	server.closeMux.Lock()
	defer server.closeMux.Unlock()
	if server.closed {
		return nil
	}
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*time.Duration(server.shutdownWait))
	defer cancel()
	if err := server.srv.Shutdown(ctx); err != nil {
		server.logger.Fatal("Server shutdown error:", err)
		return err
	}
	server.closed = true
	server.logger.Info("Server exited")
	return nil
}
