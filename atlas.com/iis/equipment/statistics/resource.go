package statistics

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
	getEquipmentStatistics = "get_equipment_statistics"
)

func InitResource(router *mux.Router, l logrus.FieldLogger) {
	r := router.PathPrefix("/equipment").Subrouter()
	r.HandleFunc("/{equipmentId}", registerGetEquipmentStatistics(l)).Methods(http.MethodGet)
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

func registerGetEquipmentStatistics(l logrus.FieldLogger) http.HandlerFunc {
	return rest.RetrieveSpan(getEquipmentStatistics, func(span opentracing.Span) http.HandlerFunc {
		return parseEquipmentId(l, func(equipmentId uint32) http.HandlerFunc {
			return handleGetEquipmentStatistics(l)(span)(equipmentId)
		})
	})
}

func handleGetEquipmentStatistics(l logrus.FieldLogger) func(span opentracing.Span) func(equipmentId uint32) http.HandlerFunc {
	return func(span opentracing.Span) func(equipmentId uint32) http.HandlerFunc {
		return func(equipmentId uint32) http.HandlerFunc {
			return func(w http.ResponseWriter, r *http.Request) {
				e, err := GetEquipmentCache().GetEquipment(equipmentId)
				if err != nil {
					l.WithError(err).Errorln("Deserializing instruction", err)
					w.WriteHeader(http.StatusBadRequest)
					json.ToJSON(&resource.GenericError{Message: err.Error()}, w)
					return
				}

				result := EquipmentStatisticsDataContainer{
					Data: EquipmentStatisticsData{
						Id:   strconv.Itoa(int(e.itemId)),
						Type: "com.atlas.iis.attribute.EquipmentAttributes",
						Attributes: EquipmentStatisticsAttributes{
							Strength:      e.strength,
							Dexterity:     e.dexterity,
							Intelligence:  e.intelligence,
							Luck:          e.luck,
							HP:            e.hp,
							MP:            e.mp,
							WeaponAttack:  e.weaponAttack,
							MagicAttack:   e.magicAttack,
							WeaponDefense: e.weaponDefense,
							MagicDefense:  e.magicDefense,
							Accuracy:      e.accuracy,
							Avoidability:  e.avoidability,
							Speed:         e.speed,
							Jump:          e.jump,
							Slots:         e.slots,
						},
					}}
				w.WriteHeader(http.StatusOK)
				json.ToJSON(result, w)
			}
		}
	}
}
