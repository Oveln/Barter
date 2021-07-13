package me.oveln.barter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import utils.CU;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class barter implements CommandExecutor, TabCompleter {
    private static final String[] HelpMessagee = {"/barter shop 打开商店"};
    private static final String[] HelpMessageOP = {"/barter createpacket 物品包名称    创建物品包",
                                                   "/barter packetlist               物品包列表",
                                                   "/barter querypacket 物品包名称     查询物品包",
                                                   "/barter barterlist               查询交易列表",
                                                   "/barter addbarter 交易包 被交易包   创建交易",
                                                   "/barter delbarter 交易ID",
                                                   "被交易包指玩家将获得的包，交易包指玩家将失去的包"};

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                CU.Sendstrings((Player) sender , HelpMessagee);
                if (sender.isOp()) CU.Sendstrings((Player) sender , HelpMessageOP);
                return true;
            }
            if (args.length == 1 && args[0].equals("shop")) {
                Inventory gui = GUI.getShopGUI(0,sender.getName());
                ((Player) sender).openInventory(gui);
            }
            if (args.length == 1 && sender.isOp()) {
                if (args[0].equals("createpacket") || args[0].equals("querypacket"))
                    sender.sendMessage(CU.t("&c请输入物品包名称"));
                if (args[0].equals("delbarter"))
                    sender.sendMessage(CU.t("&c请输入交易ID"));
                if (args[0].equals("packetlist")) {
                    Set<String> names = main.getInstance().getMap().keySet();
                    sender.sendMessage(CU.t("&a物品包列表"));
                    for (String i:names) sender.sendMessage(i);
                }
                if (args[0].equals("barterlist")) {
                    List<packetpair> barters = main.getInstance().getBarters();
                    sender.sendMessage(CU.t("&a交易列表"));
                    for (int i=0;i<barters.size();i++)
                        sender.sendMessage(CU.t("&aID:"+i+"          &c"+barters.get(i).getX()+"&f兑换&c"+barters.get(i).getY()+""));
                }
                return true;
            }
            if (args.length == 2 && sender.isOp()) {
                if (args[0].equals("querypacket")) {
                    querypacket((Player) sender , args[1]);
                }
                if (args[0].equals("createpacket")) {
                    createpacket((Player) sender , args[1]);
                }
                if (args[0].equals("delbarter")) {
                    if (!CU.isInteger(args[1])) {
                        sender.sendMessage(CU.t("&c你他妈这是数字？？？？"));
                        return true;
                    }
                    delbarter((Player) sender , Integer.valueOf(args[1]));
                }
                return true;
            }
            if (args.length == 3 && sender.isOp()) {
                if (args[0].equals("addbarter")) {
                    addbarter((Player) sender, args[1], args[2]);
                    return true;
                }
            }
            sender.sendMessage(CU.t("&c请输入正确的指令"));
            return true;
        } else {
            System.out.println(CU.t("&c该指令只能由玩家执行哦"));
            return true;
        }
    }

    private void createpacket(Player player , String key) {
        if (!main.getInstance().getMap().containsKey(CU.t(key))) {
            Inventory gui = Bukkit.createInventory(null, 2 * 9, CU.t("&e创建物品包"));
            ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName("");
            item.setItemMeta(meta);

            for (int i = 0; i < 9; i++) gui.setItem(i, item);

            meta.setDisplayName(CU.t("&2确认"));
            item.setType(Material.FIREWORK_CHARGE);
            item.setItemMeta(meta);
            gui.setItem(8, item);

            meta.setDisplayName(CU.t(key));
            item.setType(Material.GOLD_NUGGET);
            item.setItemMeta(meta);
            gui.setItem(4, item);

            player.openInventory(gui);
        }else player.sendMessage(CU.t("&c早就有了这个物品包！！屑！"));
    }

    private void querypacket(Player player , String key) {
        if (main.getInstance().getMap().containsKey(CU.t(key))) {
            Inventory gui = Bukkit.createInventory(null ,2*9,CU.t("&e查询物品包"));
            ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("");
            item.setItemMeta(meta);
            List<ItemStack> packet = main.getInstance().getMap().get(CU.t(key));

            for (int i=0;i<9;i++) gui.setItem(i,item);

            meta.setDisplayName(CU.t("&c删除"));
            item.setType(Material.FIREWORK_CHARGE);
            item.setItemMeta(meta);
            gui.setItem(8,item);

            meta.setDisplayName(CU.t(key));
            item.setType(Material.GOLD_NUGGET);
            item.setItemMeta(meta);
            gui.setItem(4,item);

            for (int i = 0; i< (long) packet.size(); i++) gui.setItem(i+9,packet.get(i));

            player.openInventory(gui);
        } else player.sendMessage(CU.t("&c没有这个物品包！！屑！"));
    }

    private void addbarter(Player player , String x , String y) {
        if (x.equals(y)) player.sendMessage(CU.t("&c我自己交易我自己？你是什么牛马"));
        String xkey = CU.t(x) , ykey = CU.t(y);
        packetpair newbarter = new packetpair(xkey,ykey);
        if (main.getInstance().getMap().containsKey(xkey) && main.getInstance().getMap().containsKey(ykey)) {
            if (!main.getInstance().getBarters().contains(newbarter)) {
                main.getInstance().AddBarter(newbarter);
                player.sendMessage(CU.t("&2创建&f"+xkey+"&2兑换&f"+ykey+"&2的交易成功"));
            } else player.sendMessage(CU.t("&c已经有这个交易了！呆瓜"));
        } else player.sendMessage(CU.t("&c物品包都没有，你隔这无中生有呢"));
    }

    private void delbarter(Player player ,Integer id) {
        if (main.getInstance().getBarters().size()>id && id>=0) {
            main.getInstance().DelBarter(id);
            player.sendMessage(CU.t("&2你删除了ID为&f"+id+"&2的交易(别的ID也发生改变了哦)"));
        }else player.sendMessage(CU.t("&cc我这个暴脾气，你他妈没有的交易你删个头"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label,String[] args) {

        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("shop");
            if (sender.isOp()) {
                list.add("createpacket");
                list.add("packetlist");
                list.add("querypacket");
                list.add("addbarter");
                list.add("delbarter");
            }
        }
        if (args.length == 2) {
            if (sender.isOp())
                if (args[0].equals("createpacket") || args[0].equals("querypacket"))
                    list.add("物品包名称");
                if (args[0].equals("addbarter") || args[0].equals("delbarter"))
                    list.add("交易ID");
        }
        return list;
    }
}
