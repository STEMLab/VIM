package lab.stem.vim.indoorGML;

import java.util.HashMap;
import java.util.Map;

public class Edge {
    private String id;
    private Map<String, Transition> transitionMap;

    public Edge(String id) {
        this.id = id;
        this.transitionMap = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Transition> getTransitionMap() {
        return transitionMap;
    }

    public void setTransitionMap(Map<String, Transition> transitionMap) {
        this.transitionMap = transitionMap;
    }

    // extend functions

    public Transition getClosestTransition(Point point) {
        double closestDistance = 10.0;
        Transition closestTransition = null;
        for (String transitionId: transitionMap.keySet()) {
            if (transitionMap.get(transitionId).isSameHeight(point)
                    && !transitionMap.get(transitionId).isStair()
                    && !transitionMap.get(transitionId).isReverse()) {
                double tempDistance = transitionMap.get(transitionId).getDistanceFromPoint(point);
//                Log.d("TAG", "getClosestTransition: " + transitionId + " : " + tempDistance);

                if (closestDistance > tempDistance) {
                    closestDistance = tempDistance;
                    closestTransition = transitionMap.get(transitionId);
                }
            }
        }
        return closestTransition;
    }
}
