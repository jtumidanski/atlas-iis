package statistics

import "sync"

type equipmentCache struct {
	mutex     sync.RWMutex
	equipment map[uint32]Equipment
}

var cache *equipmentCache
var once sync.Once

func GetEquipmentCache() *equipmentCache {
	once.Do(func() {
		cache = &equipmentCache{
			mutex:     sync.RWMutex{},
			equipment: make(map[uint32]Equipment),
		}
	})
	return cache
}

func (e *equipmentCache) GetEquipment(itemId uint32) (*Equipment, error) {
	var equipment Equipment
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
