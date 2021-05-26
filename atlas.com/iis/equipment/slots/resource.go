package slots

import (
	json2 "atlas-iis/rest/json"
	"github.com/gorilla/mux"
	"github.com/sirupsen/logrus"
	"log"
	"net/http"
	"strconv"
)

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
			json2.ToJSON(&GenericError{Message: err.Error()}, rw)
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
		json2.ToJSON(result, rw)
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
