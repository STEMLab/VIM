package lab.stem.vim.core;

import java.util.Locale;

import lab.stem.vim.indoorGML.Point;

public class Indicator {
    public double getAngleToPoint(Point cur, Point target) {
        return target.getAngle(cur);
    }
    public double getNeedTurnAngle(Point p1, Point p2, double direction){
        return getAngleDistance(direction, p2.getAngle(p1));
    }
    private double getAngleDistance(double a1, double a2) {
        double difference = Math.abs(a1 -a2) % 360;
        double direction;

        if (a1 > a2) {
            direction = difference < 180 ? -1 : 1;
        } else {
            direction = difference < 180 ? 1 : -1;
        }

        double distance = difference > 180 ? 360 - difference : difference;
        return distance*direction;
    }
    public double getAngleOfVIMForm(double angleOfPoint, double direction){
        double vimAngleOfPoint = angleOfPoint > 180.0 ? angleOfPoint - 180.0 : angleOfPoint + 180.0;
        return vimAngleOfPoint > direction ? direction - vimAngleOfPoint + 360.0 : direction - vimAngleOfPoint;
    }
    public boolean isFrontDirection(double angle) {
        return Math.abs(angle) <= 30;
    }
    public boolean isFront(double angle) {
        return !(30 < angle && angle < 330);
    }
    public String getAngleToWordDirection(boolean isKorean, boolean turning, double angle) {
        String direction;
        if (30< angle && angle <= 150) {
            direction = isKorean ? (turning ? "좌회전하여" : "왼편") : (turning ? "Turn left" : "left");
        } else if (-150 <= angle && angle < -30){
            direction = isKorean ? (turning ? "우회전하여" : "오른편") : (turning ? "Turn right" : "right");
        } else if (Math.abs(angle) <= 30){
            direction = isKorean ? (turning ? "직진하여" : "전방") : (turning ? "Go ahead" : "ahead");
        } else {
            direction = isKorean ? (turning ? "뒤 돌아" : "후방") : (turning ? "Turn behind" : "behind");

        }
        return direction;
    }
    public String getLeftDistanceWord(String distanceForm, double distance) {
        int leftDistance;
        double param = getParamDistanceForm(distanceForm);

        leftDistance = (int) Math.round(distance * param);
        leftDistance = Math.max(leftDistance, 1);

        return String.format(Locale.US,"%d ", leftDistance) + distanceForm;
    }
    public boolean isAlmostReach(String distanceForm, double lefDistance) {
        double minDistance = 4.0;
        double param = getParamDistanceForm(distanceForm);
        return lefDistance * param < minDistance * param;
    }
    private double getParamDistanceForm(String distanceForm) {
        double param;

        switch (distanceForm) {
            case "feet":
                param = 3.28084;
                break;
            case "step":
                param = 0.75;
                break;
            case "meter":
            default:
                param = 1;
                break;
        }
        return param;
    }
}
