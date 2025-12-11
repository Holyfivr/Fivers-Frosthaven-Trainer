package se.holyfivr.trainer.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

}
