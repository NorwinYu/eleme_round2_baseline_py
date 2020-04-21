package dispatch

import (
	"github.com/sirupsen/logrus"
	"github.com/spf13/cobra"
	"os"
	"os/signal"
	"syscall"
)

var (
	debug            bool
	clientId         string
	shutdownWaitSecs int
)

func init() {
	Cmd.PersistentFlags().BoolVar(&debug, "debug", true, "debug mode")
	Cmd.PersistentFlags().StringVar(&clientId, "clientId", "", "Id of your tianchi account")
	Cmd.PersistentFlags().IntVar(&shutdownWaitSecs, "shutdownWait", 10, "shutdown wait seconds")
}

var (
	// Cmd is the sub command for running as an api server
	Cmd = &cobra.Command{
		Use:   "run",
		Short: "Start player's dispatching http server at port 8080.",
		Long:  "Start player's dispatching http server at port 8080.",
		Run: func(cmd *cobra.Command, args []string) {
			if debug {
				logrus.SetLevel(logrus.DebugLevel)
			}

			server, err := NewServer(shutdownWaitSecs, debug, clientId)
			if err != nil {
				logrus.Fatalf("Starting player's server failed:  %v\n", err)
			}

			err = server.Start()
			if err != nil {
				logrus.Fatalf("Starting player's server failed:  %v\n", err)
			}

			signals := make(chan os.Signal)
			signal.Notify(signals, syscall.SIGTERM, syscall.SIGINT)
			<-signals
			err = server.Shutdown()
			if err != nil {
				logrus.Fatalf("Shutdown client server failed:  %v\n", err)
			}
		},
	}
)
