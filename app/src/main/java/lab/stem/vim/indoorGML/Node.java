package lab.stem.vim.indoorGML;

import java.util.HashMap;
import java.util.Map;

public class Node {
    private String id;
    private Map<String, State> stateMap;

    public Node(String id) {
        this.id = id;
        this.stateMap = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, State> getStateMap() {
        return stateMap;
    }

    public void setStateMap(Map<String, State> stateMap) {
        this.stateMap = stateMap;
    }

    // extend functions
    public State getClosestState(Point point) {
        double closestDistance = Double.MAX_VALUE;
        State closestState = null;

        for (String stateId : stateMap.keySet()) {
            if(stateMap.get(stateId).getPoint().getZ() != point.getZ()) continue;
            double tempDistance = Math.sqrt(Math.pow(point.getX() - stateMap.get(stateId).getPoint().getX(), 2) + Math.pow(point.getY() - stateMap.get(stateId).getPoint().getY(), 2));

            if (closestDistance > tempDistance) {
                closestDistance = tempDistance;
                closestState = stateMap.get(stateId);
            }
        }

        return closestState;
    }
}
