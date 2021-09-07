package cn.lanink.crystalwars.arena;

/**
 * @author lt_name
 */
public enum Team {

    /**
     * 没有队伍
     */
    NULL("§7"),

    /**
     * 红队
     */
    RED("§c"),

    /**
     * 黄队
     */
    YELLOW("§e"),

    /**
     * 蓝队
     */
    BLUE("§9"),

    /**
     * 绿队
     */
    GREEN("§2");

    private final String color;

    Team(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

}
