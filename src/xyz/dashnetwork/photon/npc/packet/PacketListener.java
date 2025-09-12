package xyz.dashnetwork.photon.npc.packet;

import io.netty.channel.*;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import xyz.dashnetwork.photon.npc.PhotonNPC;

import java.lang.reflect.Field;

public class PacketListener extends ChannelInboundHandlerAdapter {

    private final Player player;

    public PacketListener(Player player) { this.player = player; }

    @Override
    public void channelRead(ChannelHandlerContext context, Object object) throws Exception {
        if (object instanceof PacketPlayInUseEntity packet) {
            boolean handled = switch (packet.a()) {
                case ATTACK -> PhotonNPC.handleAttack(readField(packet, "a"), player);
                case INTERACT -> PhotonNPC.handleInteract(readField(packet, "a"), player);
                default -> false;
            };

            if (handled)
                return;
        }

        super.channelRead(context, object);
    }

    private <T>T readField(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);

            return (T) field.get(object);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

}
