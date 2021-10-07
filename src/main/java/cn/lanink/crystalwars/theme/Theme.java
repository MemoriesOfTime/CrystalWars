package cn.lanink.crystalwars.theme;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LT_Name
 */
public class Theme {

    @Getter
    private final String name;
    private final Config config;

    private String scoreboardTitleGame;
    private List<String> scoreboardLineGame;

    public Theme(@NotNull String fileName, @NotNull File file) {
        this.name = fileName;
        this.config = new Config(file, Config.YAML);

        this.scoreboardTitleGame = this.config.getString("scoreboard_game.title");
        this.scoreboardLineGame = this.config.getStringList("scoreboard_game.line");
    }

    public String getScoreboardTitleGame(BaseArena arena, Player player) {
        return this.stringReplace(arena, player, this.scoreboardTitleGame);
    }

    public List<String> getScoreboardLineGame(BaseArena arena, Player player) {
        ArrayList<String> list = new ArrayList<>();
        for (String string : this.scoreboardLineGame) {
            String stringReplace = this.stringReplace(arena, player, string, list);

            if (stringReplace.contains("[IF:")) {
                String[] split1 = stringReplace.split("\\[IF:");
                String[] split2 = split1[1].split("]");
                if (split2.length > 1) {
                    String content = split2[1].substring(split2[1].indexOf("{") + 1, split2[1].indexOf("}"));
                    String fullContent = "[IF:" + split2[0] + "]{" + content + "}";
                    if ("isOvertime".equalsIgnoreCase(split2[0])) {
                        if (arena.isOvertime()) {
                            stringReplace = stringReplace.replace(fullContent, content);
                        } else {
                            stringReplace = stringReplace.replace(fullContent, "");
                        }
                    } else if ("TeamSurviving_RED".equalsIgnoreCase(split2[0])) {
                        if (arena.isTeamCrystalSurviving(Team.RED) || !arena.getSurvivingPlayers(Team.RED).isEmpty()) {
                            stringReplace = stringReplace.replace(fullContent, content);
                        } else {
                            stringReplace = stringReplace.replace(fullContent, "");
                        }
                    } else if ("TeamSurviving_YELLOW".equalsIgnoreCase(split2[0])) {
                        if (arena.isTeamCrystalSurviving(Team.YELLOW) || !arena.getSurvivingPlayers(Team.YELLOW).isEmpty()) {
                            stringReplace = stringReplace.replace(fullContent, content);
                        } else {
                            stringReplace = stringReplace.replace(fullContent, "");
                        }
                    } else if ("TeamSurviving_BLUE".equalsIgnoreCase(split2[0])) {
                        if (arena.isTeamCrystalSurviving(Team.BLUE) || !arena.getSurvivingPlayers(Team.BLUE).isEmpty()) {
                            stringReplace = stringReplace.replace(fullContent, content);
                        } else {
                            stringReplace = stringReplace.replace(fullContent, "");
                        }
                    } else if ("TeamSurviving_GREEN".equalsIgnoreCase(split2[0])) {
                        if (arena.isTeamCrystalSurviving(Team.GREEN) || !arena.getSurvivingPlayers(Team.GREEN).isEmpty()) {
                            stringReplace = stringReplace.replace(fullContent, content);
                        } else {
                            stringReplace = stringReplace.replace(fullContent, "");
                        }
                    }
                }
            }
            if (stringReplace.contains("[IF_NOT:")) {
                String[] split1 = stringReplace.split("\\[IF_NOT:");
                String[] split2 = split1[1].split("]");
                if (split2.length > 1) {
                    String content = split2[1].substring(split2[1].indexOf("{") + 1, split2[1].indexOf("}"));
                    String fullContent = "[IF_NOT:" + split2[0] + "]{" + content + "}";
                    if ("isOvertime".equalsIgnoreCase(split2[0])) {
                        if (arena.isOvertime()) {
                            stringReplace = stringReplace.replace(fullContent, "");
                        } else {
                            stringReplace = stringReplace.replace(fullContent, content);
                        }
                    } else if ("TeamSurviving_RED".equalsIgnoreCase(split2[0])) {
                        if (arena.isTeamCrystalSurviving(Team.RED) || !arena.getSurvivingPlayers(Team.RED).isEmpty()) {
                            stringReplace = stringReplace.replace(fullContent, "");
                        } else {
                            stringReplace = stringReplace.replace(fullContent, content);

                        }
                    } else if ("TeamSurviving_YELLOW".equalsIgnoreCase(split2[0])) {
                        if (arena.isTeamCrystalSurviving(Team.YELLOW) || !arena.getSurvivingPlayers(Team.YELLOW).isEmpty()) {
                            stringReplace = stringReplace.replace(fullContent, "");
                        } else {
                            stringReplace = stringReplace.replace(fullContent, content);
                        }
                    } else if ("TeamSurviving_BLUE".equalsIgnoreCase(split2[0])) {
                        if (arena.isTeamCrystalSurviving(Team.BLUE) || !arena.getSurvivingPlayers(Team.BLUE).isEmpty()) {
                            stringReplace = stringReplace.replace(fullContent, "");
                        } else {
                            stringReplace = stringReplace.replace(fullContent, content);
                        }
                    } else if ("TeamSurviving_GREEN".equalsIgnoreCase(split2[0])) {
                        if (arena.isTeamCrystalSurviving(Team.GREEN) || !arena.getSurvivingPlayers(Team.GREEN).isEmpty()) {
                            stringReplace = stringReplace.replace(fullContent, "");
                        } else {
                            stringReplace = stringReplace.replace(fullContent, content);
                        }
                    }
                }
            }

            if ("".equals(stringReplace)) {
                continue;
            }

            list.add(stringReplace);
        }
        return list;
    }

