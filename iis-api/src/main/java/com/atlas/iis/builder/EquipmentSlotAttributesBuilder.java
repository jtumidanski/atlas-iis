package com.atlas.iis.builder;

import com.app.common.builder.RecordBuilder;
import com.atlas.iis.attribute.EquipmentSlotAttributes;

import builder.AttributeResultBuilder;

public class EquipmentSlotAttributesBuilder extends RecordBuilder<EquipmentSlotAttributes, EquipmentSlotAttributesBuilder>
      implements AttributeResultBuilder {
   private String name;

   private String wz;

   private Short slot;

   @Override
   public EquipmentSlotAttributes construct() {
      return new EquipmentSlotAttributes(name, wz, slot);
   }

   @Override
   public EquipmentSlotAttributesBuilder getThis() {
      return this;
   }

   public EquipmentSlotAttributesBuilder setName(String name) {
      this.name = name;
      return getThis();
   }

   public EquipmentSlotAttributesBuilder setWz(String wz) {
      this.wz = wz;
      return getThis();
   }

   public EquipmentSlotAttributesBuilder setSlot(Short slot) {
      this.slot = slot;
      return getThis();
   }
}
