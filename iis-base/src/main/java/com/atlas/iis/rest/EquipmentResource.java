package com.atlas.iis.rest;

import java.util.Collections;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlas.iis.attribute.EquipmentSlotAttributes;
import com.atlas.iis.builder.EquipmentSlotAttributesBuilder;
import com.atlas.iis.model.Equipment;
import com.atlas.iis.model.EquipmentSlot;
import com.atlas.iis.processor.ItemDataProcessor;
import com.atlas.iis.util.EquipmentSlotUtil;

import builder.ResultBuilder;
import builder.ResultObjectBuilder;

@Path("equipment")
public class EquipmentResource {
   @GET
   @Path("{equipmentId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getEquipment(@PathParam("equipmentId") Integer equipmentId) {
      Equipment equipment = ItemDataProcessor.getInstance().getEquipById(equipmentId);

      return new ResultBuilder()
            .addData(ResultObjectFactory.create(equipment))
            .build();
   }

   @GET
   @Path("{equipmentId}/slots")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getEquipmentSlots(@PathParam("equipmentId") Integer equipmentId) {
      return ItemDataProcessor.getInstance().getEquipmentSlot(equipmentId)
            .map(EquipmentSlotUtil::getFromTextSlot)
            .map(equipSlot -> equipSlot.slots().stream()
                  .map(slot -> new EquipmentSlot(equipSlot.name(), equipSlot.wzSlot(), slot))
                  .collect(Collectors.toList()))
            .orElse(Collections.emptyList())
            .stream()
            .map(equipmentSlot -> new ResultObjectBuilder(EquipmentSlotAttributes.class, 0)
                  .setAttribute(new EquipmentSlotAttributesBuilder()
                        .setName(equipmentSlot.name())
                        .setWz(equipmentSlot.wz())
                        .setSlot(equipmentSlot.slot().shortValue())
                  )
            )
            .collect(com.app.rest.util.stream.Collectors.toResultBuilder())
            .build();
   }
}
