package xyz.dashnetwork.photon.npc.animator;

public class SkinState {

    private byte mask;

    public SkinState() { this.mask = Byte.MAX_VALUE; }

    public SkinState(Flag... flags) {
        for (Flag flag : flags)
            this.mask |= flag.getBit();
    }

    public byte getBitMask() { return mask; }

    public boolean isFlagSet(PlayerState.Flag flag) { return (mask & flag.getBit()) != 0; }

    public enum Flag {

        CAPE((byte) 1),
        JACKET((byte) 2),
        LEFT_SLEEVE((byte) 4),
        RIGHT_SLEEVE((byte) 8),
        LEFT_PANTS((byte) 16),
        RIGHT_PANTS((byte) 32),
        HAT((byte) 64);

        private final byte bit;

        Flag(byte bit) { this.bit = bit; }

        public byte getBit() { return bit; }

    }

}
