package lab.stem.vim.core;

import org.json.JSONException;
import org.json.JSONObject;

import lab.stem.vim.indoorGML.Point;

public class DestinationInfo {
    private final String id;
    private String en_name;
    private String ko_name;
    private SpatialCanMoveFloor spatialCanMoveFloor;
    private Point targetPoint;
    private String targetLayer;
    private String targetId;

    public DestinationInfo(String id, JSONObject jsonObject) {
        this.id = id;
        try{
            if (jsonObject.has("name")){
                JSONObject name = (JSONObject) jsonObject.get("name");
                if (name.has("en")){
                    this.en_name = (String) name.get("en");
                }
                if (name.has("ko")){
                    this.ko_name = (String) name.get("ko");
                }
            }
            if (jsonObject.has("spatialCanMoveFloor")){
                JSONObject spatialCanMoveFloor = (JSONObject) jsonObject.get("spatialCanMoveFloor");
                this.spatialCanMoveFloor = new SpatialCanMoveFloor((String)spatialCanMoveFloor.get("type"),(int)spatialCanMoveFloor.get("destinationFloor"));
            }

            if (jsonObject.has("target")) {
                JSONObject target =  (JSONObject) jsonObject.get("target");
                this.targetLayer = target.getString("layer");
                this.targetId = target.getString("id");
            }

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getName(String language){
        if (language.equals("ko")){
            return this.ko_name;
        } else {
            return this.en_name;
        }
    }

    public String getEnglishName() {
        return en_name;
    }

    public String getKoreanName() {
        return ko_name;
    }

    public boolean hasSpatialCanMoveFloor() {
        return this.spatialCanMoveFloor != null;
    }

    public boolean hasTarget() {return this.targetLayer != null && this.targetId != null;}

    public SpatialCanMoveFloor getSpatialCanMoveFloor() {
        return spatialCanMoveFloor;
    }

    public Point getTargetPoint() {
        return hasTarget() ? targetPoint : null;
    }

    public void setTargetPoint(Point targetPoint) {
        this.targetPoint = targetPoint;
    }

    public String getTargetLayer() {
        return targetLayer;
    }

    public String getTargetId() {
        return targetId;
    }

    @Override
    public String toString() {
        return "DestinationInfo{" +
                "id='" + id + '\'' +
                ", en_name='" + en_name + '\'' +
                ", ko_name='" + ko_name + '\'' +
                ", spatialCanMoveFloor=" + spatialCanMoveFloor +
                ", targetPoint=" + targetPoint +
                ", targetLayer='" + targetLayer + '\'' +
                ", targetId='" + targetId + '\'' +
                '}';
    }
}
