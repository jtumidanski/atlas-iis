package rest

import (
	"atlas-iis/equipment/slots"
	"atlas-iis/equipment/statistics"
	"context"
	"github.com/gorilla/mux"
	"github.com/sirupsen/logrus"
	"net/http"
	"sync"
)

func CreateRestService(l *logrus.Logger, ctx context.Context, wg *sync.WaitGroup) {
	go NewServer(l, ctx, wg, ProduceRoutes)
}

func ProduceRoutes(l logrus.FieldLogger) http.Handler {
		router := mux.NewRouter().PathPrefix("/ms/iis").Subrouter()
		router.Use(CommonHeader)

		eRouter := router.PathPrefix("/equipment").Subrouter()
		eRouter.HandleFunc("/{equipmentId}", statistics.GetEquipmentStatistics(l)).Methods(http.MethodGet)
		eRouter.HandleFunc("/{equipmentId}/slots", slots.GetEquipmentSlots(l)).Methods(http.MethodGet)

		return router
}
