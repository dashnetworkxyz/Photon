package xyz.dashnetwork.photon.npc.animator;

public class PlayerState {

    private boolean fire, crouch, sprint, interact, invisible, glowing;

    public byte toBitMask() {
        byte mask = 0x00;

        if (fire) mask += 0x01;
        if (crouch) mask += 0x02;
        if (sprint) mask += 0x08;
        if (interact) mask += 0x10;
        if (invisible) mask += 0x20;
        if (glowing) mask += 0x40;

        return mask;
    }

}
