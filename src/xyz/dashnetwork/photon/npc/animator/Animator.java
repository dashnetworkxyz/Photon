package xyz.dashnetwork.photon.npc.animator;

import xyz.dashnetwork.photon.npc.PhotonNPC;

import java.util.Random;

public class Animator {

    private final Random random = new Random();
    private final PhotonNPC npc;
    private PlayerState playerState;
    private SkinState skinState;

    public Animator(PhotonNPC npc) {
        this.npc = npc;
        this.playerState = new PlayerState();
        this.skinState = new SkinState();
    }

    public PlayerState getPlayerState() { return playerState; }

    public SkinState getSkinState() { return skinState; }

    public void punch() { npc.getPacketSender().sendArmSwing(npc.getViewers()); }

    public void damage() {
        npc.getPacketSender().sendDamageTick(npc.getViewers());
        npc.getPacketSender().sendSound(
                npc.getViewers(),
                "game.player.hurt",
                1f,
                (random.nextFloat() - random.nextFloat()) * 0.2f + 1f
        );
    }

}
