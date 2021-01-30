package cn.lanink.bedwar.tasks.game;

import cn.lanink.bedwar.BedWars;
import cn.lanink.bedwar.room.Room;
import cn.lanink.bedwar.utils.Tools;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.PluginTask;

/**
 * 资源生成
 */
public class MineralTask extends PluginTask<BedWars> {

    private final Room room;
    private int copperSpawnTime;
    private int ironSpawnTime;
    private int goldSpawnTime;
    private int diamondSpawnTime;


    public MineralTask(BedWars owner, Room room) {
        super(owner);
        this.room = room;
        this.copperSpawnTime = room.setCopperSpawnTime;
        this.ironSpawnTime = room.setIronSpawnTime;
        this.goldSpawnTime = room.setGoldSpawnTime;
        this.diamondSpawnTime = room.setDiamondSpawnTime;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 2) {
            Tools.cleanEntity(this.room.getLevel());
            this.cancel();
            return;
        }
        if (this.copperSpawnTime < 1) {
            this.copperSpawnTime = this.room.setCopperSpawnTime;
            for (String spawn : room.getCopperSpawn()) {
                String[] s = spawn.split(":");
                room.getLevel().dropItem(new Vector3(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])),
                        Item.get(336, 0));
            }
        }else {
            this.copperSpawnTime--;
        }
        if (this.ironSpawnTime < 1) {
            this.ironSpawnTime = this.room.setIronSpawnTime;
            for (String spawn : room.getIronSpawn()) {
                String[] s = spawn.split(":");
                room.getLevel().dropItem(new Vector3(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])),
                        Item.get(256, 0));
            }
        }else {
            this.ironSpawnTime--;
        }
        if (this.goldSpawnTime < 1) {
            this.goldSpawnTime = this.room.setGoldSpawnTime;
            for (String spawn : room.getGoldSpawn()) {
                String[] s = spawn.split(":");
                room.getLevel().dropItem(new Vector3(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])),
                        Item.get(266, 0));
            }
        }else {
            this.goldSpawnTime--;
        }
        if (this.diamondSpawnTime < 1) {
            this.diamondSpawnTime = this.room.setDiamondSpawnTime;
            for (String spawn : room.getDiamondSpawn()) {
                String[] s = spawn.split(":");
                room.getLevel().dropItem(new Vector3(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])),
                        Item.get(264, 0));
            }
        }else {
            this.diamondSpawnTime--;
        }
    }

}
