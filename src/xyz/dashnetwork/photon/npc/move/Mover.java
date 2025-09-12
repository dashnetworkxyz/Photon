package xyz.dashnetwork.photon.npc.move;

public interface Mover {

    default boolean isGrounded() { return true; }

    default boolean punchOnSpawn() { return true; }

}
