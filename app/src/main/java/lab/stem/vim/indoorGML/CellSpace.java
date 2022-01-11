package lab.stem.vim.indoorGML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CellSpace extends GmlInfo {
    private String id;
    private Polygon polygon;

    public CellSpace(String id, String description, String name, Polygon polygon) {
        super(description, name);
        this.id = id;
        this.polygon = polygon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    private State getDuality(MultiLayeredGraph multiLayeredGraph) {
        AtomicReference<State> duality = new AtomicReference<>();
        Map<String, SpaceLayer> spaceLayerMap = multiLayeredGraph.getSpaceLayerMap();
        spaceLayerMap.keySet().forEach(spaceLayerId -> spaceLayerMap.get(spaceLayerId).getNode().getStateMap().forEach((stateId, state) -> {
            if (state.getDuality() != null && state.getDuality().equals(this.getId())) {
                duality.set(state);
            }
        }));
        return duality.get();
    }

    public List<String> getContainedStateIds(MultiLayeredGraph multiLayeredGraph) {
        State duality = getDuality(multiLayeredGraph);
        if (duality == null) return null;
        Map<String, InterLayerConnection> interLayerConnectionMap = multiLayeredGraph.getInterLayerConnectionMap();
        Object[] interLayerIds = interLayerConnectionMap.keySet().stream().filter(interLayerId -> {
            InterLayerConnection interLayerConnection = interLayerConnectionMap.get(interLayerId);
            if (!interLayerConnection.getTypeOfTopoExpression().equals("CONTAINS")) return false;
            return interLayerConnection.getInterConnects().stream().filter(stateId -> stateId.equals(duality.getId())).findFirst().orElse(null) != null;
        }).toArray();


        List<String> containedStateIds = new ArrayList<>();
        for (Object interLayerId : interLayerIds) {
            InterLayerConnection interLayerConnection = interLayerConnectionMap.get((String)interLayerId);
            interLayerConnection.getInterConnects().stream().filter(connectedId -> !connectedId.equals(duality.getId())).findFirst().ifPresent(containedStateIds::add);
        }

        return containedStateIds;
    }

    public boolean isAbleToMoveFloor() {
        List<String> list = new ArrayList<>(Arrays.asList("stair", "elevator", "escalator"));
        return list.contains(this.getName().toLowerCase());
    }
}
