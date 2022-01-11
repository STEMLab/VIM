package lab.stem.vim.core;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import lab.stem.vim.guideFactory.GuideInfo;
import lab.stem.vim.guideFactory.GuideInfoBroker;
import lab.stem.vim.indoorGML.CellSpace;
import lab.stem.vim.indoorGML.IndoorFeatures;
import lab.stem.vim.indoorGML.Point;
import lab.stem.vim.indoorGML.SpaceLayer;
import lab.stem.vim.indoorGML.State;
import lab.stem.vim.indoorGML.Transition;
import lab.stem.vim.observer.VIMIndoorFeaturesObserver;
import lab.stem.vim.observer.VIMIndoorFeaturesSubject;

public class VIMIndoorFeatures extends IndoorFeatures implements VIMIndoorFeaturesSubject {
    static String TAG = "VIMIndoorFeatures";
    private final List<VIMIndoorFeaturesObserver> VIMIndoorFeaturesObservers;
    private Graph graph;
    private String graphLayerId;
    private NavInfoMaker navInfoMaker;
    private final User user;

    private VIMSTATE state;

    private final SharedPreferences sharedPreferences;

    private final GuideInfoBroker guideInfoBroker;

    public VIMIndoorFeatures(User user, SharedPreferences sharedPreferences) {
        VIMIndoorFeaturesObservers = new ArrayList<>();
        this.user = user;
        this.sharedPreferences = sharedPreferences;
        this.state = VIMSTATE.STOP;
        this.guideInfoBroker = new GuideInfoBroker();
        guideInfoBroker.registerGuideInfoObserver(user);
        user.setIndoor4VIM(this);
    }

    @Override
    public void registerVIMIndoorFeaturesObserver(VIMIndoorFeaturesObserver observer) {
        VIMIndoorFeaturesObservers.add(observer);
    }

    @Override
    public void removeVIMIndoorFeaturesObserver(VIMIndoorFeaturesObserver observer) {
        VIMIndoorFeaturesObservers.remove(observer);
    }

    @Override
    public void notifyVIMIndoorFeaturesObserver() {
        VIMIndoorFeaturesObservers.forEach(VIMIndoorFeaturesObserver -> VIMIndoorFeaturesObserver.updateVIMState(this.state));
    }

