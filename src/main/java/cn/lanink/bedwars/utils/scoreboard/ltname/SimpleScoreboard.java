package cn.lanink.bedwars.utils.scoreboard.ltname;

import cn.lanink.bedwars.utils.scoreboard.base.IScoreboard;
import cn.nukkit.Player;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class SimpleScoreboard implements IScoreboard {

    private final ConcurrentHashMap<Player, Scoreboard> scoreboards = new ConcurrentHashMap<>();

    @Override
    public String getScoreboardName() {
        return "lt-name(cn.lanink.gamecore.scoreboard.ltname)";
    }

    @Override
    public void showScoreboard(Player player, String title, List<String> message) {
        Scoreboard scoreboard = this.scoreboards.getOrDefault(player,
                new Scoreboard(
                        title,
                        title,
                        ScoreboardData.DisplaySlot.SIDEBAR,
                        ScoreboardData.SortOrder.ASCENDING));
        scoreboard.clearAllLine();
        int line = 0;
        for (String string : message) {
            scoreboard.setLine(line, string, line);
            line++;
        }
        scoreboard.show(player);
        scoreboard.updateDisplayLine();
        this.scoreboards.put(player, scoreboard);
    }

    @Override
    public void closeScoreboard(Player player) {
        if (this.scoreboards.containsKey(player)) {
            this.scoreboards.get(player).hide(player);
            this.scoreboards.remove(player);
        }
    }

}
