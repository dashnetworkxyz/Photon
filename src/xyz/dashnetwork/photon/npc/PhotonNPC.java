package xyz.dashnetwork.photon.npc;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xyz.dashnetwork.photon.Photon;
import xyz.dashnetwork.photon.npc.animator.Animator;
import xyz.dashnetwork.photon.npc.animator.PlayerState;
import xyz.dashnetwork.photon.npc.animator.SkinState;
import xyz.dashnetwork.photon.npc.move.MoveController;
import xyz.dashnetwork.photon.npc.move.Mover;
import xyz.dashnetwork.photon.npc.packet.PacketSender;
import xyz.dashnetwork.photon.npc.view.ViewHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PhotonNPC implements NPC {

    public static final String NAMETAG_TEAM_NAME = "photon-npc-team";

    private static final List<PhotonNPC> npcs = new CopyOnWriteArrayList<>();
    private final List<Player> viewers = new ArrayList<>();
    private final PacketSender packetSender;
    private final Animator animator;
    private final Mover mover;
    private final BiConsumer<NPC, Player> attackCallback, interactCallback;
    private ViewHandler viewHandler;

    private final int entityId = getAndIncrementEntityId();
    private final GameProfile profile;
    private final boolean hideNametag;
    private ItemStack[] equipment;
    private Location location;

    public PhotonNPC(Function<PhotonNPC, Mover> mover, Function<PhotonNPC, Animator> animator,
                     BiConsumer<NPC, Player> attackCallback, BiConsumer<NPC, Player> interactCallback,
                     PlayerState playerState, SkinState skinState,
                     Location location, GameProfile profile, boolean autoView, boolean hideNametag) {
        this.packetSender = new PacketSender(this);
        this.animator = animator.apply(this);
        this.animator.setPlayerState(playerState);
        this.animator.setSkinState(skinState);
        this.mover = mover.apply(this);
        this.attackCallback = attackCallback;
        this.interactCallback = interactCallback;
        this.location = location;
        this.profile = profile;
        this.hideNametag = hideNametag;

        if (autoView) {
            this.viewHandler = new ViewHandler(this);

            for (Player player : Bukkit.getOnlinePlayers())
                viewHandler.updatePlayer(player);
        }

        npcs.add(this);
    }

    public static List<PhotonNPC> getNPCs() { return npcs; }

    public static PhotonNPC getNPC(int entityId) {
        for (PhotonNPC npc : npcs)
            if (npc.getEntityId() == entityId)
                return npc;

        return null;
    }

    private static boolean getNPC(int entityId, Consumer<PhotonNPC> consumer) {
        PhotonNPC npc = getNPC(entityId);

        if (npc != null) {
            consumer.accept(npc);
            return true;
        }

        return false;
    }

    public static boolean handleAttack(int entityId, final Player player) {
        return getNPC(entityId, npc -> {
            if (npc.mover instanceof MoveController) {
                // TODO: MoveController stuffs
            }

            npc.attackCallback.accept(npc, player);
        });
    }

    public static boolean handleInteract(int entityId, Player player) {
        return getNPC(entityId, npc -> npc.interactCallback.accept(npc, player));
    }

    public void destroy() {
        packetSender.sendDespawn(viewers);

        if (viewHandler != null)
            HandlerList.unregisterAll(viewHandler);

        npcs.remove(this);
    }

    public void updateLocation() {
        List<Player> viewerCopy = new ArrayList<>(viewers);

        if (viewHandler != null)
            for (Player player : this.viewers)
                if (viewHandler.updatePlayer(player))
                    viewerCopy.remove(player);


        packetSender.sendTeleport(viewerCopy);
    }

    public void hideNametag(Player player) {
        Scoreboard scoreboard = player.getScoreboard();

        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        Team team = scoreboard.getTeam(NAMETAG_TEAM_NAME);

        if (team == null) {
            team = scoreboard.registerNewTeam(NAMETAG_TEAM_NAME);
            team.setNameTagVisibility(NameTagVisibility.NEVER);
            team.setPrefix("§6"); // Test
        }

        team.addEntry(profile.getName());
    }

    @Override
    public List<Player> getViewers() { return viewers; }

    @Override
    public void addViewer(Player player) {
        final List<Player> list = List.of(player);

        packetSender.sendAddPlayerInfo(list);
        packetSender.sendSpawn(list);
        packetSender.sendRotation(list); // MC-109346

        Bukkit.getScheduler().runTaskLaterAsynchronously(Photon.getProvidingPlugin(Photon.class),
                () -> packetSender.sendRemovePlayerInfo(list), 5
        );

        if (mover.punchOnSpawn())
            packetSender.sendArmSwing(list);

        if (hideNametag)
            hideNametag(player);

        viewers.add(player);
    }

    @Override
    public void removeViewer(Player player) {
        viewers.remove(player);
        packetSender.sendDespawn(List.of(player));
    }

    @Override
    public Animator getAnimator() { return animator; }

    @Override
    public Mover getMover() { return mover; }

    @Override
    public Optional<ViewHandler> getViewHandler() { return Optional.ofNullable(viewHandler); }

    @Override
    public PacketSender getPacketSender() { return packetSender; }

    @Override
    public int getEntityId() { return entityId; }

    @Override
    public GameProfile getGameProfile() { return profile; }

    @Override
    public Location getLocation() { return location; }

    @Override
    public ItemStack[] getEquipment() { return equipment; }

    @Override
    public void setEquipment(ItemStack[] equipment) {
        this.equipment = equipment;
        // TODO: send equipment update packet
    }

    @Override
    public void teleport(Location location) {
        this.location = location;
        updateLocation();
    }

    public static synchronized int getAndIncrementEntityId() {
        try {
            Field entityCount = Entity.class.getDeclaredField("entityCount");
            entityCount.setAccessible(true);

            int count = entityCount.getInt(null);
            entityCount.setInt(null, count + 1);

            return count;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

}
