package xyz.dashnetwork.photon;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.dashnetwork.photon.npc.PhotonNPC;

public final class Photon extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PipelineInjector(), this);
    }

    @Override
    public void onDisable() {
        for (PhotonNPC npc : PhotonNPC.getNPCs())
            npc.destroy();
    }

}