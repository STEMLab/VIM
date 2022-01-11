package lab.stem.vim.guideFactory;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.VIMSTATE;

public class SpatialCanMoveFloorGuideInfoFactory implements GuideInfoFactory{
    @Override
    public VIMSTATE createVIMSTATE() {
        return VIMSTATE.FLOOR_MOVING;
    }

    @Override
    public String createMessage(FACTORY_ORDER_TYPE factoryOrder, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        NavInfo navInfo = guideInfoPurchaseOrder.getNavigatingInformation();
        boolean isKorean = guideInfoPurchaseOrder.isKorean();
        int targetFloor = navInfo.getTargetFloor();
        int currentFloor = (int) guideInfoPurchaseOrder.getUser().getPoint().getZ();
        String message;
        switch (factoryOrder) {
            case ELEVATOR:
                message = getElevatorMessage(isKorean, targetFloor);
                break;
            case ESCALATOR:
                message = getEscalatorMessage(isKorean, currentFloor, targetFloor);
                break;
            case STAIR:
                message = getStairMessage(isKorean, currentFloor, targetFloor);
                break;
            default:
                message = null;
                break;
        }
        return message;
    }

    public String getStairMessage(boolean isKorean, int currentFloor, int targetFloor){
        String message;
        int floorNeedMove = targetFloor - currentFloor;
        if (floorNeedMove > 0) {
            message = isKorean ? "전방의 계단을 이용하여 " + floorNeedMove + "번 계단을 올라가세요." : "Go up stair " + floorNeedMove +(floorNeedMove > 1 ? "times.": "time.");
        } else {
            message = isKorean ? "전방의 계단을 이용하여 " + Math.abs(floorNeedMove) + "번 계단을 내려가세요." : "Go down stair " + Math.abs(floorNeedMove) +(floorNeedMove > -1 ? "times.": "time.");
        }
        return message;
    }

    public String getEscalatorMessage(boolean isKorean, int currentFloor, int targetFloor){
        String message;
        int floorNeedMove = targetFloor - currentFloor;
        if (floorNeedMove > 0) {
            message = isKorean ? "전방의 에스컬레이터를 이용하여 " + floorNeedMove + "번 올라가세요." : "Go up escalator " + floorNeedMove +(floorNeedMove > 1 ? "times.": "time.");
        } else {
            message = isKorean ? "전방의 에스컬레이터를 이용하여 " + Math.abs(floorNeedMove) + "번 내려가세요." : "Go down escalator " + Math.abs(floorNeedMove) +(floorNeedMove > 1 ? "times.": "time.");
        }
        return message;
    }

    public String getElevatorMessage(boolean isKorean, int targetFloor){
        return isKorean ? "전방의 엘레베이터를 이용하여" + targetFloor + "층으로 이동하세요." : "Move to " + targetFloor + "floor using elevator.";
    }
}
