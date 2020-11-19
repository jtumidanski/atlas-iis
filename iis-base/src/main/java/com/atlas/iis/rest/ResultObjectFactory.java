package com.atlas.iis.rest;

import com.atlas.iis.attribute.EquipmentAttributes;
import com.atlas.iis.builder.EquipmentAttributesBuilder;
import com.atlas.iis.model.Equipment;

import builder.ResultObjectBuilder;

public class ResultObjectFactory {
   public static ResultObjectBuilder create(Equipment equipment) {
      return new ResultObjectBuilder(EquipmentAttributes.class, equipment.itemId())
            .setAttribute(new EquipmentAttributesBuilder()
                  .setStrength(equipment.strength())
                  .setDexterity(equipment.dexterity())
                  .setIntelligence(equipment.intelligence())
                  .setLuck(equipment.luck())
                  .setWeaponAttack(equipment.weaponAttack())
                  .setWeaponDefense(equipment.weaponDefense())
                  .setMagicAttack(equipment.magicAttack())
                  .setMagicDefense(equipment.magicDefense())
                  .setAccuracy(equipment.accuracy())
                  .setAvoidability(equipment.avoidability())
                  .setSpeed(equipment.speed())
                  .setJump(equipment.jump())
                  .setHp(equipment.hp())
                  .setMp(equipment.mp())
                  .setSlots(equipment.slots())
            );
   }
}