    public String stringReplace(BaseArena arena, Player player, String string) {
        return this.stringReplace(arena, player, string, new ArrayList<>());
    }

    public String stringReplace(BaseArena arena, Player player, String string, List<?> list) {
        return string
                .replace("{PluginName}", CrystalWars.PLUGIN_NAME)
                .replace("{AutoSpace}", Utils.getSpace(list))
                //队伍名称
                .replace("{TeamName_RED}", Utils.getShowTeam(Team.RED))
                .replace("{TeamName_YELLOW}", Utils.getShowTeam(Team.YELLOW))
                .replace("{TeamName_BLUE}", Utils.getShowTeam(Team.BLUE))
                .replace("{TeamName_GREEN}", Utils.getShowTeam(Team.GREEN))
                //队伍水晶血量
                .replace("{TeamCrystalHealth_RED}", Utils.getShowHealth(arena.getTeamEntityEndCrystal(Team.RED)))
                .replace("{TeamCrystalHealth_YELLOW}", Utils.getShowHealth(arena.getTeamEntityEndCrystal(Team.YELLOW)))
                .replace("{TeamCrystalHealth_BLUE}", Utils.getShowHealth(arena.getTeamEntityEndCrystal(Team.BLUE)))
                .replace("{TeamCrystalHealth_GREEN}", Utils.getShowHealth(arena.getTeamEntityEndCrystal(Team.GREEN)))
                //队伍存活人数
                .replace("{TeamSurvivingPlayers_RED}", String.valueOf(arena.getSurvivingPlayers(Team.RED).size()))
                .replace("{TeamSurvivingPlayers_YELLOW}", String.valueOf(arena.getSurvivingPlayers(Team.YELLOW).size()))
                .replace("{TeamSurvivingPlayers_BLUE}", String.valueOf(arena.getSurvivingPlayers(Team.BLUE).size()))
                .replace("{TeamSurvivingPlayers_GREEN}", String.valueOf(arena.getSurvivingPlayers(Team.GREEN).size()));
    }

}
