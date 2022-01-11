package lab.stem.vim.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

class Vertex implements Comparable<Vertex> {

    private String id;
    private Double distance;
    private String type;

    public Vertex(String id, double distance) {
        super();
        this.id = id;
        this.distance = distance;
    }

    public Vertex(String id, double distance, String type) {
        super();
        this.id = id;
        this.distance = distance;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public Double getDistance() {
        return distance;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((distance == null) ? 0 : distance.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex other = (Vertex) obj;
        if (distance == null) {
            if (other.distance != null)
                return false;
        } else if (!distance.equals(other.distance))
            return false;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
    }

    @Override
    public String toString() {
        return "Vertex [id=" + id + ", distance=" + distance + "]";
    }

    @Override
    public int compareTo(Vertex o) {
        if (this.distance < o.distance)
            return -1;
        else if (this.distance > o.distance)
            return 1;
        else
            return this.getId().compareTo(o.getId());
    }

}

public class Graph {

    private final Map<String, List<Vertex>> vertices;

    public Graph() {
        this.vertices = new HashMap<>();
    }

    public void addVertex(String character, List<Vertex> vertex) {
        this.vertices.put(character, vertex);
    }

    public List<String> getShortestPath(String start, String finish, String floorType) {
        final Map<String, Double> distances = new HashMap<>();
        final Map<String, Vertex> previous = new HashMap<>();
        PriorityQueue<Vertex> nodes = new PriorityQueue<>();

        for(String vertex : vertices.keySet()) {
            if (vertex.equals(start)) {
                distances.put(vertex, (double) 0);
                nodes.add(new Vertex(vertex, 0));
            } else {
                distances.put(vertex, Double.MAX_VALUE);
                nodes.add(new Vertex(vertex, Double.MAX_VALUE));
            }
            previous.put(vertex, null);
        }

        while (!nodes.isEmpty()) {
            Vertex smallest = nodes.poll();
            if (smallest != null && smallest.getId().equals(finish)) {
                final List<String> path = new ArrayList<>();
                while (previous.get(smallest != null ? smallest.getId() : null) != null) {
                    if (smallest != null) {
                        path.add(smallest.getId());
                        smallest = previous.get(smallest.getId());
                    }
                }
                return path;
            }

            if (distances.get(smallest.getId()) == Double.MAX_VALUE) {
                break;
            }

            for (Vertex neighbor : vertices.get(smallest.getId())) {
                Double alt = distances.get(smallest.getId()) + neighbor.getDistance();
                if (alt < distances.get(neighbor.getId())) {
                    distances.put(neighbor.getId(), alt);
                    previous.put(neighbor.getId(), smallest);

                    for (Vertex n : nodes) {
                        if (n.getId().equals(neighbor.getId())) {
                            nodes.remove(n);
                            n.setDistance(alt);
                            nodes.add(n);
                            break;
                        }
                    }
                }
            }
        }

        return new ArrayList<>(distances.keySet());
    }

}
