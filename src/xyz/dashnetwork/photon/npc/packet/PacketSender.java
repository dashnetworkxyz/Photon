package xyz.dashnetwork.photon.npc.packet;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.dashnetwork.photon.npc.PhotonNPC;

import java.lang.reflect.Field;
import java.util.List;

public class PacketSender {

    private final PhotonNPC npc;

    public PacketSender(PhotonNPC npc) { this.npc = npc; }

    public void sendAddPlayerInfo(Iterable<Player> players) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        writeField(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        writeField(packet, "b", List.of(packet.new PlayerInfoData(
                npc.getGameProfile(), 0, WorldSettings.EnumGamemode.ADVENTURE, new ChatComponentText(""))
        ));

        sendPacket(players, packet);
    }

    public void sendRemovePlayerInfo(Iterable<Player> players) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        writeField(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        writeField(packet, "b", List.of(packet.new PlayerInfoData(
                npc.getGameProfile(), 0, WorldSettings.EnumGamemode.NOT_SET, null
        )));

        sendPacket(players, packet);
    }

    public void sendSpawn(Iterable<Player> players) {
        Location location = npc.getLocation();

        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
        writeField(packet, "a", npc.getEntityId());
        writeField(packet, "b", npc.getGameProfile().getId());
        writeField(packet, "c", (int) (location.getX() * 32));
        writeField(packet, "d", (int) (location.getY() * 32));
        writeField(packet, "e", (int) (location.getZ() * 32));
        writeField(packet, "f", (byte) (location.getYaw() * 256 / 360));
        writeField(packet, "g", (byte) (location.getPitch() * 256 / 360));
        writeField(packet, "h", 0); // Held item id
        writeField(packet, "i", createMetadata());

        sendPacket(players, packet);
    }

    public void sendDespawn(Iterable<Player> players) {
        sendPacket(players, new PacketPlayOutEntityDestroy(npc.getEntityId()));
    }

    public void sendTeleport(Iterable<Player> players) {
        Location location = npc.getLocation();

        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        writeField(packet, "a", npc.getEntityId());
        writeField(packet, "b", (int) (location.getX() * 32));
        writeField(packet, "c", (int) (location.getX() * 32));
        writeField(packet, "d", (int) (location.getX() * 32));
        writeField(packet, "e", (byte) (location.getYaw() * 256 / 360));
        writeField(packet, "f", (byte) (location.getPitch() * 256 / 360));
        writeField(packet, "g", npc.getMover().isGrounded());

        sendPacket(players, packet);
    }

    public void sendRelMove(Iterable<Player> players, Location previous) {
        Location location = npc.getLocation();

        PacketPlayOutEntity.PacketPlayOutRelEntityMove packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                npc.getEntityId(),
                (byte) (location.getX() - previous.getX() * 32),
                (byte) (location.getY() - previous.getY() * 32),
                (byte) (location.getZ() - previous.getZ() * 32),
                npc.getMover().isGrounded()
        );

        sendPacket(players, packet);
    }

    public void sendLook(Iterable<Player> players) {
        Location location = npc.getLocation();

        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(
                npc.getEntityId(),
                (byte) (location.getYaw() * 256 / 360),
                (byte) (location.getPitch() * 256 / 360),
                npc.getMover().isGrounded()
        );

        sendPacket(players, packet);
    }

    public void sendRelMovLook(Iterable<Player> players, Location previous) {
        Location location = npc.getLocation();

        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
                npc.getEntityId(),
                (byte) (location.getX() - previous.getX() * 32),
                (byte) (location.getY() - previous.getY() * 32),
                (byte) (location.getZ() - previous.getZ() * 32),
                (byte) (location.getYaw() * 256 / 360),
                (byte) (location.getPitch() * 256 / 360),
                npc.getMover().isGrounded()
        );

        sendPacket(players, packet);
    }

    public void sendRotation(Iterable<Player> players) {
        PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation();
        writeField(packet, "a", npc.getEntityId());
        writeField(packet, "b", (byte) (npc.getLocation().getYaw() * 256 / 360));

        sendPacket(players, packet);
    }

    public void sendMetadata(Iterable<Player> players) {
        sendPacket(players, new PacketPlayOutEntityMetadata(npc.getEntityId(), createMetadata(), true));
    }

    public void sendArmSwing(Iterable<Player> players) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        writeField(packet, "a", npc.getEntityId());
        writeField(packet, "b", 0);

        sendPacket(players, packet);
    }

    public void sendDamageTick(Iterable<Player> players) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        writeField(packet, "a", npc.getEntityId());
        writeField(packet, "b", 1);

        sendPacket(players, packet);
    }

    public void sendSound(Iterable<Player> players, String sound, float volume, float pitch) {
        Location location = npc.getLocation();

        sendPacket(players, new PacketPlayOutNamedSoundEffect(
                sound, location.getX(), location.getY(), location.getZ(), volume, pitch
        ));
    }

    private DataWatcher createMetadata() {
        DataWatcher watcher = new DataWatcher(null);
        watcher.a(0, Byte.valueOf(npc.getAnimator().getPlayerState().toBitMask()));
        watcher.a(1, Short.valueOf((short) (npc.getMover().isGrounded() ? 0 : 300)));
        watcher.a(2, ""); // Custom name
        watcher.a(3, Byte.valueOf((byte) 0)); // Custom name visible
        watcher.a(4, Byte.valueOf((byte) 0)); // idk
        watcher.a(6, Float.valueOf(1f)); // Health
        watcher.a(7, Integer.valueOf(0)); // Potion color
        watcher.a(8, Byte.valueOf((byte) 0)); // Potion ambient
        watcher.a(9, Byte.valueOf((byte) 0)); // Arrows stuck in entity
        watcher.a(10, Byte.valueOf(npc.getAnimator().getSkinState().toBitMask()));
        watcher.a(16, Byte.valueOf((byte) 0)); // idk
        watcher.a(17, Float.valueOf(0f)); // Absorption hearts

        return watcher;
    }

    private void writeField(Object object, String name, Object set) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, set);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void sendPacket(Iterable<Player> players, Packet<?> packet) {
        for (Player player : players)
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
