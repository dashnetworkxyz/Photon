package xyz.dashnetwork.photon.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.dashnetwork.photon.npc.animator.Animator;
import xyz.dashnetwork.photon.npc.move.MoveController;
import xyz.dashnetwork.photon.npc.move.Mover;
import xyz.dashnetwork.photon.util.MojangUtil;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PhotonBuilder {

    private Location location;
    private GameProfile profile;

    private Function<PhotonNPC, Animator> animator;
    private Function<PhotonNPC, Mover> mover;
    private BiConsumer<NPC, Player> attackCallback, interactCallback;
    private boolean hideNametag;
    private boolean autoView = true;

    public void animator(Animator animator) {
        this.animator = (npc) -> animator;
    }

    public void mover(Mover mover) {
        this.mover = (npc) -> mover;
    }

    public void moveController() {
        this.mover = MoveController::new;
    }

    public void onAttack(BiConsumer<NPC, Player> consumer) {
        if (attackCallback == null)
            attackCallback = consumer;
        else
            attackCallback = attackCallback.andThen(consumer);
    }

    public void onInteract(BiConsumer<NPC, Player> consumer) {
        if (interactCallback == null)
            interactCallback = consumer;
        else
            interactCallback = interactCallback.andThen(consumer);
    }

    public void onClick(BiConsumer<NPC, Player> consumer) {
        onAttack(consumer);
        onInteract(consumer);
    }

    public void location(Location location) {
        this.location = location;
    }

    public void location(World world, double x, double y, double z, float yaw, float pitch) {
        this.location = new Location(world, x, y, z, yaw, pitch);
    }

    public void location(World world, double x, double y, double z) {
        this.location = new Location(world, x, y, z);
    }

    public void profile(String name, UUID uuid) {
        this.profile = new GameProfile(uuid, name);
    }

    public void profile(String name) {
        this.profile = new GameProfile(UUID.randomUUID(), name);
    }

    public void skin(String json, String signature) {
        this.profile.getProperties().put("textures", new Property("textures", json, signature));
    }

    public void skin(UUID account) {
        this.profile.getProperties().put("textures",
                MinecraftServer.getServer().aD().fillProfileProperties(new GameProfile(account, ""), true)
                        .getProperties().get("textures").iterator().next()
        );
    }

    public void skinAuto() {
        this.profile = MinecraftServer.getServer().aD().fillProfileProperties(profile, true);
    }

    public void disableAutoView() {
        this.autoView = false;
    }

    public void hideNametag() {
        this.hideNametag = true;
    }

    public NPC build() {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null");

        if (profile == null)
            profile = new GameProfile(UUID.randomUUID(), "NPC");

        if (animator == null)
            animator = Animator::new;

        if (mover == null)
            mover = (npc) -> new Mover() {};

        return new PhotonNPC(
                mover, animator, attackCallback, interactCallback, location, profile, autoView, hideNametag
        );
    }

}
