package net.jrdemiurge.enigmaticdice.scheduler;

public class SchedulerTask {
    private int ticksRemaining;
    private final Runnable task;
    private final int period;
    private int repeatCount;

    public SchedulerTask(Runnable task, int delay, int period, int repeatCount) {
        this.ticksRemaining = delay;
        this.period = period;
        this.task = task;
        this.repeatCount = repeatCount;
    }

    public boolean isRepeating() {
        return period > 0 && repeatCount > 0;
    }

    public int getTicksRemaining() {
        return ticksRemaining;
    }

    public void setTicksRemaining(int ticksRemaining) {
        this.ticksRemaining = ticksRemaining;
    }

    public int getPeriod() {
        return period;
    }

    public Runnable getTask() {
        return task;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void decrementRepeatCount() {
        if (repeatCount > 0) {
            repeatCount--;
        }
    }
}
