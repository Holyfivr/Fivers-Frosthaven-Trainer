package se.holyfivr.trainer.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.service.ActiveSessionData;


@Controller
public class ItemController {

    ActiveSessionData activeSessionData;

    public ItemController(ActiveSessionData activeSessionData) {
        this.activeSessionData = activeSessionData;
    }

    @GetMapping("/items/{itemType}")
    public String showItemPage(Model model, @PathVariable("itemType") String itemType) {
       Map<String, Item> itemList = activeSessionData.getItems();

        if (itemType.equals("all")) {
            model.addAttribute("category", "All Items");
            model.addAttribute("itemList", itemList);
            return "items";
        }
        
        Map<String, Item> itemTypeList = new LinkedHashMap<>();
        for (Item item : itemList.values()) {
            if (item.slot != null && item.slot.equals(itemType)) {
                itemTypeList.putIfAbsent(item.getItemName(), item);
            }
        }

        model.addAttribute("category", itemType);
        model.addAttribute("itemList", itemTypeList);
        return "items";
    }

    @GetMapping("/item-details/{itemId}")
    public String showItemDetails(Model model, @PathVariable("itemId") String itemId) {
        System.err.println(itemId);
        Item item = activeSessionData.getItems().get(itemId);
        model.addAttribute("item", item);
        return "item-details";
    }

    @PostMapping("/save-item")
    public String saveItem(@ModelAttribute Item item, @RequestParam(value = "conditions", required = false) List<String> conditions) {
        Item existingItem = activeSessionData.getItems().get(item.getStringId());

        if (existingItem != null) {
            existingItem.setCost                    (item.getCost());
            existingItem.setTotalInGame             (item.getTotalInGame());
            existingItem.setUsage                   (item.getUsage());
            existingItem.setProsperityRequirement   (item.getProsperityRequirement());
            existingItem.setConsumes                (item.getConsumes());
            existingItem.setInfuses                 (item.getInfuses());
            existingItem.setHeal                    (item.getHeal());
            existingItem.setAttack                  (item.getAttack());
            existingItem.setRange                   (item.getRange());
            existingItem.setTarget                  (item.getTarget());
            existingItem.setShield                  (item.getShield());
            existingItem.setRetaliate               (item.getRetaliate());
            existingItem.setMove                    (item.getMove());
            existingItem.setOMove                   (item.getOMove());
            existingItem.setAMove                   (item.getAMove());
            existingItem.setPull                    (item.getPull());
            existingItem.setPush                    (item.getPush());
            existingItem.setJump                    (item.getJump());
            existingItem.setShieldValue             (item.getShieldValue());
            
            


            /* This is disabled for the forseeable future, until I can implement */
            /* an "upgraded" version of the filler-banks that can use bytes from */
            /* other blocks to avoid data corruption.                            */
            /* 
            if (conditions != null && !conditions.isEmpty()) {
                existingItem.setConditions(conditions.toString());
            } else {
                existingItem.setConditions(null);
            } */

                
        }

        return "redirect:/item-details/" + item.getStringId();
    }
    

    @GetMapping("/set-item-value")
    public String setItemValues(@RequestParam String action, @RequestParam String value) {
        System.err.println("Action: " + action + " | Value: " + value);
        switch (action) {
            case "setGoldCost":
                activeSessionData.setGold(value);
                break;
            case "setDamage":
                activeSessionData.setAttack(value);
                break;
            case "setShield":
                activeSessionData.setShield(value);
                activeSessionData.setShieldValue(value);
                break;
            case "setHeal":
                activeSessionData.setHeal(value);
                break;
            case "setRetaliate":
                activeSessionData.setRetaliate(value);
                break;
            case "setMovement":
                activeSessionData.setMovement(value);
                activeSessionData.setOMove(value);
                activeSessionData.setAMove(value);
                break;
            case "setProsperityReq":
                activeSessionData.setProsperity(value);
                break;
            case "setUsage":
                activeSessionData.setUsage(value);
                break;
            case "setTotalInGame":
                activeSessionData.setTotalInGame(value);
                break;

            default:
                break;
        }
        
        return "redirect:/items/all";
    }
    

}

