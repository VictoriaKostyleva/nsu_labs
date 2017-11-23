public class Task {
    private final long length;
    private final long startForCount;
    private final long endForCount;

    public Task(long length, long startForCount, long end) {
        this.length = length;
        this.startForCount = startForCount;
        this.endForCount = end;
    }

    public long getLength() {
        return length;
    }

    public long getStartForCount() {
        return startForCount;
    }

    public long getEndForCount() {
        return endForCount;
    }
}