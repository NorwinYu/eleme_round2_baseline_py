package main

import (
	"github.com/sirupsen/logrus"
	"github.com/spf13/cobra"
	"gitlab.alibaba-inc.com/fangyu.ffy/tianchi-dispatch/dispatch-demo-go/dispatch"
)

var rootCmd = &cobra.Command{
	Use:   "run",
	Short: "Tianchi&Eleme Delivery Order Dispatching Contest 2020",
	Long:  "Tianchi&Eleme Delivery Order Dispatching Contest 2020",
	Run: func(cmd *cobra.Command, args []string) {
		cmd.Usage()
	},
}

func init() {
	rootCmd.AddCommand(dispatch.Cmd)
}
func main() {
	logrus.New().Info("demo")
	if err := rootCmd.Execute(); err != nil {
		logrus.Fatalf("%v", err)
	}
}
