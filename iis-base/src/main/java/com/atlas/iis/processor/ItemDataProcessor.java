package com.atlas.iis.processor;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.atlas.iis.builder.EquipmentBuilder;
import com.atlas.iis.model.Equipment;
import com.atlas.shared.wz.MapleData;
import com.atlas.shared.wz.MapleDataDirectoryEntry;
import com.atlas.shared.wz.MapleDataFileEntry;
import com.atlas.shared.wz.MapleDataProvider;
import com.atlas.shared.wz.MapleDataProviderFactory;
import com.atlas.shared.wz.MapleDataTool;

public class ItemDataProcessor {
   private static final Object lock = new Object();

   private static volatile ItemDataProcessor instance;

   protected MapleDataProvider itemData;

   protected MapleDataProvider equipData;

   protected Map<Integer, Map<String, Integer>> equipStatsCache = new HashMap<>();
   protected Map<Integer, Integer> equipMaxLevelCache = new HashMap<>();
   protected Map<Integer, MapleData> equipLevelInfoCache = new HashMap<>();
   protected Map<Integer, String> equipmentSlotCache = new HashMap<>();

   public static ItemDataProcessor getInstance() {
      ItemDataProcessor result = instance;
      if (result == null) {
         synchronized (lock) {
            result = instance;
            if (result == null) {
               result = new ItemDataProcessor();
               instance = result;
            }
         }
      }
      return result;
   }

   private ItemDataProcessor() {
      itemData = MapleDataProviderFactory.getDataProvider(new File("/service/wz/Item.wz"));
      equipData = MapleDataProviderFactory.getDataProvider(new File("/service/wz/Character.wz"));
   }

   protected <T> Optional<T> getCacheableThing(int itemId, Map<Integer, T> cache, Function<Integer, Optional<T>> supplier) {
      if (cache.containsKey(itemId)) {
         return Optional.of(cache.get(itemId));
      }

      Optional<T> result = supplier.apply(itemId);
      if (result.isPresent()) {
         cache.put(itemId, result.get());
         return result;
      } else {
         return Optional.empty();
      }
   }

   protected Optional<MapleData> getItemData(int itemId) {
      String idStr = "0" + itemId;

      MapleDataDirectoryEntry root = itemData.root();
      for (MapleDataDirectoryEntry topDir : root.subdirectories()) {
         for (MapleDataFileEntry iFile : topDir.files()) {
            if (iFile.name().equals(idStr.substring(0, 4) + ".img")) {
               return itemData.data(topDir.name() + "/" + iFile.name())
                     .flatMap(data -> data.childByPath(idStr));
            } else if (iFile.name().equals(idStr.substring(1) + ".img")) {
               return itemData.data(topDir.name() + "/" + iFile.name());
            }
         }
      }
      root = equipData.root();
      for (MapleDataDirectoryEntry topDir : root.subdirectories()) {
         for (MapleDataFileEntry iFile : topDir.files()) {
            if (iFile.name().equals(idStr + ".img")) {
               return equipData.data(topDir.name() + "/" + iFile.name());
            }
         }
      }
      return Optional.empty();
   }

   public Optional<Map<String, Integer>> getEquipStats(int itemId) {
      return getCacheableThing(itemId, equipStatsCache, this::supplyEquipmentStatistics);
   }

   protected Optional<Map<String, Integer>> supplyEquipmentStatistics(Integer internalItemId) {
      return getItemData(internalItemId)
            .flatMap(item -> item.childByPath("info"))
            .map(this::getEquipmentStatisticsMap);
   }

   protected Map<String, Integer> getEquipmentStatisticsMap(MapleData info) {
      Map<String, Integer> ret = info.children().stream()
            .filter(data -> data.name().startsWith("inc"))
            .collect(Collectors.toMap(data -> data.name().substring(3), MapleDataTool::getIntConvert));
      ret.put("reqJob", MapleDataTool.getInt("reqJob", info, 0));
      ret.put("reqLevel", MapleDataTool.getInt("reqLevel", info, 0));
      ret.put("reqDEX", MapleDataTool.getInt("reqDEX", info, 0));
      ret.put("reqSTR", MapleDataTool.getInt("reqSTR", info, 0));
      ret.put("reqINT", MapleDataTool.getInt("reqINT", info, 0));
      ret.put("reqLUK", MapleDataTool.getInt("reqLUK", info, 0));
      ret.put("reqPOP", MapleDataTool.getInt("reqPOP", info, 0));
      ret.put("cash", MapleDataTool.getInt("cash", info, 0));
      ret.put("tuc", MapleDataTool.getInt("tuc", info, 0));
      ret.put("cursed", MapleDataTool.getInt("cursed", info, 0));
      ret.put("success", MapleDataTool.getInt("success", info, 0));
      ret.put("fs", MapleDataTool.getInt("fs", info, 0));
      return ret;
   }

