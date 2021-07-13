package me.oveln.barter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import utils.CU;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class InventoryClick implements Listener {
    @EventHandler
    public boolean onInventoryClick(InventoryClickEvent event) {
        if (event.getSlot()<0) return true;
        if (!(event.getWhoClicked() instanceof Player)) return true;
        String InventoryName = event.getInventory().getName();
        if (InventoryName.equals(CU.t("&e创建物品包")) && event.getSlotType() != InventoryType.SlotType.QUICKBAR) {
           if (event.getSlot() >= 0 && event.getSlot() <= 8) event.setCancelled(true);
           if (event.getSlot() == 8) {
                Inventory gui = event.getInventory();
                ItemStack item = gui.getItem(4);
                String key = item.getItemMeta().getDisplayName();
                ArrayList<ItemStack> packet = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                   item = gui.getItem(i + 9);
                   if (item != null) packet.add(item);
               }
               if (!packet.isEmpty()) {
                   main.getInstance().AddPacket(key, packet);
                   event.getWhoClicked().sendMessage(CU.t("&2你创建了名为" + key + "&2的物品包"));
               } else {
                   event.getWhoClicked().sendMessage(CU.t("&c你没有放入任何物品，所以没有创建物品包"));
               }
               Close((Player) event.getWhoClicked());
           }
           return true;
        }
        if (InventoryName.equals(CU.t("&e查询物品包"))) {
            event.setCancelled(true);
            if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) return true;
            if (event.getSlot() == 8) {
                Inventory gui =event.getInventory();
                String key = gui.getItem(4).getItemMeta().getDisplayName();
                main.getInstance().DelPacket(key);
                event.getWhoClicked().sendMessage(CU.t("&2你删除了名为"+key+"&2的物品包"));
                Close((Player) event.getWhoClicked());
            }
        }
        if (InventoryName.equals(CU.t(main.getInstance().getConfig().getString("ShopTitle")))) {
            event.setCancelled(true);
            if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) return true;
            if (event.getSlot() == 53) {                                      //翻页
                int page = Integer.parseInt(event.getInventory().getItem(49).getItemMeta().getDisplayName().replace("第","").replace("页",""));
                if (main.getInstance().getBarters().size()<=page*45) return true;
                Close(((Player) event.getWhoClicked()).getPlayer());
                Inventory gui = GUI.getShopGUI(page , event.getWhoClicked().getName());
                OpenShop(((Player)event.getWhoClicked()).getPlayer() , gui);
            }
            if (event.getSlot() == 45) {
                int page = Integer.parseInt(event.getInventory().getItem(49).getItemMeta().getDisplayName().replace("第","").replace("页",""));
                if (page==1) return true;
                Close(((Player) event.getWhoClicked()).getPlayer());
                Inventory gui = GUI.getShopGUI(page-2 , event.getWhoClicked().getName());
                OpenShop(((Player)event.getWhoClicked()).getPlayer() , gui);
            }
            if (event.getSlot()<45 && event.getInventory().getItem(event.getSlot()) != null) {
                int page = Integer.parseInt(event.getInventory().getItem(49).getItemMeta().getDisplayName().replace("第","").replace("页",""));
                page--;
                Inventory gui = GUI.getShopingGUI(event.getSlot()+page*45);
                OpenShop((Player)event.getWhoClicked() , gui);
            }
        }
        if (InventoryName.equals(CU.t(main.getInstance().getConfig().getString("ShopingTitle")))) {
            event.setCancelled(true);
            if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) return true;
            if (event.getSlot() == 9*5+3) {
                Close((Player) event.getWhoClicked());
                return true;
            }
            if (event.getSlot() == 9*5+5) {
                int id = Integer.parseInt(event.getInventory().getItem(4).getItemMeta().getDisplayName());
                Player player = (Player) event.getWhoClicked();
                Inventory bag = player.getInventory();
                packetpair barter = main.getInstance().getBarters().get(id);
                List<ItemStack> packet = main.getInstance().getMap().get(barter.getX());
                List<ItemStack> packetY = main.getInstance().getMap().get(barter.getY());
                boolean flag = true;
                for (ItemStack item : packet) {
                    flag = flag && bag.containsAtLeast(item , item.getAmount());
                }
                if (!flag) {
                    player.sendMessage(CU.t("&c你没有足够的材料！"));
                    Close(player);
                    return true;
                }
                for (ItemStack item : packet) {
                    int num = item.getAmount();
                    ListIterator<ItemStack> iterator = bag.iterator(0);
                    while (num != 0) {
                        for (int i=0;i<bag.getSize();i++)
                            if (item.isSimilar(bag.getItem(i))) {
                                int mount = bag.getItem(i).getAmount();
                                if (num<mount) {
                                    bag.getItem(i).setAmount(mount - num);
                                    num =0;
                                } else {
                                    bag.clear(i);
                                    num = num - mount;
                                }
                            }
                    }
                }
                if (!Addpacket(packetY,player)) {
                    if (!Addpacket(packet, player)) System.out.print("??????");
                    player.sendMessage(CU.t("&c你没有足够的空间容纳物品，交易取消"));
                    return true;
                }
                List<String> strings = main.getInstance().getConfig().getStringList("PlayerMessage");
                for (String s:strings) {
                    System.out.print(s);
                    String ret = s.replace("%player%", player.getName()).replace("%packetname%",barter.getY());
                    player.sendMessage(CU.t(ret));
                }
            }
        }
        return true;
    }

    public boolean Addpacket(List<ItemStack> packet , Player player) {
        Inventory bag = player.getInventory();
        int cnt = 0;
        for (int i=0;i<36;i++) if (bag.getItem(i) == null) cnt++;
        if (cnt >= packet.size()) {
            for (ItemStack item :packet) {
                int slot = bag.firstEmpty();
                bag.setItem(slot , item);
            }
        } else return false;
        return true;
    }


    public void Close(Player player) {
        new CloseInventory(player).runTaskLater(main.getInstance(),1);
    }
    public void OpenShop(Player player ,Inventory gui) {
        new OpenInventory(player , gui).runTaskLater(main.getInstance(),1);
    }

    public static class OpenInventory extends BukkitRunnable {
        private final Player player;
        private final Inventory gui;

        public OpenInventory(Player player , Inventory gui) {
            this.player = player;
            this.gui =gui;
        }
        @Override
        public void run() {
            player.openInventory(gui);
        }
    }

    public static class CloseInventory extends BukkitRunnable {
        private final Player player;

        public CloseInventory(Player player) {
            this.player = player;
        }
        @Override
        public void run() {
            player.closeInventory();
        }
    }
}
