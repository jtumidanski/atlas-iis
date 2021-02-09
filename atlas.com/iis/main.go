package main

import (
	"atlas-iis/rest"
	"atlas-iis/wz"
	"log"
	"os"
	"os/signal"
	"syscall"
)

func main() {
	l := log.New(os.Stdout, "iis ", log.LstdFlags|log.Lmicroseconds)

	wzDir := os.Getenv("WZ_DIR")
	wz.GetFileCache().Init(wzDir)

	createRestService(l)

	// trap sigterm or interrupt and gracefully shutdown the server
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt, os.Kill, syscall.SIGTERM)

	// Block until a signal is received.
	sig := <-c
	l.Println("[INFO] shutting down via signal:", sig)
}

func createRestService(l *log.Logger) {
	rs := rest.NewServer(l)
	go rs.Run()
}
