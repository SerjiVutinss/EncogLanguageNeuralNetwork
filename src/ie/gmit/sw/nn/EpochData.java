package ie.gmit.sw.nn;

public class EpochData {

    private int index;
    private double error;
    private double duration;

    public EpochData(int index, double error, double duration) {
        this.index = index;
        this.error = error;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return String.format("Epoch #%04d; Error: %f; Duration: %03.5fs", index, error, duration);
    }

    public String toRowData() {
        return String.format("%d, %f, %f", index, error, duration);
    }

}
