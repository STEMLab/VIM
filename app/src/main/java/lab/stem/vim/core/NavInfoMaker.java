package lab.stem.vim.core;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lab.stem.vim.indoorGML.CellSpace;
import lab.stem.vim.indoorGML.Point;
import lab.stem.vim.indoorGML.State;
import lab.stem.vim.indoorGML.Transition;
import lab.stem.vim.observer.NavInfoMakerObserver;
import lab.stem.vim.observer.NavInfoMakerSubject;

enum NavInfoType {
    SPATIAL_CAN_MOVE_FLOOR,
    CLOSE_STATE,
    CLOSE_TRANSITION,
    NONE_IN_TRANSITION,
}

public class NavInfoMaker implements NavInfoMakerSubject {
    private final Route route;
    private final Indicator indicator;
    private final List<NavInfoMakerObserver> navInfoMakerObservers;
    private final SharedPreferences sharedPreferences;

    @Override
    public void registerNavInfoMakerObserver(NavInfoMakerObserver observer) {
        navInfoMakerObservers.add(observer);
    }

    @Override
    public void notifyUpdateRoute(Route route) {
        navInfoMakerObservers.forEach(navInfoMakerObserver -> navInfoMakerObserver.updateRoute(route));
    }

    public NavInfoMaker(User user, List<State> stateList, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.indicator = new Indicator();
        this.navInfoMakerObservers = new ArrayList<>();
        this.navInfoMakerObservers.add(user);

        this.route = new Route(stateList);
        notifyUpdateRoute(route);
    }

    public String getDestinationId() {
        return this.route.getDestination().getId() != null ? this.route.getDestination().getId() : "";
    }

    public NavInfo getInformationForGuide(CellSpace currentExistCellSpace, Point currentPoint, Double currentDirection) {
        Transition closeTransition = this.route.getCloseTransition(2.0, currentPoint);
        State closeState = route.getCloseState(currentPoint, closeTransition);
        NavInfo navInfo;
        switch (getNavInfoType(currentExistCellSpace, currentPoint, closeTransition, closeState)){
            case NONE_IN_TRANSITION:
                navInfo = getMoveToTransitionInformation(closeState, currentPoint, currentDirection);
                break;
            case CLOSE_STATE:
                navInfo = getInformationCloseState(closeState, currentDirection);
                break;
            case CLOSE_TRANSITION:
                navInfo = getInformationInTransition(closeTransition, closeState, currentPoint, currentDirection);
                break;
            case SPATIAL_CAN_MOVE_FLOOR:
                navInfo = getMoveToFloorInformation(closeState);
                break;
            default:
                navInfo = null;
                break;
        }
        return navInfo;
    }

    private NavInfoType getNavInfoType(CellSpace currentExistCellSpace, Point currentPoint, Transition closeTransition, State closeState) {
        NavInfoType navInfoType;
        if (currentExistCellSpace != null){
            Log.e("TAG", "getNavInfoType: " + route.isDestination(closeState) + " : " + route.isNextStateStair(closeState) + " : " + (currentExistCellSpace != null)  + " : " + (currentExistCellSpace.getName().toLowerCase().contains("stair") || currentExistCellSpace.getName().toLowerCase().contains("elevator") || currentExistCellSpace.getName().toLowerCase().contains("escalator")));
        }
        if (!route.isDestination(closeState)
                && route.isNextStateStair(closeState)
                && currentExistCellSpace != null
                && (currentExistCellSpace.getName().toLowerCase().contains("stair") || currentExistCellSpace.getName().toLowerCase().contains("elevator") || currentExistCellSpace.getName().toLowerCase().contains("escalator"))) {
            navInfoType = NavInfoType.SPATIAL_CAN_MOVE_FLOOR;
        } else if (closeState.getPoint().getDistance(currentPoint) <= 1.0) {
            navInfoType = NavInfoType.CLOSE_STATE;
        } else if (closeTransition == null) {
            navInfoType = NavInfoType.NONE_IN_TRANSITION;
        } else {
            navInfoType = NavInfoType.CLOSE_TRANSITION;
        }
        Log.d("TAG", "getNavInfoType: " + navInfoType);
        return navInfoType;
    }

