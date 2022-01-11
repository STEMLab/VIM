package lab.stem.vim.guideFactory;

import lab.stem.vim.core.DestinationInfo;
import lab.stem.vim.core.User;
import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.Indicator;
import lab.stem.vim.core.VIMSTATE;

public class ArrivalGuideInfoFactory implements GuideInfoFactory{
    private Indicator indicator;

    @Override
    public VIMSTATE createVIMSTATE() {
        return VIMSTATE.ARRIVE;
    }

    @Override
    public String createMessage(FACTORY_ORDER_TYPE factoryOrder, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        NavInfo navInfo = guideInfoPurchaseOrder.getNavigatingInformation();
        this.indicator = navInfo.getIndicator();
        User user = guideInfoPurchaseOrder.getUser();
        DestinationInfo destinationInfo = user.getDestinationInfo();
        boolean isKorean = guideInfoPurchaseOrder.isKorean();
        String distanceForm = navInfo.getDistanceForm();

        String message;
        switch (factoryOrder) {
            case ARRIVAL:
                message = getArrivalMessage(isKorean, destinationInfo);
                break;
            case ARRIVAL_NEAR:
                message = getNearMessage(isKorean, distanceForm, user, destinationInfo);
                break;
            case ARRIVAL_MOVING_OBJECT:
                message = getOnSpatialCanMoveFloorAndGoMessage(isKorean, destinationInfo);
                break;
            default:
                message = null;
                break;
        }
        return message;
    }

    private String getOnSpatialCanMoveFloorAndGoMessage(boolean isKorean, DestinationInfo destinationInfo) {
        String destinationName = destinationInfo.getKoreanName();
        destinationName = destinationName.split("\\(")[0];
        String type = destinationInfo.getSpatialCanMoveFloor().getType();
        int destinationFloor = destinationInfo.getSpatialCanMoveFloor().getDestinationFloor();
        if (isKorean) {
            switch (type) {
                case "elevator":
                    type = "엘레베이터";
                    break;
                case "escalator":
                    type = "에스컬레이터";
                    break;
                case "stair":
                    type = "계단";
                    break;
                default:
                    type = "탈것";
                    break;
            }
        }

        return isKorean
                ? "Using the "+ type + " in front, go to the "+ getOrdinalNumber(destinationFloor) +" floor and go to the destination " + destinationName +"."
                : "전방의 " + type + "을 이용하여 " + destinationFloor +"층으로 이동하여 목적지 " + destinationName + "까지 이동하세요.";
    }

    private String getNearMessage(boolean isKorean, String distanceForm, User user, DestinationInfo destinationInfo) {
        double angle = indicator.getAngleToPoint(destinationInfo.getTargetPoint(), user.getPoint());
        double distance = user.getPoint().getDistance(destinationInfo.getTargetPoint());
        String distanceMessage = indicator.getLeftDistanceWord(distanceForm, distance);
        String wordDirection = indicator.getAngleToWordDirection(isKorean, false, angle);

        return  isKorean
                ? "목적지 " + destinationInfo.getKoreanName()+ "이(가) " + wordDirection + " " + distanceMessage + " 에 있습니다."
                : "The destination " + destinationInfo.getEnglishName() + " is located at " + wordDirection + " and " + distanceMessage + " away." ;
    }

    private String getArrivalMessage(boolean isKorean, DestinationInfo destinationInfo) {
        return isKorean ? "목적지 "+ destinationInfo.getKoreanName() +"에 도착했습니다." : "You've arrived at destination " + destinationInfo.getEnglishName() +".";
    }

    private String getOrdinalNumber(int number) {
        String ordinalNumber;
        switch (number){
            case 1:
                ordinalNumber = "1st";
                break;
            case 2:
                ordinalNumber = "2nd";
                break;
            case 3:
                ordinalNumber = "3st";
                break;
            default:
                ordinalNumber = number+"th";
                break;
        }
        return ordinalNumber;
    }
}
