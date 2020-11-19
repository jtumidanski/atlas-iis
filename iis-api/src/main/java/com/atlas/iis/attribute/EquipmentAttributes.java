package com.atlas.iis.attribute;

import rest.AttributeResult;

public record EquipmentAttributes(Integer strength, Integer dexterity, Integer intelligence, Integer luck,
                                  Integer hp, Integer mp, Integer weaponAttack, Integer magicAttack, Integer weaponDefense,
                                  Integer magicDefense, Integer accuracy, Integer avoidability, Integer speed, Integer jump,
                                  Integer slots) implements AttributeResult {
}
