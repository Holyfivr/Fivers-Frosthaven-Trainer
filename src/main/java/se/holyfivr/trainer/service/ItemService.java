package se.holyfivr.trainer.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

        if (existingItem == null) {
            return;
        }
        // Copy only the fields the form actually submitted (non-null). A hidden row or a
        // disabled select (e.g. Infuse/Consumes set to 'Any', or an Attack field on a
        // Damage-only item) arrives null and must NOT overwrite the existing value —
        // previously that blanked those attributes on every save.
        copy(item.getCost(),         existingItem::setCost);
        copy(item.getTotalInGame(),  existingItem::setTotalInGame);
        copy(item.getUsage(),        existingItem::setUsage);
        copy(item.getProsperReq(),   existingItem::setProsperReq);
        copy(item.getConsumes(),     existingItem::setConsumes);
        copy(item.getInfuse(),       existingItem::setInfuse);
        copy(item.getHeal(),         existingItem::setHeal);
        copy(item.getAttack(),       existingItem::setAttack);
        copy(item.getDamage(),       existingItem::setDamage);
        copy(item.getRange(),        existingItem::setRange);
        copy(item.getTarget(),       existingItem::setTarget);
        copy(item.getShield(),       existingItem::setShield);
        copy(item.getRetaliate(),    existingItem::setRetaliate);
        copy(item.getMove(),         existingItem::setMove);
        copy(item.getOMove(),        existingItem::setOMove);
        copy(item.getAMove(),        existingItem::setAMove);
        copy(item.getPull(),         existingItem::setPull);
        copy(item.getPush(),         existingItem::setPush);
        copy(item.getJump(),         existingItem::setJump);
        copy(item.getShieldValue(),  existingItem::setShieldValue);

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

    /* Applies a submitted value only if it was actually present (non-null),   */
    /* so hidden/disabled form fields leave the existing value untouched.      */
    private void copy(String value, Consumer<String> setter) {
        if (value != null) {
            setter.accept(value);
        }
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

        // "Unchanged" is the default for the dropdown fields (Usage/Consumes/Infuse). Blank/0
        // are the "leave alone" values for the numeric fields. Skip all of these so an untouched
        // field never overwrites every item (important now that word values are actually written).
        if (value == null || value.isBlank() || value.equals("0") || value.equalsIgnoreCase("Unchanged")) {
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
            case SET_CONSUMES           ->  updateAllElementItems(Item::getConsumes, Item::setConsumes, value);
            case SET_INFUSE             ->  updateAllElementItems(Item::getInfuse,   Item::setInfuse,   value);
        }
    }

    private void updateAllItems(Function<Item, String> getter, BiConsumer<Item, String> setter, String value) {
        for (Item item : activeSessionData.getItems().values()) {
            if (getter.apply(item) != null) {
                setter.accept(item, value);
            }
        }
    }

    /* ============================================================================ */
    /* Mass-updates element attributes (Consumes/Infuse), but only for items whose  */
    /* current value is a single, non-'Any' element. Items set to 'Any' or an array */
    /* are left untouched, because switching those to a specific element breaks the  */
    /* item in-game (the single-card editor guards this with JS; here we enforce it   */
    /* server-side so a mass edit can't corrupt them).                               */
    /* ============================================================================ */
    private void updateAllElementItems(Function<Item, String> getter, BiConsumer<Item, String> setter, String value) {
        for (Item item : activeSessionData.getItems().values()) {
            String current = getter.apply(item);
            if (current == null || current.equalsIgnoreCase("Any") || current.contains(",")) {
                continue;
            }
            setter.accept(item, value);
        }
    }

}
