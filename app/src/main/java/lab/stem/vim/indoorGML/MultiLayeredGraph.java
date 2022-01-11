package lab.stem.vim.indoorGML;

import java.util.List;
import java.util.Map;

public class MultiLayeredGraph {
    private String id;
    private Map<String, SpaceLayer> spaceLayerMap;
    private Map<String, InterLayerConnection> interLayerConnectionMap;

    public MultiLayeredGraph(String id) {
        this.id = id;
    }

    public Map<String, SpaceLayer> getSpaceLayerMap() {
        return spaceLayerMap;
    }

    public void setSpaceLayerMap(Map<String, SpaceLayer> spaceLayerMap) {
        this.spaceLayerMap = spaceLayerMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, InterLayerConnection> getInterLayerConnectionMap() {
        return interLayerConnectionMap;
    }

    public void setInterLayerConnectionMap(Map<String, InterLayerConnection> interLayerConnectionMap) {
        this.interLayerConnectionMap = interLayerConnectionMap;
    }

    @Override
    public String toString() {
        return "MultiLayeredGraph{" +
                "id='" + id + '\'' +
                '}';
    }

    // extend functions
    public SpaceLayer getSpaceLayer(String id) {
        return getSpaceLayerMap().get(id);
    }
    public State getState(String id) {
        Map<String, SpaceLayer> spaceLayerMap = this.getSpaceLayerMap();
        return spaceLayerMap
                .values().stream()
                .filter(spaceLayer -> spaceLayer.getNode().getStateMap().get(id) != null)
                .findFirst().get().getNode().getStateMap().get(id);
    }
    public State getClosestState(String layerId, Point point) {
        SpaceLayer spaceLayer  = getSpaceLayer(layerId);
        Transition closestTransition = spaceLayer.getEdge().getClosestTransition(point);

        if (closestTransition != null) {
            return spaceLayer.getNode().getStateMap().get(closestTransition.getCloseStateIdInTransition(point));
        } else {
            return spaceLayer.getNode().getClosestState(point);
        }
    }
    public String getInterConnectedWithState(String typeOfTopoExpression, String firstLayerId, String secondLayerId, String stateId) {
        String interConnectedStateWithState = null;
        Map<String, InterLayerConnection> interLayerConnectionMap = this.getInterLayerConnectionMap();
        for (String interLayerConnectionId: interLayerConnectionMap.keySet()) {
            InterLayerConnection interLayerConnection = interLayerConnectionMap.get(interLayerConnectionId);
            List<String> connectedLayers = interLayerConnection.getConnectedLayers();
            if (connectedLayers.size() != 2) continue;
            if (connectedLayers.get(0).equals(firstLayerId)
                    && connectedLayers.get(1).equals(secondLayerId)
                    && interLayerConnection.getTypeOfTopoExpression().toUpperCase().equals(typeOfTopoExpression.toUpperCase())){

                List<String> interConnects = interLayerConnection.getInterConnects();
                if (interConnects.size() != 2) continue;
                if (interConnects.get(1).equals(stateId)) {
                    interConnectedStateWithState = interConnects.get(0);
                    break;
                }
            }
        }
        return interConnectedStateWithState;
    }

}