    private NavInfo getMoveToFloorInformation(State state) {
        NavInfo navInfo = new NavInfo(sharedPreferences);
        navInfo.setNeedFloorMoving(true);

        int targetFloor = route.getNextFloor(state);

        navInfo.setTargetFloor(targetFloor);
        navInfo.setFloorMovingType(state.getDescription());

        if (!route.isSameWithStart(state)) navInfo.setPrvPoint(route.getState("previous",state).getPoint());
        navInfo.setCurPoint(state.getPoint());
        if (!route.isDestination(state)) navInfo.setNextPoint(route.getState("next",state).getPoint());
        return navInfo;
    }

    private NavInfo getMoveToTransitionInformation(State closestState, Point currentPoint, double currentDirection) {
        NavInfo navInfo = new NavInfo(sharedPreferences);

        Point nextPoint;

        double deltaX = Math.abs(closestState.getPoint().getX() - currentPoint.getX());
        double deltaY = Math.abs(closestState.getPoint().getY() - currentPoint.getY());

        if (deltaX > 1.0 && deltaX < deltaY) {
            nextPoint = new Point(closestState.getPoint().getX(), currentPoint.getY(), currentPoint.getZ());
        } else if (deltaY > 1.0 && deltaY < deltaX){
            nextPoint = new Point(currentPoint.getX(), closestState.getPoint().getY(), currentPoint.getZ());
        } else {
            nextPoint = closestState.getPoint();
        }

        if (nextPoint != null) {
            double angle = indicator.getNeedTurnAngle(nextPoint, currentPoint, currentDirection);
            navInfo.setAngleDirection(angle);
            navInfo.setLeftDistance(nextPoint.getDistance(currentPoint));
        }

        return navInfo;
    }

    private NavInfo getInformationCloseState(State closestState, double currentDirection) {
        NavInfo navInfo = new NavInfo(sharedPreferences);
        if (!route.isDestination(closestState)){
            State prvState = route.getState("previous",closestState);
            State nextState = route.getState("next",closestState);
            if (prvState!=null) navInfo.setPrvPoint(prvState.getPoint());
            if (nextState!=null) {
                navInfo.setNextPoint(nextState.getPoint());
                navInfo.setLeftDistance(nextState.getPoint().getDistance(closestState.getPoint()));
            }
            navInfo.setCurPoint(closestState.getPoint());
            if (!route.isSameWithStart(closestState)) currentDirection = prvState != null ? prvState.getPoint().getAngle(closestState.getPoint()) : 0;

            double angle = indicator.getNeedTurnAngle(nextState != null ? nextState.getPoint() : new Point(), closestState.getPoint(), currentDirection);
            navInfo.setAngleDirection(angle);
        }
        return navInfo;
    }

    private NavInfo getInformationInTransition(Transition closeTransition, State closeState, Point currentPoint, double currentDirection) {
        List<State> stateList = route.getStates();
        int indexOfState = stateList.indexOf(closeState);

        NavInfo navInfo = new NavInfo(sharedPreferences);

        if (closeTransition.isExist(closeState.getId())) {
            int connectedStateIndex = route.getIndexOfState(closeState);

            if (connectedStateIndex != -1 && connectedStateIndex < indexOfState){
                indexOfState = connectedStateIndex;
            }

            double angle = indicator.getNeedTurnAngle(stateList.get(indexOfState).getPoint(), stateList.get(indexOfState+1).getPoint(), currentDirection);
            navInfo.setAngleDirection(angle);
            navInfo.setLeftDistance(stateList.get(indexOfState).getPoint().getDistance(currentPoint));

            if (indexOfState < stateList.size() - 1) navInfo.setPrvPoint(stateList.get(indexOfState+1).getPoint());
            navInfo.setCurPoint(stateList.get(indexOfState).getPoint());
            if (indexOfState > 0) {
                navInfo.setNextPoint(stateList.get(indexOfState - 1).getPoint());
                if (route.isCloseWithNextPoint(stateList.get(indexOfState)) && 2 <= indexOfState){
                    navInfo.setCloseWithNextPoint(stateList.get(indexOfState-2).getPoint());
                }
            } else {
                navInfo.setInDestinationTransition(true);
            }
        } else {
            String stateId = closeTransition.getId().split("-")[1];
            double angle = indicator.getNeedTurnAngle(closeTransition.getConnectMap().get(stateId), currentPoint, currentDirection);
            navInfo.setAngleDirection(angle);
            navInfo.setLeftDistance(closeTransition.getConnectMap().get(stateId).getDistance(currentPoint));
        }
        return navInfo;
    }
}
