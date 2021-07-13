package me.oveln.barter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import utils.CU;


public class InventoryClose implements Listener {
    @EventHandler
    public boolean onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getName().equals(CU.t("&e创建物品包"))) {
            event.getPlayer().sendMessage(CU.t("&c你退出了创建物品包，所以什么都没有发生"));
        }
        return true;
    }
}
