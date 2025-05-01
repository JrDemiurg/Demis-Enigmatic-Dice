package net.jrdemiurge.enigmaticdice.item.custom.unequalexchange;

import net.jrdemiurge.enigmaticdice.Config;

public class UnequalExchangeData {

    private int hitCount = 0;
    private int timeLeftTicks = -10;

    public void onHit() {
        hitCount++;
        timeLeftTicks = 20 * Config.UnequalExchangeDebuffDuration;
    }

    public void tick() {
        if (timeLeftTicks > 0) {
            timeLeftTicks--;
        }
    }

    public boolean isExpired() {
        return timeLeftTicks <= 0 && timeLeftTicks != -10;
    }

    public int getHitCount() {
        return hitCount;
    }

    public int getTimeLeftTicks() {
        return timeLeftTicks;
    }

    public void reset() {
        hitCount = 0;
        timeLeftTicks = -10;
    }
}
