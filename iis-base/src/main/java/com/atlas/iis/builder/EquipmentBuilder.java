package com.atlas.iis.builder;

import com.app.common.builder.RecordBuilder;
import com.atlas.iis.model.Equipment;

public class EquipmentBuilder extends RecordBuilder<Equipment, EquipmentBuilder> {
   private int equipmentId;

   private int strength;

   private int dexterity;

   private int intelligence;

   private int luck;

   private int weaponAttack;

   private int weaponDefense;

   private int magicAttack;

   private int magicDefense;

   private int accuracy;

   private int avoidability;

   private int speed;

   private int jump;

   private int hp;

   private int mp;

   private int slots;

   public EquipmentBuilder(int equipmentId) {
      this.equipmentId = equipmentId;
   }

   @Override
   public Equipment construct() {
      return new Equipment(equipmentId, strength, dexterity, intelligence, luck, weaponAttack, weaponDefense, magicAttack,
            magicDefense, accuracy, avoidability, speed, jump, hp, mp, slots);
   }

   @Override
   public EquipmentBuilder getThis() {
      return this;
   }

   public EquipmentBuilder setStrength(int strength) {
      this.strength = strength;
      return getThis();
   }

   public EquipmentBuilder setDexterity(int dexterity) {
      this.dexterity = dexterity;
      return getThis();
   }

   public EquipmentBuilder setIntelligence(int intelligence) {
      this.intelligence = intelligence;
      return getThis();
   }

   public EquipmentBuilder setLuck(int luck) {
      this.luck = luck;
      return getThis();
   }

   public EquipmentBuilder setWeaponAttack(int weaponAttack) {
      this.weaponAttack = weaponAttack;
      return getThis();
   }

   public EquipmentBuilder setWeaponDefense(int weaponDefense) {
      this.weaponDefense = weaponDefense;
      return getThis();
   }

   public EquipmentBuilder setMagicAttack(int magicAttack) {
      this.magicAttack = magicAttack;
      return getThis();
   }

   public EquipmentBuilder setMagicDefense(int magicDefense) {
      this.magicDefense = magicDefense;
      return getThis();
   }

   public EquipmentBuilder setAccuracy(int accuracy) {
      this.accuracy = accuracy;
      return getThis();
   }

   public EquipmentBuilder setAvoidability(int avoidability) {
      this.avoidability = avoidability;
      return getThis();
   }

   public EquipmentBuilder setSpeed(int speed) {
      this.speed = speed;
      return getThis();
   }

   public EquipmentBuilder setJump(int jump) {
      this.jump = jump;
      return getThis();
   }

   public EquipmentBuilder setHp(int hp) {
      this.hp = hp;
      return getThis();
   }

   public EquipmentBuilder setMp(int mp) {
      this.mp = mp;
      return getThis();
   }

   public EquipmentBuilder setSlots(int slots) {
      this.slots = slots;
      return getThis();
   }
}
