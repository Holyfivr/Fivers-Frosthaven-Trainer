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

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.service.ItemService;


@Controller
public class ItemController {

    private ActiveSessionData activeSessionData;
    private ItemService itemService;


    public ItemController(ActiveSessionData activeSessionData, ItemService itemService) {
        this.activeSessionData = activeSessionData;
        this.itemService = itemService;
    }

    @GetMapping("/items/{itemType}")
    public String showItemPage(Model model, @PathVariable("itemType") String itemType) {
       
        Map<String, Item> itemTypeList = itemService.getItemsByType(itemType);
        model.addAttribute("category", itemType);
        model.addAttribute("itemList", itemTypeList);

        return "items";
    }

    @GetMapping("/item-details/{itemId}")
    public String showItemDetails(Model model, @PathVariable("itemId") String itemId) {

        Item item = activeSessionData.getItems().get(itemId);
        model.addAttribute("item", item);

        return "item-details";
    }

    @PostMapping("/save-item")
    public String saveItem(@ModelAttribute Item item, @RequestParam(value = "conditions", required = false) List<String> conditions) {
       
        Item existingItem = activeSessionData.getItems().get(item.getStringId());
        itemService.saveItem(existingItem, item);

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
            case "setRange":
                activeSessionData.setRange(value);
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

