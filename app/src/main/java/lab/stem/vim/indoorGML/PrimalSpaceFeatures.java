package lab.stem.vim.indoorGML;

import java.util.HashMap;
import java.util.Map;

public class PrimalSpaceFeatures {
    private String id;
    private final Map<String, CellSpace> cellSpaceMap;

    public PrimalSpaceFeatures(String id) {
        this.id = id;
        cellSpaceMap = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, CellSpace> getCellSpaceMap() {
        return cellSpaceMap;
    }

    public void addCellSpace(String id, CellSpace cellSpace) {
        cellSpaceMap.put(id, cellSpace);
    }

    // extend functions
    public CellSpace getCellSpace(String id) {
        return getCellSpaceMap().get(id);
    }
}
