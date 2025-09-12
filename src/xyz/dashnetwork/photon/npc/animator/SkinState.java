package xyz.dashnetwork.photon.npc.animator;

public class SkinState {

    private boolean cape, jacket, leftSleeve, rightSleeve, leftPants, rightPants, hat;

    public SkinState() {
        cape = true;
        jacket = true;
        leftSleeve = true;
        rightSleeve = true;
        leftPants = true;
        rightPants = true;
        hat = true;
    }

    public byte toBitMask() {
        byte mask = 0x00;

        if (cape) mask += 0x01;
        if (jacket) mask += 0x02;
        if (leftSleeve) mask += 0x04;
        if (rightSleeve) mask += 0x08;
        if (leftPants) mask += 0x10;
        if (rightPants) mask += 0x20;
        if (hat) mask += 0x40;

        return mask;
    }

}
