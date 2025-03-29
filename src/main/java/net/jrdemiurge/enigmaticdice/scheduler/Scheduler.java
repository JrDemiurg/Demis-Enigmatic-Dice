package net.jrdemiurge.enigmaticdice.scheduler;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class Scheduler {
    private static final List<SchedulerTask> tasks = new ArrayList<>();

    public static void schedule(Runnable task, int delay, int period) {
        synchronized (tasks) {
            tasks.add(new SchedulerTask(delay, period, task));
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            synchronized (tasks) {
                Iterator<SchedulerTask> iterator = tasks.iterator();
                while (iterator.hasNext()) {
                    SchedulerTask st = iterator.next();
                    int newTicks = st.getTicksRemaining() - 1;
                    st.setTicksRemaining(newTicks);
                    if (newTicks <= 0) {
                        st.getTask().run();
                        if (st.isRepeating()) {
                            st.setTicksRemaining(st.getPeriod());
                        } else {
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }
}
