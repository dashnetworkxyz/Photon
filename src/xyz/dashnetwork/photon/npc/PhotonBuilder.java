package xyz.dashnetwork.photon.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.dashnetwork.photon.npc.animator.Animator;
import xyz.dashnetwork.photon.npc.animator.PlayerState;
import xyz.dashnetwork.photon.npc.animator.SkinState;
import xyz.dashnetwork.photon.npc.move.MoveController;
import xyz.dashnetwork.photon.npc.move.Mover;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PhotonBuilder {

    private Location location;
    private GameProfile profile;

    private Function<PhotonNPC, Animator> animator;
    private Function<PhotonNPC, Mover> mover;
    private BiConsumer<NPC, Player> attackCallback, interactCallback;
    private PlayerState playerState;
    private SkinState skinState;
    private boolean hideNametag;
    private boolean autoView = true;

    public PhotonBuilder animator(Animator animator) {
        this.animator = (npc) -> animator;
        return this;
    }

    public PhotonBuilder mover(Mover mover) {
        this.mover = (npc) -> mover;
        return this;
    }

    public PhotonBuilder moveController() {
        this.mover = MoveController::new;
        return this;
    }

    public PhotonBuilder onAttack(BiConsumer<NPC, Player> consumer) {
        if (attackCallback == null)
            attackCallback = consumer;
        else
            attackCallback = attackCallback.andThen(consumer);

        return this;
    }

    public PhotonBuilder onInteract(BiConsumer<NPC, Player> consumer) {
        if (interactCallback == null)
            interactCallback = consumer;
        else
            interactCallback = interactCallback.andThen(consumer);

        return this;
    }

    public PhotonBuilder onClick(BiConsumer<NPC, Player> consumer) {
        onAttack(consumer);
        onInteract(consumer);

        return this;
    }

    public PhotonBuilder location(Location location) {
        this.location = location;
        return this;
    }

    public PhotonBuilder location(World world, double x, double y, double z, float yaw, float pitch) {
        this.location = new Location(world, x, y, z, yaw, pitch);
        return this;
    }

    public PhotonBuilder location(World world, double x, double y, double z) {
        this.location = new Location(world, x, y, z);
        return this;
    }

    public PhotonBuilder profile(String name, UUID uuid) {
        this.profile = new GameProfile(uuid, name);
        return this;
    }

    public PhotonBuilder profile(String name) {
        this.profile = new GameProfile(UUID.randomUUID(), name);
        return this;
    }

    public PhotonBuilder skin(String json, String signature) {
        this.profile.getProperties().put("textures", new Property("textures", json, signature));
        return this;
    }

    public PhotonBuilder skin(UUID account) {
        this.profile.getProperties().put("textures",
                MinecraftServer.getServer().aD().fillProfileProperties(new GameProfile(account, ""), true)
                        .getProperties().get("textures").iterator().next()
        );

        return this;
    }

    public PhotonBuilder skinAuto() {
        this.profile = MinecraftServer.getServer().aD().fillProfileProperties(profile, true);
        return this;
    }

    public PhotonBuilder playerFlags(PlayerState.Flag... flags) {
        this.playerState = new PlayerState(flags);
        return this;
    }

    public PhotonBuilder skinFlags(SkinState.Flag... flags) {
        this.skinState = new SkinState(flags);
        return this;
    }

    public PhotonBuilder disableAutoView() {
        this.autoView = false;
        return this;
    }

    public PhotonBuilder hideNametag() {
        this.hideNametag = true;
        return this;
    }

    public NPC build() {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null");

        if (profile == null)
            profile = new GameProfile(UUID.randomUUID(), "NPC");

        if (animator == null)
            animator = Animator::new;

        if (playerState == null)
            playerState = new PlayerState();

        if (skinState == null)
            skinState = new SkinState();

        if (mover == null)
            mover = (npc) -> new Mover() {};

        return new PhotonNPC(
                mover, animator, attackCallback, interactCallback, playerState, skinState, location, profile, autoView, hideNametag
        );
    }

}
