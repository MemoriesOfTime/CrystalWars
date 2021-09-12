package cn.lanink.crystalwars.arena;

import cn.nukkit.utils.BlockColor;
import lombok.Getter;

/**
 * @author lt_name
 */
public enum Team {

    /**
     * 没有队伍
     */
    NULL("§7", BlockColor.GRAY_BLOCK_COLOR),

    /**
     * 红队
     */
    RED("§c", BlockColor.RED_BLOCK_COLOR),

    /**
     * 黄队
     */
    YELLOW("§e", BlockColor.YELLOW_BLOCK_COLOR),

    /**
     * 蓝队
     */
    BLUE("§9", BlockColor.BLUE_BLOCK_COLOR),

    /**
     * 绿队
     */
    GREEN("§2", BlockColor.GREEN_BLOCK_COLOR);

    @Getter
    private final String stringColor;
    @Getter
    private final BlockColor blockColor;

    Team(String stringColor, BlockColor blockColor) {
        this.stringColor = stringColor;
        this.blockColor = new BlockColor(
                blockColor.getRed(),
                blockColor.getGreen(),
                blockColor.getBlue(),
                blockColor.getAlpha()
        );
    }

}
