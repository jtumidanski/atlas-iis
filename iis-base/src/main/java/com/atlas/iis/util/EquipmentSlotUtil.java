package com.atlas.iis.util;

public final class EquipmentSlotUtil {
   private EquipmentSlotUtil() {
   }

   public static EquipSlot getFromTextSlot(String slot) {
      if (!slot.isEmpty()) {
         for (EquipSlot c : EquipSlot.values()) {
            if (c.wzSlot() != null) {
               if (c.wzSlot().equals(slot)) {
                  return c;
               }
            }
         }
      }
      return EquipSlot.PET_EQUIP;
   }
}
