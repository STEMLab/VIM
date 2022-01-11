package lab.stem.vim.indoorGML;

import java.util.List;

public class Polygon {
    private List<Point> pointList;

    public Polygon(List<Point> pointList) {
        this.pointList = pointList;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }
}
