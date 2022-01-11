package lab.stem.vim.core;

public class DetectedPoiInfo {
    private String id;
    private String name;
    private double distance;
    private double angle;
    private long time;

    public DetectedPoiInfo(String id, String name, double distance, double angle) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.angle = angle;
        this.time = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}