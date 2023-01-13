package me.losin6450.core;

import java.util.function.Consumer;

/**
 * The type Task.
 */
public class Task {

    private final int clock;
    private final Consumer<Task> runnable;
    /**
     * The Current.
     */
    public int current;
    /**
     * The Infinite.
     */
    public final boolean infinite;

    /**
     * Instantiates a new Task.
     *
     * @param clock    the clock
     * @param runnable the runnable
     * @param infinite the infinite
     */
    public Task(int clock, Consumer<Task> runnable, boolean infinite){
        this.clock = clock;
        this.runnable = runnable;
        this.current = 0;
        this.infinite = infinite;
    }

    /**
     * Gets clock.
     *
     * @return the clock
     */
    public int getClock() {
        return clock;
    }

    /**
     * Gets runnable.
     *
     * @return the runnable
     */
    public Consumer<Task> getRunnable() {
        return runnable;
    }

    /**
     * Is infinite boolean.
     *
     * @return the boolean
     */
    public boolean isInfinite() {
        return infinite;
    }
}
