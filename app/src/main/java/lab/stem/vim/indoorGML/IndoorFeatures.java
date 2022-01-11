package lab.stem.vim.indoorGML;

public class IndoorFeatures {
    private PrimalSpaceFeatures primalSpaceFeatures;
    private MultiLayeredGraph multiLayeredGraph;

    public PrimalSpaceFeatures getPrimalSpaceFeatures() {
        return primalSpaceFeatures;
    }

    public void setPrimalSpaceFeatures(PrimalSpaceFeatures primalSpaceFeatures) {
        this.primalSpaceFeatures = primalSpaceFeatures;
    }

    public MultiLayeredGraph getMultiLayeredGraph() {
        return multiLayeredGraph;
    }

    public void setMultiLayeredGraph(MultiLayeredGraph multiLayeredGraph) {
        this.multiLayeredGraph = multiLayeredGraph;
    }

    public CellSpace getContainedCellSpace(String motherLayer, String childrenLayer, String stateId) {
        String interConnectedWithState = this.getMultiLayeredGraph().getInterConnectedWithState("contains",motherLayer,childrenLayer,stateId);
        if(interConnectedWithState == null) return null;
        return this.getPrimalSpaceFeatures().getCellSpace(this.getMultiLayeredGraph().getState(interConnectedWithState).getDuality());
    }
}
