package lab.stem.vim.indoorGML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transition extends GmlInfo {
    private String id;
    private double weight;
    private Map<String, Point> connectMap;

    public Transition(String id){
        this.id = id;
        this.weight = 0.0;
        this.connectMap = new HashMap<>();
    }

    public Transition(State firstState, State secondState){
        this.id = firstState.getId()+"-"+secondState.getId();
        this.weight = 0.0;
        Map<String, Point> connectMap = new HashMap<>();
        connectMap.put(firstState.getId(), firstState.getPoint());
        connectMap.put(secondState.getId(), secondState.getPoint());
        this.connectMap = connectMap;
    }

    public Transition(String description, String name) {
        super(description, name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Map<String, Point> getConnectMap() {
        return connectMap;
    }

    public void setConnectMap(Map<String, Point> connectMap) {
        this.connectMap = connectMap;
    }

    public boolean isExist(String stateId) {
        return this.connectMap.get(stateId) != null;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "id='" + id + '\'' +
                ", weight=" + weight +
                ", connectMap=" + connectMap.keySet() +
                '}';
    }

    // extend functions

    public String getCloseStateIdInTransition(Point point) {
        double closestDistance = Double.MAX_VALUE;
        String closestStateId = null;

        for (String stateId : this.connectMap.keySet()) {
            double tempDistance = this.connectMap.get(stateId).getDistance(point);
            if (closestDistance > tempDistance) {
                closestDistance = tempDistance;
                closestStateId = stateId;
            }
        }
        return closestStateId;
    }

    public boolean isStair(){
        boolean isStair = false;
        Double height = null;
        for (String stateId : connectMap.keySet()) {
            Double stateHeight = connectMap.get(stateId).getZ();
            if (height == null) {
                height = stateHeight;
                continue;
            }

            if (!height.equals(stateHeight)) isStair = true;
        }
        return isStair;
    }

    public boolean isReverse(){
        return this.getId().contains("reverse") || this.getId().contains("REVERSE") ;
    }

    public boolean isSameHeight(Point point) {
        boolean isSameHeight = false;
        for (String stateId : connectMap.keySet()) {
            double stateHeight = connectMap.get(stateId).getZ();
            isSameHeight = stateHeight == point.getZ();
        }
        return isSameHeight;
    }

    public double getHeight() {
        return 0;
    }

    public double getFloor(){
        return 0;
    }

//    public double getDistanceToPoint(Point point) {
//        List<Point> transitionPoints = new ArrayList<>();
//
//        for (String stateId: connectMap.keySet()) {
//            transitionPoints.add(connectMap.get(stateId));
//        }
//
//        final Point l1 = transitionPoints.get(0);
//        final Point l2 = transitionPoints.get(1);
//
//        double minX = l1.getX() <= l2.getX() ? l1.getX() : l2.getX();
//        double maxX = l1.getX() > l2.getX() ? l1.getX() : l2.getX();
//        double minY = l1.getY() <= l2.getY() ? l1.getY() : l2.getY();
//        double maxY = l1.getY() > l2.getY() ? l1.getY() : l2.getY();
//
//        if (minX - point.getX() > 1 || maxX - point.getX() < -1) return Double.MAX_VALUE;
//        if (minY - point.getY() > 1 || maxY - point.getY() < -1) return Double.MAX_VALUE;
//
//        if (transitionPoints.size() != 2) return Double.MAX_VALUE;
//        return Math.abs((l2.getX() - l1.getX()) * (l1.getY() - point.getY())
//                - (l1.getX() - point.getX()) * (l2.getY() - l1.getY()))
//                / Math.sqrt(Math.pow(l2.getX() - l1.getX(), 2) + Math.pow(l2.getY() - l1.getY(), 2));
//    }

    public double getDistanceFromPoint(Point point) {
        return getClosePoint(point).getDistance(point);
    }

    public Point getClosePoint(Point point) {
        List<Point> connectArray = new ArrayList<>();
        this.getConnectMap().forEach((id, p) -> connectArray.add(p));

        double x1 = Math.min(connectArray.get(0).getX(), connectArray.get(1).getX());
        double x2 = Math.max(connectArray.get(0).getX(), connectArray.get(1).getX());

        double y1 = Math.min(connectArray.get(0).getY(), connectArray.get(1).getY());
        double y2 = Math.max(connectArray.get(0).getY(), connectArray.get(1).getY());


        if (x2 - x1 == 0) {
            if (y1 <= point.getY() && point.getY() <= y2) return new Point(x2, point.getY(), point.getZ());
            else if (point.getY() < y1 ) {
                return new Point(x2, y1, point.getZ());
            } else {
                return new Point(x2, y2, point.getZ());
            }
        }

        if (y2 - y1 == 0) {
            if (x1 <= point.getX() && point.getX() <= x2) return new Point(point.getX(), y2, point.getZ());
            else if (point.getX() < x1 ) {
                return new Point(x1, y2, point.getZ());
            } else {
                return new Point(x2, y2, point.getZ());
            }
        }


        double slope = (y2 - y1) / (x2 - x1);

        double y_intercept = y1 - slope * x1;

        double reverseSlope = slope != 0 ? 1 / slope : 0;

        double reverse_y_intercept = point.getY() - reverseSlope * point.getX();

        double closestX = slope != 0 ? (reverse_y_intercept - y_intercept) / (slope - reverseSlope) : (reverse_y_intercept - y_intercept);
        double closestY = reverseSlope * closestX + reverse_y_intercept;
        
        if (closestX < x1) {
            closestX = x1;
        } else if (x2 < closestX) {
            closestX = x2;
        }
        
        if (closestY < y1) {
            closestY = y1;
        } else if (y2 < closestY) {
            closestY = y2;
        }

//        Log.e(TAG, "getClosestPoint: " + x1 + "," + y1 + "} {" + x2 + ":" + y2);
//        if (x1 <= closestX && closestX <= x2){
//            Log.d(TAG, "getClosestPoint: " + this.getId() + " : " + true);
//        }
//        Log.e(TAG, "getClosestPoint: "  + this.getId() + ":"+ point );

        return new Point(closestX, closestY, point.getZ());
    }

    public int compareTo(Transition t, Point p){
//        Log.d(TAG, "compareTo: " + this.getId() + ":" + t.getId());
        if (this.isSameHeight(p) && t.isSameHeight(p)){
            if (this.getDistanceFromPoint(p) == t.getDistanceFromPoint(p)) return 0;
            else if (this.getDistanceFromPoint(p) < t.getDistanceFromPoint(p)) return -1;
            return 1;
        } else if (this.isStair() || t.isStair()) return 1;
        else return 1;
    }
}