    public boolean setState(VIMSTATE state) {
        if (state != null) {
            this.state = state;
            notifyVIMIndoorFeaturesObserver();
            return true;
        } else  return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initGraph(String layerId){
        this.graphLayerId = layerId;
        SpaceLayer spaceLayer  = this.getMultiLayeredGraph().getSpaceLayer(layerId);

        Map<String, List<Vertex>> vertices = new HashMap<>();

        Map<String, State> stateMap = spaceLayer.getNode().getStateMap();
        Map<String, Transition> transitionMap = spaceLayer.getEdge().getTransitionMap();

        for (String transitionId: transitionMap.keySet()) {
            if(transitionId.contains("REVERSE") || transitionId.contains("reverse")) continue;
            Transition transition = transitionMap.get(transitionId);

            Map<String,Point> connectMap = transition.getConnectMap();

            List<String> linkIds = new ArrayList<>(connectMap.keySet());

            double distance;
            if (transition.getDescription() != null && transition.getDescription().length() > 0 && !transition.getDescription().contains(sharedPreferences.getString("setUpPathAboutFloor","elevator"))){
                distance = 10000;
            }else {
                distance = stateMap.get(linkIds.get(0)).getPoint().getZ() == stateMap.get(linkIds.get(1)).getPoint().getZ()
                ? stateMap.get(linkIds.get(0)).getPoint().getDistance(stateMap.get(linkIds.get(1)).getPoint())
                : Double.MIN_VALUE;
            }


            Vertex newVertex = new Vertex(linkIds.get(1), distance, transitionMap.get(transitionId).getDescription());

            if (vertices.putIfAbsent(linkIds.get(0), new ArrayList<Vertex>() {{add(newVertex);}}) != null) {
                List<Vertex> newVertices = vertices.get(linkIds.get(0));
                Objects.requireNonNull(newVertices).add(newVertex);
                vertices.replace(linkIds.get(0), newVertices);
            }

            if (vertices.putIfAbsent(linkIds.get(1), new ArrayList<Vertex>() {{add(new Vertex(linkIds.get(0), distance, transitionMap.get(transitionId).getDescription()));}}) != null) {
                List<Vertex> newVertices = vertices.get(linkIds.get(1));
                if (newVertices != null) {
                    newVertices.add(new Vertex(linkIds.get(0), distance, transitionMap.get(transitionId).getDescription()));
                }
                vertices.replace(linkIds.get(1), newVertices);
            }
        }
        this.graph = new Graph();

        for (String stateId : vertices.keySet()) {
            graph.addVertex(stateId, vertices.get(stateId));
        }
    }

    public CellSpace getCellSpaceWherePointExist(Point point) {
        if (this.getPrimalSpaceFeatures() == null) return null;
        if (this.getPrimalSpaceFeatures().getCellSpaceMap() == null) return null;
        String cellSpaceId = this.getPrimalSpaceFeatures().getCellSpaceMap().keySet().stream().filter(id -> {
            CellSpace cellSpace = this.getPrimalSpaceFeatures().getCellSpaceMap().get(id);
            Point[] bottomOfPolygon = new Point[cellSpace.getPolygon().getPointList().size()];
            if (bottomOfPolygon.length <= 0) return false;
            bottomOfPolygon = cellSpace.getPolygon().getPointList().toArray(bottomOfPolygon);
            return bottomOfPolygon[0].getZ() == point.getZ() && GFG.isInside(bottomOfPolygon, bottomOfPolygon.length, point);
        }).findFirst().orElse(null);
        if (cellSpaceId == null) return null;
        return this.getPrimalSpaceFeatures().getCellSpaceMap().get(cellSpaceId);
    }

    public boolean setUpRoutePath(String destinationStateId){
        if (destinationStateId == null) this.navInfoMaker = null;
        else {
            State closestState = this.getMultiLayeredGraph().getClosestState(this.graphLayerId, user.getPoint());
            this.navInfoMaker = new NavInfoMaker(user, getShortestPath(closestState.getId(), destinationStateId, true), this.sharedPreferences);
            this.navInfoMaker.registerNavInfoMakerObserver(user);
        }
        return false;
    }

    public List<State> getShortestPath(String start, String finish, boolean isIncludeStart){
        List<State> shortestPathWithState = new ArrayList<>();
        if (start.equals(finish)) {
            shortestPathWithState.add(this.getMultiLayeredGraph().getState(finish));
            return shortestPathWithState;
        }
        List<String> shortestPath = this.graph.getShortestPath(start,finish, sharedPreferences.getString("setUpPathAboutFloor","elevator"));
        for (String stateId: shortestPath) {
            shortestPathWithState.add(this.getMultiLayeredGraph().getState(stateId));
        }

        if (isIncludeStart) shortestPathWithState.add(this.getMultiLayeredGraph().getState(start));

        return getRemovedDuplicateDirectionState(shortestPathWithState);
    }

    private List<State> getRemovedDuplicateDirectionState(List<State> stateList) {
        List<State> shortestPath = new ArrayList<>();
        for (int i = 0; i < stateList.size(); i++) {
            if (shortestPath.size() == 0 || i == stateList.size() -1) {
                shortestPath.add(stateList.get(i));
                continue;
            }

            Point lastPointInPath = shortestPath.get(shortestPath.size()-1).getPoint();
            if (lastPointInPath.getZ() != stateList.get(i).getPoint().getZ()){
                double floor = lastPointInPath.getZ();
                while (i < stateList.size() && floor == stateList.get(i).getPoint().getZ()) i++;
                shortestPath.add(stateList.get(i));
                continue;
            }

            if (lastPointInPath.getZ() != stateList.get(i + 1).getPoint().getZ()) {
                shortestPath.add(stateList.get(i));
                continue;
            }

            if (stateList.get(i).isDotBlock()) {
                shortestPath.add(stateList.get(i));
                continue;
            }

            double firstAngle = lastPointInPath.getAngle(stateList.get(i).getPoint());
            double secondAngle = lastPointInPath.getAngle(stateList.get(i + 1).getPoint());
            if (!(Math.abs(firstAngle - secondAngle) < 2.0)) shortestPath.add(stateList.get(i));
        }
        return shortestPath;
    }

    public boolean reSetRoute() {
        this.navInfoMaker = null;
        return true;
    }

    public List<GuideInfo> getGuideInfoList(String destinationStateId, boolean force, long systemTime) {
        long callTime = force ? Long.MAX_VALUE : systemTime;

        CellSpace cellSpaceExistUser = getCellSpaceWherePointExist(user.getPoint());

        if (navInfoMaker == null) {
            setUpRoutePath(destinationStateId);
        }
        if (!navInfoMaker.getDestinationId().equals(destinationStateId)) setUpRoutePath(destinationStateId);

        NavInfo navInfo = navInfoMaker.getInformationForGuide(cellSpaceExistUser, user.getPoint(), (double) user.getDir());
        navInfo.setBrailleBlocks(sharedPreferences.getBoolean("brailleBlocks", true));
        if (!navInfo.isArrived()) setUpDetectedPoiList(navInfo);
        return guideInfoBroker.takeAOrder(force, callTime, user, navInfo, sharedPreferences.getString("language", Locale.getDefault().getLanguage()).equals("ko"));
    }

    public List<GuideInfo> getNearbyPoi() {
        if (navInfoMaker == null) return null;
        NavInfo navInfo = new NavInfo(sharedPreferences);
        navInfo.setIgnoreDetected(true);
        setUpDetectedPoiList(navInfo);
        return guideInfoBroker.takeAOrderOnlyPoi(true, Long.MAX_VALUE, sharedPreferences.getString("language", Locale.getDefault().getLanguage()).equals("ko"), user, navInfo);
    }

    private Map<String,State> detectAroundPoi(SpaceLayer spaceLayer, Point point, double minDetectDistance) {
        if (spaceLayer == null) return null;

        Map<String, State> stateMap = spaceLayer.getNode().getStateMap();

        HashMap<String, State> detectedStateMap = new HashMap<>();
        for (String stateId : stateMap.keySet()) {
            Point statePoint = stateMap.get(stateId).getPoint();
            if (statePoint.getZ() == point.getZ()){
                double distanceFromPoint = point.getDistance(statePoint);
                if (distanceFromPoint <= minDetectDistance) {
                    detectedStateMap.put(stateId, stateMap.get(stateId));
                }
            }
        }

        return  detectedStateMap;
    }

    private void setUpDetectedPoiList(NavInfo navInfo) {
        Point userPoint = user.getPoint();
        double userDirection = user.getDir();
        Indicator indicator = navInfo.getIndicator();

        SpaceLayer safetyLayer = this.getMultiLayeredGraph().getSpaceLayer("safety");
        SpaceLayer landmarkLayer = this.getMultiLayeredGraph().getSpaceLayer("landmark");

        Map<String, State> detectedSafetyPoiMap = detectAroundPoi(safetyLayer, userPoint, 5.0);
        Map<String, State> detectedLandmarkPoiMap = detectAroundPoi(landmarkLayer, userPoint, 5.0);

        List<DetectedPoiInfo> detectedSafetyList = new ArrayList<>();
        List<DetectedPoiInfo> detectedLandmarkList = new ArrayList<>();

        if (sharedPreferences.getBoolean("instructionOfSafety",true)){
            detectedSafetyPoiMap.forEach((id, state) -> {
                double distance = Math.round(state.getPoint().getDistance(userPoint));
                double angle = indicator.getNeedTurnAngle(state.getPoint(), userPoint, userDirection);
                if (indicator.isFrontDirection(angle)) detectedSafetyList.add(new DetectedPoiInfo(id, state.getName(), distance, angle));
            });
        }

        if (sharedPreferences.getBoolean("instructionOfLandmark",true)){
            detectedLandmarkPoiMap.forEach((id, state) -> {
                double distance = Math.round(state.getPoint().getDistance(userPoint));
                double angle = indicator.getNeedTurnAngle(state.getPoint(), userPoint, userDirection);
                detectedLandmarkList.add(new DetectedPoiInfo(id, state.getName(), distance, angle));
            });
        }

        navInfo.setNearByPoiList(detectedLandmarkList);
        navInfo.setNearBySafetyList(detectedSafetyList);
    }

}
