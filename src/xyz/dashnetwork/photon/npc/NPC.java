package xyz.dashnetwork.photon.npc;

import com.mojang.authlib.GameProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.dashnetwork.photon.npc.animator.Animator;
import xyz.dashnetwork.photon.npc.move.Mover;
import xyz.dashnetwork.photon.npc.packet.PacketSender;
import xyz.dashnetwork.photon.npc.view.ViewHandler;

import java.util.Collection;
import java.util.Optional;

public interface NPC {

    Collection<Player> getViewers();

    void addViewer(Player player);

    void removeViewer(Player player);

    Animator getAnimator();

    Mover getMover();

    Optional<ViewHandler> getViewHandler();

    PacketSender getPacketSender();

    int getEntityId();

    GameProfile getGameProfile();

    Location getLocation();

    ItemStack[] getEquipment();

    void setEquipment(ItemStack[] equipment);

    void teleport(Location location);

}
