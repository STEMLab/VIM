package lab.stem.vim.core;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lab.stem.vim.indoorGML.Point;

public class NavInfo {
    private double leftDistance;
    private double angleDirection;

    private int targetFloor;
    private String floorMovingType;

    private Point prvPoint;
    private Point curPoint;
    private Point nextPoint;
    private Point closeWithNextPoint;

    private Indicator indicator;

    private List<DetectedPoiInfo> nearByPoiList;
    private List<DetectedPoiInfo> nearBySafetyList;
    
    private boolean ignoreDetected;
    private boolean needFloorMoving;
    private boolean brailleBlocks;
    private boolean inDestinationTransition;
    
    private String distanceForm;

    public NavInfo(SharedPreferences sharedPreferences) {
        leftDistance = -1.0;
        angleDirection = -1.0;

        targetFloor = Integer.MIN_VALUE;
        floorMovingType = null;
        prvPoint = null;
        curPoint = null;
        nextPoint = null;

        this.indicator = new Indicator();

        nearByPoiList = new ArrayList<>();
        nearBySafetyList = new ArrayList<>();

        needFloorMoving = false;
        ignoreDetected = false;
        inDestinationTransition = false;

        this.distanceForm = sharedPreferences.getString("distanceForm","meter");
    }

    public double getLeftDistance() {
        return leftDistance;
    }

    public void setLeftDistance(double leftDistance) {
        this.leftDistance = leftDistance;
    }

    public boolean isNeedFloorMoving() {
        return needFloorMoving;
    }

    public void setNeedFloorMoving(boolean needFloorMoving) {
        this.needFloorMoving = needFloorMoving;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public void setTargetFloor(int targetFloor) {
        this.targetFloor = targetFloor;
    }

    public String getFloorMovingType() {
        return floorMovingType;
    }

    public void setFloorMovingType(String floorMovingType) {
        this.floorMovingType = floorMovingType;
    }

    public boolean isArrived() {
        return leftDistance == -1.0;
    }

    public boolean isAlmostReachAtDestination() {
        return leftDistance < 4.0 && inDestinationTransition;
    }

    public boolean isNeedTurn(){
        return !indicator.isFrontDirection(this.angleDirection);
    }

    public Point getPrvPoint() {
        return prvPoint;
    }

    public void setPrvPoint(Point prvPoint) {
        this.prvPoint = prvPoint;
    }

    public Point getCurPoint() {
        return curPoint;
    }

    public void setCurPoint(Point curPoint) {
        this.curPoint = curPoint;
    }

    public Point getNextPoint() {
        return nextPoint;
    }

    public void setNextPoint(Point nextPoint) {
        this.nextPoint = nextPoint;
    }

    private boolean isReadyToGetAngle() {
        return prvPoint != null && nextPoint != null && curPoint != null;
    }

    public boolean needNextTurningGuide() {
        return leftDistance <= 4.0 && getAngle() != null;
    }

    public Double getAngle() {
        if (!isReadyToGetAngle()) return null;
        double currentDirection = prvPoint.getAngle(curPoint);
        return curPoint.getZ() == nextPoint.getZ() ? indicator.getNeedTurnAngle(nextPoint, curPoint, currentDirection) : null;
    }

    public Double getNextAngle() {
        double nextDirection = curPoint.getAngle(nextPoint);
        return indicator.getNeedTurnAngle(closeWithNextPoint, nextPoint, nextDirection);
    }

    public double getAngleDirection() {
        return angleDirection;
    }

    public void setAngleDirection(double angleDirection) {
        this.angleDirection = angleDirection;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public boolean isBrailleBlocks() {
        return brailleBlocks;
    }

    public void setBrailleBlocks(boolean brailleBlocks) {
        this.brailleBlocks = brailleBlocks;
    }

    public List<DetectedPoiInfo> getNearByPoiList() {
        return nearByPoiList;
    }

    public void setNearByPoiList(List<DetectedPoiInfo> nearByPoiList) {
        this.nearByPoiList = nearByPoiList;
    }

    public List<DetectedPoiInfo> getNearBySafetyList() {
        return nearBySafetyList;
    }

    public void setNearBySafetyList(List<DetectedPoiInfo> nearBySafetyList) {
        this.nearBySafetyList = nearBySafetyList;
    }

    public boolean isIgnoreDetected() {
        return ignoreDetected;
    }

    public void setIgnoreDetected(boolean ignoreDetected) {
        this.ignoreDetected = ignoreDetected;
    }

    public Point getCloseWithNextPoint() {
        return closeWithNextPoint;
    }

    public void setCloseWithNextPoint(Point closeWithNextPoint) {
        this.closeWithNextPoint = closeWithNextPoint;
    }

    public String getDistanceForm() {
        return distanceForm;
    }

    public void setDistanceForm(String distanceForm) {
        this.distanceForm = distanceForm;
    }

    public boolean isInDestinationTransition() {
        return inDestinationTransition;
    }

    public void setInDestinationTransition(boolean inDestinationTransition) {
        this.inDestinationTransition = inDestinationTransition;
    }

    @Override
    public String toString() {
        return "NavInfo{" +
                "leftDistance=" + leftDistance +
                ", angleDirection=" + angleDirection +
                ", targetFloor=" + targetFloor +
                ", floorMovingType='" + floorMovingType + '\'' +
                ", prvPoint=" + prvPoint +
                ", curPoint=" + curPoint +
                ", nextPoint=" + nextPoint +
                ", closeWithNextPoint=" + closeWithNextPoint +
                ", indicator=" + indicator +
                ", nearByPoiList=" + nearByPoiList +
                ", nearBySafetyList=" + nearBySafetyList +
                ", ignoreDetected=" + ignoreDetected +
                ", needFloorMoving=" + needFloorMoving +
                ", brailleBlocks=" + brailleBlocks +
                ", inDestinationTransition=" + inDestinationTransition +
                ", distanceForm='" + distanceForm + '\'' +
                '}';
    }
}
