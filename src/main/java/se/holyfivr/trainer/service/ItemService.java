package se.holyfivr.trainer.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.Item;

@Service
public class ItemService {

    private ActiveSessionData activeSessionData;

    public ItemService(ActiveSessionData activeSessionData) {
        this.activeSessionData = activeSessionData;

    }

    public Map<String, Item> getItemsByType(String itemType) {
        Map<String, Item> itemList = activeSessionData.getItems();
        if ("all".equals(itemType)) {
            return itemList;
        }
        Map<String, Item> itemTypeList = new LinkedHashMap<>();
        for (Item item : itemList.values()) {
            if (item.slot != null && item.slot.equals(itemType)) {
                itemTypeList.putIfAbsent((item.getItemName()), item);
            }
        }
        return itemTypeList;
    }

    public void saveItem(Item existingItem, Item item) {

        if (existingItem != null) {
            existingItem.setCost(item.getCost());
            existingItem.setTotalInGame(item.getTotalInGame());
            existingItem.setUsage(item.getUsage());
            existingItem.setProsperityRequirement(item.getProsperityRequirement());
            existingItem.setConsumes(item.getConsumes());
            existingItem.setInfuses(item.getInfuses());
            existingItem.setHeal(item.getHeal());
            existingItem.setAttack(item.getAttack());
            existingItem.setRange(item.getRange());
            existingItem.setTarget(item.getTarget());
            existingItem.setShield(item.getShield());
            existingItem.setRetaliate(item.getRetaliate());
            existingItem.setMove(item.getMove());
            existingItem.setOMove(item.getOMove());
            existingItem.setAMove(item.getAMove());
            existingItem.setPull(item.getPull());
            existingItem.setPush(item.getPush());
            existingItem.setJump(item.getJump());
            existingItem.setShieldValue(item.getShieldValue());
        }

        /* This is disabled for the forseeable future, until I can implement */
        /* an "upgraded" version of the filler-banks that can use bytes from */
        /* other blocks to avoid data corruption. */
        /*
         * if (conditions != null && !conditions.isEmpty()) {
         * existingItem.setConditions(conditions.toString());
         * } else {
         * existingItem.setConditions(null);
         * }
         */

    }

}
