package cn.lanink.bedwars.utils.scoreboard.ltname;

import cn.lanink.bedwars.utils.scoreboard.ltname.packet.RemoveObjectivePacket;
import cn.lanink.bedwars.utils.scoreboard.ltname.packet.SetObjectivePacket;
import cn.lanink.bedwars.utils.scoreboard.ltname.packet.SetScorePacket;
import cn.lanink.bedwars.utils.scoreboard.ltname.packet.data.ScoreData;
import cn.nukkit.Player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class Scoreboard {

    private final String criteriaName = "dummy";
    private final String objectiveName;
    private final String displayName;
    private final ScoreboardData.DisplaySlot displaySlot;
    private final ScoreboardData.SortOrder sortOrder;
    private final ConcurrentHashMap<Integer, ScoreboardData.ScoreboardLine> line = new ConcurrentHashMap<>();
    private final HashSet<Player> showPlayers = new HashSet<>();

    public Scoreboard(String objective, String title, ScoreboardData.DisplaySlot displaySlot, ScoreboardData.SortOrder sortOrder) {
        this.objectiveName = objective;
        this.displayName = title;
        this.displaySlot = displaySlot;
        this.sortOrder = sortOrder;
    }

    public synchronized void show(Player player) {
        if (this.showPlayers.add(player)) {
            SetObjectivePacket setObjectivePacket = new SetObjectivePacket();
            setObjectivePacket.criteriaName = this.criteriaName;
            setObjectivePacket.displayName = this.displayName;
            setObjectivePacket.objectiveName = this.objectiveName;
            setObjectivePacket.displaySlot = this.displaySlot.name().toLowerCase();
            setObjectivePacket.sortOrder = this.sortOrder.ordinal();
            player.dataPacket(setObjectivePacket);
            this.updateDisplayLine(player);
        }
    }

    public synchronized void hide(Player player) {
        if (this.showPlayers.contains(player)) {
            RemoveObjectivePacket removeObjectivePacket = new RemoveObjectivePacket();
            removeObjectivePacket.objectiveName = this.objectiveName;
            player.dataPacket(removeObjectivePacket);
            this.showPlayers.remove(player);
        }
    }

    public void updateDisplayLine() {
        for (Player player : this.showPlayers) {
            this.updateDisplayLine(player);
        }
    }

    public void updateDisplayLine(Player player) {
        List<ScoreData> scoreDataList = new LinkedList<>();
        for (Map.Entry<Integer, ScoreboardData.ScoreboardLine> entry : this.line.entrySet()) {
            ScoreData scoreData = new ScoreData();
            scoreData.scoreId = entry.getValue().getScore();
            scoreData.objective = this.objectiveName;
            scoreData.score = entry.getValue().getScore();
            scoreData.entityType = (byte) SetScorePacket.Type.FAKE.ordinal();
            scoreData.fakeEntity = entry.getValue().getMessage();
            scoreData.entityId = 0;
            scoreDataList.add(scoreData);
        }
        SetScorePacket pk = new SetScorePacket();
        pk.type = (byte) SetScorePacket.Action.SET.ordinal();
        pk.scoreDataList = scoreDataList;
        player.dataPacket(pk);
    }

    public void removeDisplayLine() {
        for (Player player : this.showPlayers) {
            this.removeDisplayLine(player);
        }
    }

    public void removeDisplayLine(Player player) {
        List<ScoreData> scoreDataList = new LinkedList<>();
        for (Map.Entry<Integer, ScoreboardData.ScoreboardLine> entry : this.line.entrySet()) {
            ScoreData scoreData = new ScoreData();
            scoreData.scoreId = entry.getValue().getScore();
            scoreData.objective = this.objectiveName;
            scoreData.score = entry.getValue().getScore();
            scoreDataList.add(scoreData);
        }
        SetScorePacket pk = new SetScorePacket();
        pk.type = (byte) SetScorePacket.Action.REMOVE.ordinal();
        pk.scoreDataList = scoreDataList;
        player.dataPacket(pk);
    }

    public ConcurrentHashMap<Integer, ScoreboardData.ScoreboardLine> getAllLine() {
        return this.line;
    }

    public void clearAllLine() {
        this.removeDisplayLine();
        this.line.clear();
    }

    public void setLine(int id, String message, int score) {
        this.line.put(id, new ScoreboardData.ScoreboardLine(message, score));
    }

    public int addLine(String message, int score) {
        int id = this.line.size() + 1;
        this.line.put(id, new ScoreboardData.ScoreboardLine(message, score));
        return id;
    }

    public void removeLine(int id) {
        this.line.remove(id);
    }

}
