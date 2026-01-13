package se.holyfivr.trainer.controller;

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

    /* ==================================================== */
    /* Handles request to fetch Map depending on itemtypes  */
    /* ==================================================== */
    @GetMapping("/items/{itemType}")
    public String showItemPage(Model model, @PathVariable("itemType") String itemType) {
        Map<String, Item> itemTypeList = itemService.getItemsByType(itemType);
        model.addAttribute("category", itemType);
        model.addAttribute("itemList", itemTypeList);
        return "items";
    }

    /* ================================================================== */
    /* Handles request to fetch specific item to view on the details view */
    /* ================================================================== */
    @GetMapping("/item-details/{itemId}")
    public String showItemDetails(Model model, @PathVariable("itemId") String itemId) {
        Item item = activeSessionData.getItems().get(itemId);
        model.addAttribute("item", item);
        return "item-details";
    }

    /* ====================================================================== */
    /* Handles save item request, to store changes in the active session data */
    /* ====================================================================== */
    @PostMapping("/save-item")
    public String saveItem(@ModelAttribute Item item, @RequestParam(value = "conditions", required = false) List<String> conditions) {
        Item existingItem = activeSessionData.getItems().get(item.getStringId());
        itemService.saveItem(existingItem, item);
        return "redirect:/item-details/" + item.getStringId();
    }

    /* ============================================================ */
    /* Handles request to update all items with the received values */
    /* ============================================================ */
    @GetMapping("/set-all-item-values")
    public String setItemValues(@RequestParam String action, @RequestParam String value) {
        itemService.updateAllItems(action, value);
        return "redirect:/items/all";
    }
    

}

