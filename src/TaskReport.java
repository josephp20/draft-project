/**
 * Represents a summary report of tasks by priority.
 */
public class TaskReport {

    /** Total number of tasks */
    private int totalTasks;

    /** Number of low priority tasks */
    private int lowCount;

    /** Number of medium priority tasks */
    private int mediumCount;

    /** Number of high priority tasks */
    private int highCount;

    /**
     * Constructor to initialize task report.
     * @param totalTasks total tasks
     * @param lowCount low priority count
     * @param mediumCount medium priority count
     * @param highCount high priority count
     */
    public TaskReport(int totalTasks, int lowCount, int mediumCount, int highCount) {
        this.totalTasks = totalTasks;
        this.lowCount = lowCount;
        this.mediumCount = mediumCount;
        this.highCount = highCount;
    }

    /** @return total tasks */
    public int getTotalTasks() {
        return totalTasks;
    }

    /** @return low priority count */
    public int getLowCount() {
        return lowCount;
    }

    /** @return medium priority count */
    public int getMediumCount() {
        return mediumCount;
    }

    /** @return high priority count */
    public int getHighCount() {
        return highCount;
    }
}