package slots

import (
	"atlas-iis/json"
	"atlas-iis/rest"
	"atlas-iis/rest/resource"
	"github.com/gorilla/mux"
	"github.com/opentracing/opentracing-go"
	"github.com/sirupsen/logrus"
	"net/http"
	"strconv"
)

const (
	getEquipmentSlots = "get_equipment_slots"
)

func InitResource(router *mux.Router, l logrus.FieldLogger) {
	r := router.PathPrefix("/equipment").Subrouter()
	r.HandleFunc("/{equipmentId}/slots", registerGetEquipmentSlots(l)).Methods(http.MethodGet)
}

type equipmentIdHandler func(equipmentId uint32) http.HandlerFunc

func parseEquipmentId(l logrus.FieldLogger, next equipmentIdHandler) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		value, err := strconv.Atoi(vars["equipmentId"])
		if err != nil {
			l.WithError(err).Errorf("Error parsing characterId as uint32")
			w.WriteHeader(http.StatusBadRequest)
			return
		}
		next(uint32(value))(w, r)
	}
}

func registerGetEquipmentSlots(l logrus.FieldLogger) http.HandlerFunc {
	return rest.RetrieveSpan(getEquipmentSlots, func(span opentracing.Span) http.HandlerFunc {
		return parseEquipmentId(l, func(equipmentId uint32) http.HandlerFunc {
			return handleGetEquipmentSlots(l)(span)(equipmentId)
		})
	})
}

func handleGetEquipmentSlots(l logrus.FieldLogger) func(span opentracing.Span) func(equipmentId uint32) http.HandlerFunc {
	return func(span opentracing.Span) func(equipmentId uint32) http.HandlerFunc {
		return func(equipmentId uint32) http.HandlerFunc {
			return func(w http.ResponseWriter, r *http.Request) {
				e, err := GetEquipmentSlotCache().GetEquipmentSlot(equipmentId)
				if err != nil {
					l.WithError(err).Errorln("Deserializing instruction", err)
					w.WriteHeader(http.StatusBadRequest)
					json.ToJSON(&resource.GenericError{Message: err.Error()}, w)
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
				w.WriteHeader(http.StatusOK)
				json.ToJSON(result, w)
			}
		}
	}
}
