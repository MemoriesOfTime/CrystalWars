package cn.lanink.bedwars.scoreboard.ltname.packet;

import cn.nukkit.network.protocol.DataPacket;

/**
 * 参考项目：
 * https://github.com/Creeperface01/ScoreboardAPI
 * https://github.com/LucGamesYT/ScoreboardAPI
 */
public class SetObjectivePacket extends DataPacket {

    public static final byte NETWORK_ID = 0x6b;

    public String displaySlot;
    public String objectiveName;
    public String displayName;
    public String criteriaName;
    public int sortOrder;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.displaySlot);
        this.putString(this.objectiveName);
        this.putString(this.displayName);
        this.putString(this.criteriaName);
        this.putVarInt(this.sortOrder);
    }

    @Override
    public String toString() {
        return "SetObjectivePacket(displaySlot=" + this.displaySlot +
                ", objectiveName=" + this.objectiveName +
                ", displayName=" + this.displayName +
                ", criteriaName=" + this.criteriaName +
                ", sortOrder=" + this.sortOrder + ")";
    }

}
