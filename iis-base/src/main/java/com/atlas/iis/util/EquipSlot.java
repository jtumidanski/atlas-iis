package com.atlas.iis.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EquipSlot {

   HAT("Cp", -1),
   SPECIAL_HAT("HrCp", -1),
   FACE_ACCESSORY("Af", -2),
   EYE_ACCESSORY("Ay", -3),
   EARRINGS("Ae", -4),
   TOP("Ma", -5),
   OVERALL("MaPn", -5),
   PANTS("Pn", -6),
   SHOES("So", -7),
   GLOVES("GlGw", -8),
   CASH_GLOVES("Gv", -8),
   CAPE("Sr", -9),
   SHIELD("Si", -10),
   WEAPON("Wp", -11),
   WEAPON_2("WpSi", -11),
   LOW_WEAPON("WpSp", -11),
   RING("Ri", -12, -13, -15, -16),
   PENDANT("Pe", -17),
   TAMED_MOB("Tm", -18),
   SADDLE("Sd", -19),
   MEDAL("Me", -49),
   BELT("Be", -50),
   PET_EQUIP;

   private String wzSlot;
   private int[] slots;

   EquipSlot() {
   }

   EquipSlot(String wzSlot, int... slots) {
      this.wzSlot = wzSlot;
      this.slots = slots;
   }

   public String wzSlot() {
      return wzSlot;
   }

   public List<Integer> slots() {
      return Arrays.stream(slots).boxed().collect(Collectors.toList());
   }
}
