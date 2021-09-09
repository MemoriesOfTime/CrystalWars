package cn.lanink.crystalwars.entity;

import cn.lanink.gamecore.api.Info;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author LT_Name
 */
public class EntityText extends Entity {

    @Info("只是为了兼容PN核心")
    @Deprecated
    public EntityText(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.close();
    }

    public EntityText(Position position, String message) {
        super(position.getChunk(), getDefaultNBT(position));
        this.setNameTag(message);
    }

    @Override
    public int getNetworkId() {
        return 64;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
        this.setHealth(20.0F);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setImmobile(true);
    }

}
