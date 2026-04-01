public class TaskReport {

    private int totalTasks;
    private int highCount;
    private int mediumCount;
    private int lowCount;

    // Constructor vacío
    public TaskReport() {
        this.totalTasks = 0;
        this.highCount = 0;
        this.mediumCount = 0;
        this.lowCount = 0;
    }

    // Constructor con valores
    public TaskReport(int totalTasks, int highCount, int mediumCount, int lowCount) {
        this.totalTasks = totalTasks;
        this.highCount = highCount;
        this.mediumCount = mediumCount;
        this.lowCount = lowCount;
    }

    // Getters
    public int getTotalTasks() {
        return totalTasks;
    }

    public int getHighCount() {
        return highCount;
    }

    public int getMediumCount() {
        return mediumCount;
    }

    public int getLowCount() {
        return lowCount;
    }

    // Setters
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public void setHighCount(int highCount) {
        this.highCount = highCount;
    }

    public void setMediumCount(int mediumCount) {
        this.mediumCount = mediumCount;
    }

    public void setLowCount(int lowCount) {
        this.lowCount = lowCount;
    }

    // Métodos para incrementar conteo
    public void incrementHigh() {
        highCount++;
        totalTasks++;
    }

    public void incrementMedium() {
        mediumCount++;
        totalTasks++;
    }

    public void incrementLow() {
        lowCount++;
        totalTasks++;
    }
}