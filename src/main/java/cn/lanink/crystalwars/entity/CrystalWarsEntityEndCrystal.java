package cn.lanink.crystalwars.entity;

import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.gamecore.utils.EntityUtils;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.DummyBossBar;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LT_Name
 */
public class CrystalWarsEntityEndCrystal extends Entity implements EntityExplosive {

    public static final int NETWORK_ID = 71;

    protected boolean detonated = false;

    @Getter
    private final BaseArena arena;
    @Getter
    private final Team team;

    private int lastAttackTick;

    private final HashMap<Player, DummyBossBar> bossBarMap = new HashMap<>();

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public CrystalWarsEntityEndCrystal(FullChunk chunk, CompoundTag nbt, BaseArena arena, Team team) {
        super(chunk, nbt);
        this.arena = arena;
        this.team = team;
        this.setShowBase(true);
        this.setMaxHealth(100);
        this.setHealth(100);
        this.setNameTag(Utils.getShowTeam(this.getTeam()) + "\n" + Utils.getEntityShowHealth(this));
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("ShowBottom")) {
            this.setShowBase(this.namedTag.getBoolean("ShowBottom"));
        }

        this.fireProof = true;
        this.setDataFlag(
                EntityUtils.getEntityField("DATA_FLAGS", DATA_FLAGS),
                EntityUtils.getEntityField("DATA_FLAG_FIRE_IMMUNE", DATA_FLAG_FIRE_IMMUNE),
                true
        );

        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
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
    public boolean onUpdate(int currentTick) {
        if (currentTick%5 == 0) {
            for (Player player : this.getLevel().getPlayers().values()) {
                if (this.distance(player) <= 10) {
                    if (!this.bossBarMap.containsKey(player)) {
                        DummyBossBar bossBar = new DummyBossBar
                                .Builder(player).text(Utils.getShowTeam(this.getTeam()) + "§e水晶").build();
                        this.bossBarMap.put(player, bossBar);
                    }
                    DummyBossBar bossBar = this.bossBarMap.get(player);
                    if (!player.getDummyBossBars().containsKey(bossBar.getBossBarId())) {
                        player.createBossBar(bossBar);
                    }
                    bossBar.setLength((this.getHealth() / this.getMaxHealth()) * 100);
                }else if (this.bossBarMap.containsKey(player)) {
                    DummyBossBar bossBar = this.bossBarMap.get(player);
                    player.removeBossBar(bossBar.getBossBarId());
                }
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (Server.getInstance().getTick() - this.lastAttackTick < 10) {
            return false;
        }

        if (source.getCause() != EntityDamageEvent.DamageCause.FIRE && source.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK && source.getCause() != EntityDamageEvent.DamageCause.LAVA) {
            if (super.attack(source)) {
                this.setNameTag(Utils.getShowTeam(this.getTeam()) + "\n" + Utils.getEntityShowHealth(this));
                this.lastAttackTick = Server.getInstance().getTick();

                this.level.addSound(this, Sound.MOB_BLAZE_HIT);
                this.level.addParticle(new DestroyBlockParticle(this, Block.get(Block.REDSTONE_BLOCK)));

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

            for (Player player : this.getArena().getPlayers(this.getTeam())) {
                player.sendTitle("§c§l✘", "§e你的水晶已被§c§l破坏§r§e将§c§l无法重生");
            }
        }
    }

    @Override
    public void close() {
        super.close();

        for (Map.Entry<Player, DummyBossBar> entry : this.bossBarMap.entrySet()) {
            entry.getKey().removeBossBar(entry.getValue().getBossBarId());
        }
        this.bossBarMap.clear();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public boolean showBase() {
        return this.getDataFlag(
                EntityUtils.getEntityField("DATA_FLAGS", DATA_FLAGS),
                EntityUtils.getEntityField("DATA_FLAG_SHOWBASE", DATA_FLAG_SHOWBASE)
        );
    }

    public void setShowBase(boolean value) {
        this.setDataFlag(
                EntityUtils.getEntityField("DATA_FLAGS", DATA_FLAGS),
                EntityUtils.getEntityField("DATA_FLAG_SHOWBASE", DATA_FLAG_SHOWBASE),
                value
        );
    }

}
