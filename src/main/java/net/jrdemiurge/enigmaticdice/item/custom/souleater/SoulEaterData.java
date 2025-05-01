package net.jrdemiurge.enigmaticdice.item.custom.souleater;

import net.jrdemiurge.enigmaticdice.Config;

public class SoulEaterData {

    private float hpCount = 0;
    private int timeLeftTicks = -10;

    public void onKill(float additionalHp) {
        hpCount = additionalHp;
        timeLeftTicks = 20 * Config.SoulEaterMaxHealthBuffDuration;
    }

    public void tick() {
        if (timeLeftTicks > 0) {
            timeLeftTicks--;
        }
    }

    public boolean isExpired() {
        return timeLeftTicks <= 0 && timeLeftTicks != -10;
    }

    public float getHpCount() {
        return hpCount;
    }

    public void reset() {
        hpCount = 0;
        timeLeftTicks = -10;
    }
}
