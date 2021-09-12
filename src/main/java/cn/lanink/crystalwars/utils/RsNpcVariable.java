package cn.lanink.crystalwars.utils;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.nukkit.Player;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariableV2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LT_Name
 */
public class RsNpcVariable extends BaseVariableV2 {

    @Override
    public void onUpdate(Player player, RsNpcConfig rsNpcConfig) {
        HashMap<String, Integer> map = new HashMap<>();
        int allPlayerCount = 0;
        for (BaseArena arena : CrystalWars.getInstance().getArenas().values()) {
            int playerCount = arena.getPlayerCount();
            String key = arena.getGameMode().toLowerCase();
            map.put(key, map.getOrDefault(key, 0) + playerCount);
            allPlayerCount += playerCount;
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            this.addVariable("{CrystalWarsArenaPlayerCountByMode#" + entry.getKey() +  "}", String.valueOf(entry.getValue()));
        }
        this.addVariable("{CrystalWarsArenaPlayerCountByMode#all}", String.valueOf(allPlayerCount));
    }

}
