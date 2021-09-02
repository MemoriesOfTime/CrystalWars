package cn.lanink.crystalwars.entity;

import cn.lanink.crystalwars.arena.Team;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import lombok.Getter;

/**
 * @author LT_Name
 */
public class CrystalWarsEntityEndCrystal extends Entity implements EntityExplosive {

    public static final int NETWORK_ID = 71;

    protected boolean detonated = false;

    @Getter
    private final Team team;

    private int lastAttackTick;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public CrystalWarsEntityEndCrystal(FullChunk chunk, CompoundTag nbt, Team team) {
        super(chunk, nbt);
        this.team = team;
        this.setShowBase(true);
        this.setMaxHealth(100);
        this.setHealth(100);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("ShowBottom")) {
            this.setShowBase(this.namedTag.getBoolean("ShowBottom"));
        }

        this.fireProof = true;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_FIRE_IMMUNE, true);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putBoolean("ShowBottom", this.showBase());
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (Server.getInstance().getTick() - this.lastAttackTick < 10) {
            return false;
        }

        if (source.getCause() != EntityDamageEvent.DamageCause.FIRE && source.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK && source.getCause() != EntityDamageEvent.DamageCause.LAVA) {
            if (super.attack(source)) {
                this.lastAttackTick = Server.getInstance().getTick();
                if (this.getHealth() < 1) {
                    this.explode();
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void explode() {
        if (!this.detonated) {
            this.detonated = true;
            Position pos = this.getPosition();
            this.close();

            this.level.addParticle(new HugeExplodeSeedParticle(pos));
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_EXPLODE);
        }

    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public boolean showBase() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SHOWBASE);
    }

    public void setShowBase(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHOWBASE, value);
    }

}
