package me.oveln.barter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import utils.CU;

import java.util.ArrayList;
import java.util.List;

public class GUI {
    public static Inventory getShopGUI(int Page,String playername) {
        Inventory gui = Bukkit.createInventory(null , 6*9 , CU.t(main.getInstance().getConfig().getString("ShopTitle")));
        ItemStack item = new ItemStack(Material.SIGN);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(CU.t("&e玩家名: &f"+playername));
        meta.setLore(lore);
        meta.setDisplayName("第"+(Page+1)+"页");
        item.setItemMeta(meta);
        gui.setItem(49,item);

        item = new ItemStack(Material.PAPER);
        meta = item.getItemMeta();
        meta.setDisplayName(CU.t("&2下一页"));
        item.setItemMeta(meta);
        gui.setItem(53,item);

        item = new ItemStack(Material.PAPER);
        meta = item.getItemMeta();
        meta.setDisplayName(CU.t("&2上一页"));
        item.setItemMeta(meta);
        gui.setItem(45,item);

        List<packetpair> barters = main.getInstance().getBarters();
        int start = Page*45 , end = Math.min(start+45-1,barters.size()-1) , nowslot = 0;
        for (int i=start;i<=end;i++) {
            gui.setItem(nowslot,main.getInstance().getMap().get(barters.get(i).getY()).get(0));
            nowslot++;
        }
        return gui;
    }

    public static Inventory getShopingGUI(int id) {
        Inventory gui = Bukkit.createInventory(null , 6*9 , CU.t(main.getInstance().getConfig().getString("ShopingTitle")));

        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE , 1, (short)15);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        for (int i=0;i<=8;i++) {
            gui.setItem(i,item);
            gui.setItem(i+9*4,item);
        }
        for (int i=1;i<=3;i++) {
            gui.setItem(i*9,item);
            gui.setItem(i*9+4,item);
            gui.setItem(i*9+8,item);
        }
        item = new ItemStack(Material.GOLD_NUGGET);
        meta = item.getItemMeta();
        meta.setDisplayName(""+id);
        item.setItemMeta(meta);
        gui.setItem(4,item);

        item = new ItemStack(Material.BEDROCK);
        meta = item.getItemMeta();
        meta.setDisplayName(CU.t("&c取消"));
        item.setItemMeta(meta);
        gui.setItem(9*5+3,item);

        item = new ItemStack(Material.EMERALD_BLOCK);
        meta = item.getItemMeta();
        meta.setDisplayName(CU.t("&2确认"));
        item.setItemMeta(meta);
        gui.setItem(9*5+5,item);

        packetpair barter = main.getInstance().getBarters().get(id);
        List<ItemStack> packet = main.getInstance().getMap().get(barter.getX());
        int nowslot = 10;
        for (ItemStack x:packet) {
            gui.setItem(nowslot,x);
            nowslot = nowslot +1;
            if (nowslot%9 > 3) nowslot = (nowslot/9 +1)*9+1;
        }
        packet = main.getInstance().getMap().get(barter.getY());
        nowslot = 14;
        for (ItemStack x:packet) {
            gui.setItem(nowslot,x);
            nowslot = nowslot +1;
            if (nowslot%9 > 7) nowslot = (nowslot/9 +1)*9+5;
        }

        return gui;
    }

}
