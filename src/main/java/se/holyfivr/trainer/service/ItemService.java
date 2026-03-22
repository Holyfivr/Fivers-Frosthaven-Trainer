package se.holyfivr.trainer.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import se.holyfivr.trainer.model.enums.ItemAction;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.Item;

@Service
public class ItemService {

    private final ActiveSessionData activeSessionData;

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
            existingItem
                    .setCost                 (item.getCost                 ())
                    .setTotalInGame          (item.getTotalInGame          ())
                    .setUsage                (item.getUsage                ())
                    .setProsperReq           (item.getProsperReq           ())
                    .setConsumes             (item.getConsumes             ())
                    .setInfuse               (item.getInfuse               ())
                    .setHeal                 (item.getHeal                 ())
                    .setAttack               (item.getDamage               ())
                    .setDamage               (item.getDamage               ())
                    .setRange                (item.getRange                ())
                    .setTarget               (item.getTarget               ())
                    .setShield               (item.getShield               ())
                    .setRetaliate            (item.getRetaliate            ())
                    .setMove                 (item.getMove                 ())
                    .setOMove                (item.getOMove                ())
                    .setAMove                (item.getAMove                ())
                    .setPull                 (item.getPull                 ())
                    .setPush                 (item.getPush                 ())
                    .setJump                 (item.getJump                 ())
                    .setShieldValue          (item.getShieldValue          ());
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

    /* ============================================ */
    /* Updates all items with the received value.   */
    /*                                              */
    /* Uses functions and biconsumer to dynamically */
    /* set values, and avoid having 15 identical    */
    /* setters with loops+nullchecks in the         */
    /* ActiveSessionData class.                     */
    /* ============================================ */
    
    public void updateAllItems(String action, String value) {

        if (value == null || value.isBlank() || value.equals("0")) {
            return;
        }

        ItemAction itemAction = ItemAction.fromString(action);
        System.err.println("Action: " + itemAction + " | Value: " + value);

        switch (itemAction) {
            case SET_GOLD_COST          ->  updateAllItems(Item::getCost,        Item::setCost,          value);
            case SET_RANGE              ->  updateAllItems(Item::getRange,       Item::setRange,         value);
            case SET_HEAL               ->  updateAllItems(Item::getHeal,        Item::setHeal,          value);
            case SET_RETALIATE          ->  updateAllItems(Item::getRetaliate,   Item::setRetaliate,     value);
            case SET_USAGE              ->  updateAllItems(Item::getUsage,       Item::setUsage,         value);
            case SET_TOTAL_IN_GAME      ->  updateAllItems(Item::getTotalInGame, Item::setTotalInGame,   value);
            case SET_XP                 ->  updateAllItems(Item::getXp,          Item::setXp,            value);
            case SET_TARGET             ->  updateAllItems(Item::getTarget,      Item::setTarget,        value);
            case SET_PIERCE             ->  updateAllItems(Item::getPierce,      Item::setPierce,        value);
            case SET_DAMAGE             -> {updateAllItems(Item::getAttack,      Item::setAttack,        value);
                                            updateAllItems(Item::getDamage,      Item::setDamage,        value);}
            case SET_SHIELD             -> {updateAllItems(Item::getShield,      Item::setShield,        value);
                                            updateAllItems(Item::getShieldValue, Item::setShieldValue,   value);}
            case SET_MOVEMENT           -> {updateAllItems(Item::getMove,        Item::setMove,          value);
                                            updateAllItems(Item::getOMove,       Item::setOMove,         value);
                                            updateAllItems(Item::getAMove,       Item::setAMove,         value);}
            case SET_PROSPERITY_REQ     ->  activeSessionData.setProsperity(value); 
        }
    }

    private void updateAllItems(Function<Item, String> getter, BiConsumer<Item, String> setter, String value) {
        for (Item item : activeSessionData.getItems().values()) {
            if (getter.apply(item) != null) {
                setter.accept(item, value);
            }
        }
    }

}
