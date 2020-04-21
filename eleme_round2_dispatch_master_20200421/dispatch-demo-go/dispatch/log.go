package dispatch

import (
	"github.com/gin-gonic/gin"
	"github.com/sirupsen/logrus"
	"io"
	"time"
)

func Logger(writer io.Writer, debug bool) *logrus.Logger {
	logger := logrus.New()
	logger.Out = writer
	if debug {
		logger.SetLevel(logrus.DebugLevel)
	}
	return logger
}

func NewLogger(writer io.Writer, debug bool) gin.HandlerFunc {
	logger := Logger(writer, debug)
	return func(c *gin.Context) {
		// 开始时间
		startTime := time.Now()

		// 处理请求
		c.Next()

		// 结束时间
		endTime := time.Now()

		// 执行时间
		latencyTime := endTime.Sub(startTime)

		// 请求方式
		reqMethod := c.Request.Method

		// 请求路由
		reqUri := c.Request.RequestURI

		// 状态码
		statusCode := c.Writer.Status()

		// 请求IP
		clientIP := c.ClientIP()

		//日志格式
		logger.Infof("| %3d | %13v | %15s | %s | %s |",
			statusCode,
			latencyTime,
			clientIP,
			reqMethod,
			reqUri,

		)
	}
}
