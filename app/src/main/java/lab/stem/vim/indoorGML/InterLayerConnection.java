package lab.stem.vim.indoorGML;

import java.util.ArrayList;
import java.util.List;

public class InterLayerConnection {
    private String id;
    private String typeOfTopoExpression;
    private List<String> interConnects;
    private List<String> connectedLayers;

    public InterLayerConnection(String id) {
        this.id = id;
        this.interConnects = new ArrayList<>();
        this.connectedLayers = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeOfTopoExpression() {
        return typeOfTopoExpression;
    }

    public void setTypeOfTopoExpression(String typeOfTopoExpression) {
        this.typeOfTopoExpression = typeOfTopoExpression;
    }

    public List<String> getInterConnects() {
        return interConnects;
    }

    public void setInterConnects(List<String> interConnects) {
        this.interConnects = interConnects;
    }

    public List<String> getConnectedLayers() {
        return connectedLayers;
    }

    public void setConnectedLayers(List<String> connectedLayers) {
        this.connectedLayers = connectedLayers;
    }
}
