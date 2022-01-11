package lab.stem.vim.indoorGML;

import java.util.Locale;

public class Point implements Comparable{
    private double x;
    private double y;
    private double z;

    public Point() {
        this.x = this.y = this.z = 0;
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0.0;
    }

    public Point(String pos) {
        String[] arrayOfPos = pos.split(" ");
        try {
            if (arrayOfPos.length != 3) throw new Exception("You need to check indoorGML data form.");
            this.x = Double.parseDouble(arrayOfPos[0]);
            this.y = Double.parseDouble(arrayOfPos[1]);
            this.z = Double.parseDouble(arrayOfPos[2]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Point point) {
        this.x = point.getX();
        this.y = point.getY();
        this.z = point.getZ();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getDistance(Point point) {
        return Math.sqrt(Math.pow(this.x - point.getX(), 2) + Math.pow(this.y - point.getY(), 2));
    }

    public double getAngle(Point point){
        final double deltaY = (this.y - point.getY());
        final double deltaX = (point.getX() - this.x);
        final double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360d + result) : result;
    }

    public void multiply(double param) {
        this.x = this.x * param;
        this.y = this.y * param;
    }

    public void addPoint(Point point) {
        this.x = this.x + point.x;
        this.y = this.y + point.y;
    }

    public boolean isDifferentHeight(Point point) {
        return this.z != point.getZ();
    }

    @Override
    public String toString() {
        return "{\"x\": "+ String.format(Locale.US,"%.2f",x) +",\"y\": "+ String.format(Locale.US,"%.2f",y) +", \"z\": "+ z +"}";
    }

    @Override
    public int compareTo(Object o) {
        Point point = (Point) o;
        return (int) Math.abs((this.x - point.getX()) + (this.y - point.getY()) + (this.z - point.getZ()));
    }
}
