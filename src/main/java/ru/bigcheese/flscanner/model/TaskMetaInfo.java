package ru.bigcheese.flscanner.model;

public class TaskMetaInfo {

    private final String name;
    private final boolean isScheduled;
    private final long created;

    public TaskMetaInfo(String name, boolean isScheduled) {
        this(name, isScheduled, System.currentTimeMillis());
    }

    public TaskMetaInfo(String name, boolean isScheduled, long created) {
        this.name = name;
        this.isScheduled = isScheduled;
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public long getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskMetaInfo that = (TaskMetaInfo) o;

        if (isScheduled != that.isScheduled) return false;
        if (created != that.created) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (isScheduled ? 1 : 0);
        result = 31 * result + (int) (created ^ (created >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "TaskMetaInfo{" +
                "name='" + name + '\'' +
                ", isScheduled=" + isScheduled +
                ", created=" + created +
                '}';
    }
}
