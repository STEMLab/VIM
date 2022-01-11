package lab.stem.vim.indoorGML;

import java.util.ArrayList;
import java.util.List;

public class State extends GmlInfo implements Comparable {
    private String id;
    private String duality;
    private List<String> connects;
    private Point point;

    public State(String id) {
        this.id = id;
        this.connects = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDuality() {
        return duality;
    }

    public void setDuality(String duality) {
        this.duality = duality;
    }

    public List<String> getConnects() {
        return connects;
    }

    public void setConnects(List<String> connects) {
        this.connects = connects;
    }

    public Point getPoint() {
        return point == null ? new Point() : point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    //extend method//
    public boolean isDotBlock() {
        return this.getDescription().contains("dot");
    }
    //extend method//

    @Override
    public String toString() {
        return "State{" +
                "id='" + id + '\'' +
                ", duality='" + duality + '\'' +
                ", connects=" + connects +
                ", point=" + point +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public int compareTo(State s, Point p){
        if (this.getPoint().getZ() != p.getZ()) return (int)(this.getPoint().getDistance(p) + p.getZ() + 1);
        if (this.getPoint().getDistance(p) == s.getPoint().getDistance(p)) return 0;
        else if (this.getPoint().getDistance(p) < s.getPoint().getDistance(p)) return -1;
        return 1;
    }
}
