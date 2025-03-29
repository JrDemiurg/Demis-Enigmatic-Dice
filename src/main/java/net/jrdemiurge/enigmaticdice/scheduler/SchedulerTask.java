package net.jrdemiurge.enigmaticdice.scheduler;

public class SchedulerTask {
    private int ticksRemaining;
    private final Runnable task;
    private final int period;

    public SchedulerTask(int delay, int period, Runnable task) {
        this.ticksRemaining = delay;
        this.period = period;
        this.task = task;
    }

    public boolean isRepeating() {
        return period > 0;
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
}
