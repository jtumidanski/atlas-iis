package statistics

import (
	json2 "atlas-iis/rest/json"
	"github.com/gorilla/mux"
	"log"
	"net/http"
	"strconv"
)

// GenericError is a generic error message returned by a server
type GenericError struct {
	Message string `json:"message"`
}

func GetEquipmentStatistics(l *log.Logger) http.HandlerFunc {
	return func(rw http.ResponseWriter, r *http.Request) {
		ei := getEquipmentId(r)
		e, err := GetEquipmentCache().GetEquipment(ei)
		if err != nil {
			l.Println("[ERROR] deserializing instruction", err)
			rw.WriteHeader(http.StatusBadRequest)
			json2.ToJSON(&GenericError{Message: err.Error()}, rw)
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
