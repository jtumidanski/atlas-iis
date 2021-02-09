package slots

import "sync"

type equipmentSlotCache struct {
	mutex     sync.RWMutex
	equipment map[uint32]EquipmentSlot
}

var cache *equipmentSlotCache
var once sync.Once

func GetEquipmentSlotCache() *equipmentSlotCache {
	once.Do(func() {
		cache = &equipmentSlotCache{
			mutex:     sync.RWMutex{},
			equipment: make(map[uint32]EquipmentSlot),
		}
	})
	return cache
}

func (e *equipmentSlotCache) GetEquipmentSlot(itemId uint32) (*EquipmentSlot, error) {
	var equipment EquipmentSlot
	e.mutex.RLock()
	if val, ok := e.equipment[itemId]; ok {
		equipment = val
		e.mutex.RUnlock()
	} else {
		e.mutex.RUnlock()
		e.mutex.Lock()
		eq, err := Read(itemId)
		if err != nil {
			e.mutex.Unlock()
			return nil, err
		} else {
			equipment = *eq
			e.equipment[itemId] = equipment
		}
		e.mutex.Unlock()
	}
	return &equipment, nil
}
