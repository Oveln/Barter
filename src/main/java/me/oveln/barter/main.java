package me.oveln.barter;

import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import utils.CU;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class main extends JavaPlugin {

    private PluginDescriptionFile descriptionFile;
    private static main instance;
    private String PacketsPath , BartersPath;

    private Map<String,List<ItemStack>> packets;
    private List<packetpair> barters;

    @Override
    public void onEnable() {
        descriptionFile = getDescription();
        instance = this;
        packets = new HashMap<>();
        barters = new ArrayList<>();
        PacketsPath = "plugins/"+descriptionFile.getName()+"/packets.db";
        BartersPath = "plugins/"+descriptionFile.getName()+"/barters.db";

        saveDefaultConfig();

        try {
            ReadPackets();
            ReadBarters();
        } catch(IOException i) {
            CheckFile();
        }
        getServer().getPluginManager().registerEvents(new InventoryClick() , this);
        getCommand("barter").setExecutor(new barter());

        String appends =
                descriptionFile.getName() +
                descriptionFile.getVersion() +
                "启动完成 &c作者" +
                descriptionFile.getAuthors().get(0);
        System.out.print(CU.t(appends));
    }

    @Override
    public void onDisable() {

        try {
            SavePackets();
            SaveBarters();
        } catch(IOException i) {
            i.printStackTrace();
        }

        String appends =
                descriptionFile.getName() +
                "关闭完成";
        System.out.print(appends);
    }

    public static main getInstance() {return instance;}

//    public void SavePackets() throws IOException {
//        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PacketsPath));
//        Map<String , List<String>> savefile = new HashMap<>();
//        for (String key:packets.keySet()) {
//            List<ItemStack> packet = packets.get(key);
//            List<String> mappacket = new ArrayList<>();
//            for (ItemStack item:packet) mappacket.add(CraftItemStack.asNMSCopy(item).toString());
//            savefile.put(key , mappacket);
//        }
//        out.writeObject(savefile);
//        out.flush();
//        out.close();
//        System.out.print("保存了"+savefile.keySet().size()+"个物品包");
//    }
//
//    public void ReadPackets() throws IOException {
//        FileInputStream file = new FileInputStream(PacketsPath);
//        if (file.available() == 0) return;
//        ObjectInputStream in = new ObjectInputStream(new FileInputStream(PacketsPath));
//        Map<String , List<String>> savefile = new HashMap<>();
//        try {
//            savefile = (Map<String , List<String>>) in.readObject();
//        }catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        for (String key:savefile.keySet()) {
//            List<ItemStack> packet = new ArrayList<>();
//            List<String> mappacket = savefile.get(key);
//            for (String item:mappacket) {
//                net.minecraft.server.v1_8_R3.ItemStack Item = new net.minecraft.server.v1_8_R3.ItemStack();
//                Item.
//                packet.add();
//            }
//            packets.put(key,packet);
//        }
//        in.close();
//        System.out.print("读取了"+savefile.keySet().size()+"个物品包");
//    }

    public void SavePackets() throws IOException {
        NBTTagCompound NBTPackets = new NBTTagCompound();
        for (String key : packets.keySet()) {
            NBTTagList items = new NBTTagList();
            for (ItemStack item : packets.get(key)) {
                NBTTagCompound nbt = new NBTTagCompound();
                CraftItemStack.asNMSCopy(item).save(nbt);
                items.add(nbt);
            }
            NBTPackets.set(key , items);
        }
        NBTCompressedStreamTools.a(NBTPackets , new FileOutputStream(PacketsPath));
        System.out.print("保存了"+packets.size()+"个物品包");
    }

    public void ReadPackets() throws IOException {
        NBTTagCompound NBTPackets = NBTCompressedStreamTools.a(new FileInputStream(PacketsPath));
        for (String key : NBTPackets.c()) {
            List<ItemStack> packet = new ArrayList<>();
            NBTTagList NBTPacket = (NBTTagList) NBTPackets.get(key);
            for (int i = 0;i<NBTPacket.size();i++)
                packet.add(CraftItemStack.asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack.createStack(NBTPacket.get(i))));
            packets.put(key , packet);
        }
        System.out.print("读取了"+packets.size()+"个物品包");
    }

    public void SaveBarters() throws IOException{
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BartersPath));
        out.writeObject(barters);
        out.flush();
        out.close();
        System.out.print("保存了"+barters.size()+"个交易");
    }

    public void ReadBarters() throws IOException {
        FileInputStream file = new FileInputStream(BartersPath);
        if (file.available() == 0) return;
        ObjectInputStream in = new ObjectInputStream(file);
        try {
            barters = (List<packetpair>) in.readObject();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        in.close();
        System.out.print("读取了"+barters.size()+"个交易");
    }

    public void CheckFile() {
        try {
            File file = new File(PacketsPath);
            file.createNewFile();
            file = new File(BartersPath);
            file.createNewFile();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String,List<ItemStack>> getMap() {return packets;}

    public void AddPacket(String key , List<ItemStack> packet) {
        packets.put(key,packet);
    }

    public void DelPacket(String key) {
        packets.remove(key);
    }

    public List<packetpair> getBarters() {return barters;}

    public void AddBarter(packetpair x) {
        barters.add(x);
    }

    public void DelBarter(Integer x) {
        barters.remove((int) x);
    }

}
