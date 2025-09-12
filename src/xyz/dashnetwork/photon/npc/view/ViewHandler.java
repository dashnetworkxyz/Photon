package xyz.dashnetwork.photon.npc.view;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.dashnetwork.photon.Photon;
import xyz.dashnetwork.photon.npc.PhotonNPC;

public class ViewHandler implements Listener {

    public static int NPC_ENTER_RANGE = 46;
    public static int NPC_LEAVE_RANGE = 50;
    private final PhotonNPC npc;

    public ViewHandler(PhotonNPC npc) {
        this.npc = npc;
        Bukkit.getPluginManager().registerEvents(this, Photon.getProvidingPlugin(Photon.class));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { updatePlayer(event.getPlayer()); }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) { updatePlayer(event.getPlayer()); }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) { updatePlayer(event.getPlayer()); }

    public boolean updatePlayer(Player player) {
        Location location = npc.getLocation();

        if (!player.getWorld().equals(location.getWorld()))
            return false;

        double distanceX = location.getX() - player.getLocation().getX();
        double distanceZ = location.getZ() - player.getLocation().getZ();
        double distance = Math.sqrt((distanceX * distanceX) + (distanceZ * distanceZ));

        if (npc.getViewers().contains(player) && distance > NPC_LEAVE_RANGE)
            npc.removeViewer(player);
        else if (!npc.getViewers().contains(player) && distance < NPC_ENTER_RANGE)
            npc.addViewer(player);
        else
            return false;

        return true;
    }

}
