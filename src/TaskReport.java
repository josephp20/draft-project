public class TaskReport {
    private int totalTasks;
    private int lowCount;
    private int mediumCount;
    private int highCount;

    public TaskReport(int totalTasks, int lowCount, int mediumCount, int highCount) {
        this.totalTasks = totalTasks;
        this.lowCount = lowCount;
        this.mediumCount = mediumCount;
        this.highCount = highCount;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getLowCount() {
        return lowCount;
    }

    public int getMediumCount() {
        return mediumCount;
    }

    public int getHighCount() {
        return highCount;
    }
}