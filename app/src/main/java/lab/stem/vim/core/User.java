package lab.stem.vim.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import lab.stem.vim.log.UserLog;
import lab.stem.vim.indoorGML.Edge;
import lab.stem.vim.indoorGML.Point;
import lab.stem.vim.indoorGML.Transition;
import lab.stem.vim.observer.GuideInfoObserver;
import lab.stem.vim.observer.NavInfoMakerObserver;

public class User implements NavInfoMakerObserver, GuideInfoObserver {
    private final Point point;
    private float yawAngle;
    private float dir;
    private long time;

    private final Set<String> detectedPOI;

    private VIMSTATE vimState;
    private DestinationInfo destinationInfo;
    private Edge networkEdge;

    private Route route;

    private lab.stem.vim.core.VIMIndoorFeatures VIMIndoorFeatures;
    private boolean wantExplain;
    private long resetTime;

    private final UserLog userLog;

    public User() {
        this.point = new Point(0, 0, 0);
        this.dir = 0;
        this.time = 0;
        this.yawAngle = 0;
        this.detectedPOI = new HashSet<>();
        this.userLog = new UserLog();
        this.vimState = VIMSTATE.STOP;
        this.wantExplain = false;
        this.route = null;
        this.resetTime = 0;
    }

    public void updateVIMState(VIMSTATE vimState) {
        this.vimState = vimState;
    }

    public void set(double x, double y, double z, float dir, long time) {
        if (this.vimState != null && this.vimState.equals(VIMSTATE.FLOOR_MOVING)) {
            this.point.setZ(z);
        } else if (route != null && !route.isStatesEmpty() && !route.isTransitionsEmpty() && !route.isCloseFirstState(point)) {
            Transition transition = route.getCloseTransition(4.0, new Point(x,y,z));
            if (transition != null){
                this.point.set(transition.getClosePoint(new Point(x, y, z)));
            } else {
                this.point.set(x, y, z);
                if (time - this.resetTime > 15000) {
                    this.resetTime = time;
                    this.VIMIndoorFeatures.reSetRoute();
                }
            }
        } else {
            if (networkEdge != null){
                Transition transition = networkEdge.getClosestTransition(new Point(x, y, z));
                if (transition != null){
                    this.point.set(transition.getClosePoint(new Point(x, y, z)));
                } else {
                    this.point.set(x, y, z);
                }
            } else {
                this.point.set(x, y, z);
            }
        }
        this.dir = dir;
        this.time = time;
    }

    public Point getPoint() { return point; }

    public float getDir() {
        return dir;
    }

    public long getTime() {
        return time;
    }

    public float getYawAngle() {
        return yawAngle;
    }

    public Set<String> getDetectedPOI() {
        return detectedPOI;
    }

    public void initializeDetectPOI() {
        detectedPOI.clear();
//        return detectedPOI.size() == 0 ? true : false;
    }

    public DestinationInfo getDestinationInfo() {
        return destinationInfo;
    }

    public void setDestinationInfo(DestinationInfo destinationInfo) {
        this.destinationInfo = destinationInfo;
        if (destinationInfo.hasTarget()) this.detectedPOI.add(destinationInfo.getTargetId());
        wantExplain = true;
    }

    public void setNetworkEdge(Edge networkEdge) {
        this.networkEdge = networkEdge;
    }

    public void saveUserLog(){
        this.userLog.writeLogFile();
    }

    public void pushGuideMessageLog(String guide) {
        this.userLog.addLog(this.point, this.yawAngle, this.dir, this.time, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US).format(new Date()), guide);
    }

    public void setIndoor4VIM(lab.stem.vim.core.VIMIndoorFeatures VIMIndoorFeatures) {
        this.VIMIndoorFeatures = VIMIndoorFeatures;
    }

    public boolean isWantExplain() {
        return wantExplain;
    }

    public void setWantExplain(boolean wantExplain) {
        this.wantExplain = wantExplain;
    }

    public double getTotalDistance(){
        return this.route.getTotalDistance(this.point);
    }

    @Override
    public void updateRoute(Route route) {
        this.route = route;
        this.set(this.point.getX(), this.point.getY(), this.point.getZ(), this.yawAngle, this.time);
    }

    @Override
    public void updateState(VIMSTATE state) {
        this.vimState = state;
    }

    @Override
    public String toString() {
        return "User{" +
                "point=" + point +
                ", dir=" + dir +
                ", yawAngle=" + yawAngle +
                ", time=" + time +
                '}';
    }
}