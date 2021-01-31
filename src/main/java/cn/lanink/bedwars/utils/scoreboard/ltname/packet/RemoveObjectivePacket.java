package cn.lanink.bedwars.utils.scoreboard.ltname.packet;

import cn.nukkit.network.protocol.DataPacket;

/**
 * 参考项目：
 * https://github.com/Creeperface01/ScoreboardAPI
 * https://github.com/LucGamesYT/ScoreboardAPI
 */
public class RemoveObjectivePacket extends DataPacket {

    public static final byte NETWORK_ID = 0x6a;

    public String objectiveName;

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
        this.putString( this.objectiveName );
    }

    @Override
    public String toString() {
        return "RemoveObjectivePacket(objectiveName=" + this.objectiveName + ")";
    }

}
