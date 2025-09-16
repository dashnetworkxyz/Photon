package xyz.dashnetwork.photon.npc.animator;

public class PlayerState {

    private byte mask;

    public PlayerState() { this.mask = 0; }

    public PlayerState(Flag... flags) {
        for (Flag flag : flags)
            this.mask |= flag.getBit();
    }

    public byte getBitMask() { return mask; }

    public boolean isFlagSet(Flag flag) { return (mask & flag.getBit()) != 0; }

    public enum Flag {

        FIRE((byte) 1),
        CROUCH((byte) 2),
        RIDING((byte) 4), // Unused?
        SPRINT((byte) 8),
        INTERACT((byte) 16),
        INVISIBLE((byte) 32),
        GLOWING((byte) 64);

        private final byte bit;

        Flag(byte bit) { this.bit = bit; }

        public byte getBit() { return bit; }

    }

}