   public Equipment getEquipById(int equipId) {
      return getEquipById(equipId, -1);
   }

   protected Equipment getEquipById(int equipId, int ringId) {
      EquipmentBuilder builder = new EquipmentBuilder(equipId).setSlots(ringId);

      getEquipStats(equipId)
            .map(Map::entrySet)
            .orElse(Collections.emptySet())
            .forEach(stat -> {
               switch (stat.getKey()) {
                  case "STR" -> builder.setStrength(stat.getValue());
                  case "DEX" -> builder.setDexterity(stat.getValue());
                  case "INT" -> builder.setIntelligence(stat.getValue());
                  case "LUK" -> builder.setLuck(stat.getValue());
                  case "PAD" -> builder.setWeaponAttack(stat.getValue());
                  case "PDD" -> builder.setWeaponDefense(stat.getValue());
                  case "MAD" -> builder.setMagicAttack(stat.getValue());
                  case "MDD" -> builder.setMagicDefense(stat.getValue());
                  case "ACC" -> builder.setAccuracy(stat.getValue());
                  case "EVA" -> builder.setAvoidability(stat.getValue());
                  case "Speed" -> builder.setSpeed(stat.getValue());
                  case "Jump" -> builder.setJump(stat.getValue());
                  case "MHP" -> builder.setHp(stat.getValue());
                  case "MMP" -> builder.setMp(stat.getValue());
                  case "tuc" -> builder.setSlots(stat.getValue());
               }
               //            } else if (isUntradeableRestricted(equipId)) {
               //               builder.orFlag(ItemConstants.UNTRADEABLE);
               //            } else if (stats.get("fs") > 0) {
               //               builder.orFlag(ItemConstants.SPIKES);
               //            }
            });
      return builder.build();
   }

   public int getEquipLevel(int itemId, boolean getMaxLevel) {
      return getCacheableThing(itemId, equipMaxLevelCache, internalItemId -> supplyEquipLevel(itemId, getMaxLevel))
            .orElse(1);
   }

   protected boolean childExistsAndHasMultipleChildren(MapleData data, int index) {
      String path = Integer.toString(index);
      Optional<MapleData> child = data.childByPath(path);
      return child.isPresent() && child.get().children().size() > 1;
   }

   protected Optional<Integer> supplyEquipLevel(int itemId, boolean getMaxLevel) {
      if (getMaxLevel) {
         return getEquipLevelInfo(itemId)
               .map(data -> IntStream.iterate(1, i -> childExistsAndHasMultipleChildren(data, i), i -> i + 1)
                     .count()
               )
               .map(Long::intValue);
      } else {
         return getEquipLevelInfo(itemId)
               .flatMap(data -> data.childByPath("1"))
               .filter(data -> data.children().size() > 1)
               .map(data -> 2);
      }
   }

   private Optional<MapleData> getEquipLevelInfo(int itemId) {
      return getCacheableThing(itemId, equipLevelInfoCache, this::supplyEquipLevelInfo);
   }

   private Optional<MapleData> supplyEquipLevelInfo(int itemId) {
      return getItemData(itemId)
            .flatMap(data -> data.childByPath("info/level"))
            .flatMap(data -> data.childByPath("info"));
   }

   public Optional<String> getEquipmentSlot(int itemId) {
      return getCacheableThing(itemId, equipmentSlotCache, this::supplyEquipmentSlot);
   }

   protected Optional<String> supplyEquipmentSlot(Integer internalItemId) {
      return getItemData(internalItemId)
            .flatMap(item -> item.childByPath("info"))
            .map(info -> MapleDataTool.getString("islot", info, ""));
   }
}
