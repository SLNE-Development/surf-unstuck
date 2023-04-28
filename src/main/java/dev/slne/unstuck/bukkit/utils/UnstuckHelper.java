package dev.slne.unstuck.bukkit.utils;

import java.util.UUID;

import org.bukkit.scheduler.BukkitTask;

public class UnstuckHelper {
    private UUID uuid;
    private String code;
    private BukkitTask task;

    public UnstuckHelper(UUID uuid, String code) {
        this.uuid = uuid;
        this.code = code;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCode() {
        return code;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void setTask(BukkitTask task) {
        cancelTask();

        this.task = task;
    }

    public void cancelTask() {
        if (hasTask()) {
            task.cancel();
        }
    }

    public boolean hasTask() {
        try {
            return task != null && !task.isCancelled();
        } catch (Exception exception) {
            return false;
        }
    }
}
