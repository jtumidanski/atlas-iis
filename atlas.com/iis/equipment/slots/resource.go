package slots

import (
	"atlas-iis/json"
	"github.com/gorilla/mux"
	"github.com/sirupsen/logrus"
	"log"
	"net/http"
	"strconv"
)

func InitResource(router *mux.Router, l logrus.FieldLogger) {
	eRouter := router.PathPrefix("/equipment").Subrouter()
	eRouter.HandleFunc("/{equipmentId}/slots", GetEquipmentSlots(l)).Methods(http.MethodGet)
}

// GenericError is a generic error message returned by a server
type GenericError struct {
	Message string `json:"message"`
}

func GetEquipmentSlots(l logrus.FieldLogger) http.HandlerFunc {
	return func(rw http.ResponseWriter, r *http.Request) {
		ei := getEquipmentId(r)
		e, err := GetEquipmentSlotCache().GetEquipmentSlot(ei)
		if err != nil {
			l.WithError(err).Errorln("Deserializing instruction", err)
			rw.WriteHeader(http.StatusBadRequest)
			json.ToJSON(&GenericError{Message: err.Error()}, rw)
			return
		}

		var data = make([]EquipmentSlotData, 0)
		for _, s := range e.slot {
			data = append(data, EquipmentSlotData{
				Id:   strconv.Itoa(int(e.itemId)),
				Type: "com.atlas.iis.attribute.EquipmentSlotAttributes",
				Attributes: EquipmentSlotAttributes{
					Name: e.name,
					WZ:   e.wz,
					Slot: s,
				},
			})
		}

		result := EquipmentSlotListDataContainer{
			Data: data}
		rw.WriteHeader(http.StatusOK)
		json.ToJSON(result, rw)
	}
}

func getEquipmentId(r *http.Request) uint32 {
	vars := mux.Vars(r)
	value, err := strconv.Atoi(vars["equipmentId"])
	if err != nil {
		log.Println("Error parsing characterId as uint32")
		return 0
	}
	return uint32(value)
}
