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

    public void setPlayerState(PlayerState.Flag... flags) { this.playerState = new PlayerState(flags); }

    public void setPlayerState(PlayerState playerState) { this.playerState = playerState; }

    public boolean getPlayerStateFlag(PlayerState.Flag flag) { return playerState.isFlagSet(flag); }

    public void setSkinState(SkinState.Flag... flags) { this.skinState = new SkinState(flags); }

    public void setSkinState(SkinState skinState) { this.skinState = skinState; }

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
