package cn.lanink.bedwars.scoreboard.theamychan;

import cn.lanink.bedwars.scoreboard.base.IScoreboard;
import cn.nukkit.Player;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class SimpleScoreboard implements IScoreboard {

    private final ConcurrentHashMap<Player, Scoreboard> scoreboards = new ConcurrentHashMap<>();

    @Override
    public String getScoreboardName() {
        return "LucGamesYT(de.theamychan.scoreboard.api)";
    }

    @Override
    public void showScoreboard(Player player, String title, List<String> message) {
        de.theamychan.scoreboard.network.Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR, title, title);
        if (this.scoreboards.containsKey(player)) {
            this.scoreboards.get(player).hideFor(player);
        }
        for (int line = 0; line < message.size(); line++) {
            scoreboardDisplay.addLine(message.get(line), line);
        }
        scoreboard.showFor(player);
        this.scoreboards.put(player, scoreboard);
    }

    @Override
    public void closeScoreboard(Player player) {
        if (this.scoreboards.containsKey(player)) {
            Scoreboard scoreboard = this.scoreboards.get(player);
            scoreboard.hideFor(player);
            this.scoreboards.remove(player);
        }
    }

}
