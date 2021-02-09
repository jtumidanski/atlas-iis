package statistics

import (
	"atlas-iis/wz"
	"atlas-iis/xml"
	"errors"
	"fmt"
	"strconv"
)

func Read(itemId uint32) (*Equipment, error) {
	i, err := findItem(itemId)
	if err != nil {
		return nil, err
	}

	exml, err := xml.Read(i.Path())
	if err != nil {
		return nil, err
	}
	return getEquipmentFromInfo(itemId, exml)
}

func getEquipmentFromInfo(itemId uint32, exml *xml.Node) (*Equipment, error) {
	info, err := exml.ChildByName("info")
	if err != nil {
		info, err := exml.ChildByName("0" + strconv.Itoa(int(itemId)))
		if err != nil {
			return nil, err
		} else {
			info, err = info.ChildByName("info")
			if err != nil {
				return nil, err
			}
		}
	}
	if info == nil {
		return &Equipment{itemId: itemId}, nil
	}

	return &Equipment{
		itemId:        itemId,
		strength:      info.GetShort("incSTR", 0),
		dexterity:     info.GetShort("incDEX", 0),
		intelligence:  info.GetShort("incINT", 0),
		luck:          info.GetShort("incLUK", 0),
		weaponAttack:  info.GetShort("incPAD", 0),
		weaponDefense: info.GetShort("incPDD", 0),
		magicAttack:   info.GetShort("incMAD", 0),
		magicDefense:  info.GetShort("incMDD", 0),
		accuracy:      info.GetShort("incACC", 0),
		avoidability:  info.GetShort("incEVA", 0),
		speed:         info.GetShort("incSpeed", 0),
		jump:          info.GetShort("incJump", 0),
		hp:            info.GetShort("incMHP", 0),
		mp:            info.GetShort("incMMP", 0),
		slots:         info.GetShort("tuc", 0),
	}, nil
}

func findItem(itemId uint32) (*wz.FileEntry, error) {
	idstr := "0" + strconv.Itoa(int(itemId))
	runes := []rune(idstr)

	if val, ok := wz.GetFileCache().GetFile(string(runes[0:4]) + ".img.xml"); ok == nil {
		return val, nil
	}
	if val, ok := wz.GetFileCache().GetFile(string(runes[0:1]) + ".img.xml"); ok == nil {
		return val, nil
	}
	if val, ok := wz.GetFileCache().GetFile(idstr + ".img.xml"); ok == nil {
		return val, nil
	}
	return nil, errors.New(fmt.Sprintf("item %d not found", itemId))
}
