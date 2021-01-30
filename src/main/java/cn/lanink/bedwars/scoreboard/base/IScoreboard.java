package cn.lanink.bedwars.scoreboard.base;

import cn.nukkit.Player;

import java.util.List;

/**
 * @author lt_name
 */
public interface IScoreboard {

    /**
     * @return Scoreboard API Name
     * 格式： 作者的Github名称(包名)
     */
    String getScoreboardName();

    /**
     * 计分板显示信息
     * @param player 玩家
     * @param title 标题
     * @param message 信息
     */
    void showScoreboard(Player player, String title, List<String> message);

    /**
     * 关闭计分板显示
     * @param player 玩家
     */
    void closeScoreboard(Player player);

}
