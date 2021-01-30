package cn.lanink.bedwars.scoreboard.creeperface;

import cn.lanink.bedwars.scoreboard.base.IScoreboard;
import cn.nukkit.Player;
import gt.creeperface.nukkit.scoreboardapi.ScoreboardAPI;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class SimpleScoreboard implements IScoreboard {

    private final ConcurrentHashMap<Player, gt.creeperface.nukkit.scoreboardapi.scoreboard.SimpleScoreboard> scoreboards = new ConcurrentHashMap<>();

    @Override
    public String getScoreboardName() {
        return "Creeperface01(gt.creeperface.nukkit.scoreboardapi)";
    }

    @Override
    public void showScoreboard(Player player, String title, List<String> message) {
        gt.creeperface.nukkit.scoreboardapi.scoreboard.SimpleScoreboard simpleScoreboard;
        if (!this.scoreboards.containsKey(player)) {
            simpleScoreboard = ScoreboardAPI.builder().build();
        }else {
            simpleScoreboard = this.scoreboards.get(player);
            simpleScoreboard.clearCache();
            simpleScoreboard.resetAllScores();
        }
        simpleScoreboard.setDisplayName(title);
        for (int line = 0; line < message.size(); line++) {
            simpleScoreboard.setScore(line, message.get(line), line);
        }
        simpleScoreboard.update();
        simpleScoreboard.addPlayer(player);
        this.scoreboards.put(player, simpleScoreboard);
    }

    @Override
    public void closeScoreboard(Player player) {
        if (this.scoreboards.containsKey(player)) {
            gt.creeperface.nukkit.scoreboardapi.scoreboard.SimpleScoreboard simpleScoreboard = this.scoreboards.get(player);
            simpleScoreboard.removePlayer(player);
            simpleScoreboard.update();
            this.scoreboards.remove(player);
        }
    }

}
