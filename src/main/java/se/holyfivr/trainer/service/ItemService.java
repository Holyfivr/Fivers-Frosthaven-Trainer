package se.holyfivr.trainer.service;

import java.util.LinkedHashMap;
import java.util.Map;
import se.holyfivr.trainer.model.enums.ItemAction;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.Item;

@Service
public class ItemService {

    private ActiveSessionData activeSessionData;

    public ItemService(ActiveSessionData activeSessionData) {
        this.activeSessionData = activeSessionData;

    }


    /* =================================================== */
    /* Retrieves a map of all items of a specific category */
    /* =================================================== */
    public Map<String, Item> getItemsByType(String itemType) {
        Map<String, Item> itemMap = activeSessionData.getItems();
        if ("all".equals(itemType)) {
            return itemMap;
        }
        Map<String, Item> itemTypeList = new LinkedHashMap<>();
        for (Item item : itemMap.values()) {
            if (item.getSlot() != null && item.getSlot().equals(itemType)) {
                itemTypeList.putIfAbsent((item.getItemName()), item);
            }
        }
        return itemTypeList;
    }

    /* ================================================================= */
    /* Saves the specific item, when edited from it's specific item-page */
    /* ================================================================= */
    public void saveItem(Item existingItem, Item item) {

        if (existingItem != null) {
            existingItem.setCost(item.getCost())
                    .setTotalInGame(item.getTotalInGame())
                    .setUsage(item.getUsage())
                    .setProsperityRequirement(item.getProsperityRequirement())
                    .setConsumes(item.getConsumes())
                    .setInfuse(item.getInfuse())
                    .setHeal(item.getHeal())
                    .setAttack(item.getAttack())
                    .setRange(item.getRange())
                    .setTarget(item.getTarget())
                    .setShield(item.getShield())
                    .setRetaliate(item.getRetaliate())
                    .setMove(item.getMove())
                    .setOMove(item.getOMove())
                    .setAMove(item.getAMove())
                    .setPull(item.getPull())
                    .setPush(item.getPush())
                    .setJump(item.getJump())
                    .setShieldValue(item.getShieldValue());
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

    /* ========================================== */
    /* Updates all items with the received value. */
    /* ========================================== */
    public void updateAllItems(String action, String value) {

        if (value == null || value.isBlank() || value.equals("0")) {
            return;
        }

        ItemAction itemAction = ItemAction.fromString(action);
        System.err.println("Action: " + itemAction + " | Value: " + value);

        switch (itemAction) {
            case SET_GOLD_COST:
                activeSessionData.setGold(value);
                break;
            case SET_DAMAGE:
                activeSessionData.setAttack(value);
                break;
            case SET_RANGE:
                activeSessionData.setRange(value);
                break;
            case SET_HEAL:
                activeSessionData.setHeal(value);
                break;
            case SET_RETALIATE:
                activeSessionData.setRetaliate(value);
                break;
            case SET_PROSPERITY_REQ:
                activeSessionData.setProsperity(value);
                break;
            case SET_USAGE:
                activeSessionData.setUsage(value);
                break;
            case SET_TOTAL_IN_GAME:
                activeSessionData.setTotalInGame(value);
                break;
            case SET_SHIELD:
                activeSessionData.setShield(value);
                activeSessionData.setShieldValue(value);
                break;
            case SET_MOVEMENT:
                activeSessionData.setMovement(value);
                activeSessionData.setOMove(value);
                activeSessionData.setAMove(value);
                break;
            default:
                break;
        }
    }
}
