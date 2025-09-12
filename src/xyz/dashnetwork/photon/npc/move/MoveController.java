package xyz.dashnetwork.photon.npc.move;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.dashnetwork.photon.Photon;
import xyz.dashnetwork.photon.npc.PhotonNPC;

import java.util.function.Function;

public class MoveController implements Mover, Runnable {

    private final PhotonNPC npc;
    private final BukkitTask task;
    private Vector velocity;
    private boolean grounded;

    public MoveController(PhotonNPC npc) {
        this.npc = npc;
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(Photon.getProvidingPlugin(Photon.class), this, 1, 1);
    }

    public Vector getVelocity() { return velocity; }

    public void setVelocity(Vector velocity) { this.velocity = velocity; }

    public void setVelocity(Function<Vector, Vector> function) { this.velocity = function.apply(this.velocity); }

    public BukkitTask getTask() { return task; }

    public void updateLocation(Location calculated) {
        Location location = npc.getLocation();

        if (!calculated.equals(location)) {
            double distanceX = calculated.getX() - location.getX();
            double distanceY = calculated.getY() - location.getY();
            double distanceZ = calculated.getZ() - location.getZ();

            if (distanceX > 8 || distanceY > 8 || distanceZ > 8) {

            }
        }
    }

    @Override
    public boolean isGrounded() { return grounded; }

    @Override
    public boolean punchOnSpawn() { return false; }

    @Override
    public void run() {
        // Tick
    }

}
