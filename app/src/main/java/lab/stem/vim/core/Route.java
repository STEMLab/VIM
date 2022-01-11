package lab.stem.vim.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import lab.stem.vim.indoorGML.Point;
import lab.stem.vim.indoorGML.State;
import lab.stem.vim.indoorGML.Transition;

public class Route {
    private List<State> states;
    private List<Transition> transitions;

    public Route(List<State> states) {
        this.states = states;
        constructTransition();
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    public boolean isStatesEmpty(){
        if (this.states == null) return true;
        return this.states.isEmpty();
    }

    public boolean isTransitionsEmpty(){
        if (this.transitions == null) return true;
        return this.transitions.isEmpty();
    }

    public State getDestination() {
        if (isStatesEmpty()) return null;
        return  this.states.get(0);
    }

    private void constructTransition(){
        if (isStatesEmpty()) return;
        if (this.transitions == null) this.transitions = new ArrayList<>();
        for (int i = 1; i < states.size(); i++) {
            transitions.add(new Transition(states.get(i), states.get(i-1)));
        }
    }

    public Transition getCloseTransition(double minDistance, Point point) {
        if (isTransitionsEmpty()) return null;
        if (transitions == null) return null;
        final double[] distance = {minDistance};
        AtomicReference<Transition> closestTransition = new AtomicReference<>();
        transitions.forEach(transition -> {
            if (transition.isReverse()) return;
            if (!transition.isSameHeight(point)) return;
            if (transition.isStair()) return;

            double distanceFromPoint = transition.getDistanceFromPoint(point);
            if (distanceFromPoint != Double.MAX_VALUE && distanceFromPoint <= distance[0]) {
                closestTransition.set(transition);
                distance[0] = distanceFromPoint;
            }
        });
        return closestTransition.get();
    }

    public State getCloseState(Point point, Transition transition) {
        State closeState;
        if (transition != null) closeState = getCloseStateInTransition(transition);
        else closeState = getCloseState(point);
        return closeState;
    }

    public State getCloseState(Point point) {
        Optional<State> optionalState = this.states.stream().min((s1, s2) -> s1.compareTo(s2, point));
        return optionalState.orElse(null);
    }

    public boolean isCloseFirstState(Point point) {
        Optional<State> optionalState = this.states.stream().min((s1, s2) -> s1.compareTo(s2, point));
        return optionalState.orElse(null) != null && this.states.indexOf(optionalState.get()) == states.size() - 1;
    }

    public State getCloseStateInTransition(Transition transition) {
        Map<String, Integer> stateListMap = new HashMap<>();
        for (int i = 0; i < states.size(); i++) {
            String id = states.get(i).getId();
            if (transition.getConnectMap().containsKey(id)) stateListMap.put(id, i);
        }
        Optional<String> stringOptional = stateListMap.keySet().stream().min((o1, o2) -> stateListMap.get(o1) - stateListMap.get(o2));
        return stringOptional.map(s -> states.get(stateListMap.get(s))).orElse(null);
    }

    public State getState(String type, State s) {
        State state = null;
        if (isStatesEmpty()) return null;
        int index = this.states.indexOf(s);
        switch (type){
            case "next":
                if (index > 0) state = this.states.get(index-1);
                break;
            case "previous":
                if (index > -1) state = this.states.get(index+1);
                break;
            default:
                break;
        }
        return state;
    }

    public boolean isDestination(State state){
        if (!isStatesEmpty()) return this.states.indexOf(state) == 0;
        else return false;
    }

    public boolean isSameWithStart(State state) {
        return this.states.indexOf(state) == this.states.size() - 1;
    }

    public boolean isNextStateStair(State state) {
        if (isStatesEmpty()) return false;
        if (isDestination(state)) return false;
        int index = this.states.indexOf(state);
        return state.getPoint().getZ() != this.states.get(index - 1).getPoint().getZ();
    }

    public double getTotalDistance(Point point) {
        double distance = 0.0;
        if (isStatesEmpty()) return distance;
        distance += point.getDistance(this.states.get(0).getPoint());
        if (this.states.size() == 1) return distance;

        for (int i = 0; i < this.states.size()-1; i++) {
            Point point1 = this.states.get(i).getPoint();
            Point point2 = this.states.get(i+1).getPoint();
            distance += point1.getDistance(point2);
        }
        return distance;
    }

    public int getNextFloor(State state) {
        int floor = Integer.MIN_VALUE;
        if (isStatesEmpty()) return floor;
        int index = this.states.indexOf(state);
        for (int i = index; i >= 0; i--) {
            if (floor == Integer.MIN_VALUE
                    || floor != this.states.get(i).getPoint().getZ())
                floor = (int) this.states.get(i).getPoint().getZ();
            else break;
        }
        return floor;
    }

    public boolean isCloseWithNextPoint(State state) {
        if (isStatesEmpty()) return false;
        int index = this.states.indexOf(state);
        if (index <= 0) return false;
        double distance = state.getPoint().getDistance(this.states.get(index-1).getPoint());
        return distance < 2;
    }

    public int getIndexOfState(State state) {
        return this.states.indexOf(state);
    }
}
