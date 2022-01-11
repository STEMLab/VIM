package lab.stem.vim.core;

public class SpatialCanMoveFloor {
    private String type;
    private int destinationFloor;

    public SpatialCanMoveFloor(String type, int destinationFloor) {
        this.type = type;
        this.destinationFloor = destinationFloor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }
}
