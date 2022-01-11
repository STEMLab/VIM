package lab.stem.vim.guideFactory;

import java.util.Map;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.VIMSTATE;

public class SpatialCanMoveFloorGuideInfoStore extends GuideInfoStore {

    @Override
    GuideInfo createGuideInfo(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        GuideInfo guideInfo = null;
        GuideInfoFactory guideInfoFactory = new SpatialCanMoveFloorGuideInfoFactory();
        this.state = VIMSTATE.FLOOR_MOVING;

        switch (factory_order_type){
            case ELEVATOR:
                guideInfo = new ElevatorGuideInfo(guideInfoFactory);
                break;
            case ESCALATOR:
                guideInfo = new EscalatorGuideInfo(guideInfoFactory);
                break;
            case STAIR:
                guideInfo = new StairGuideInfo(guideInfoFactory);
                break;
            default:
                break;
        }

        return guideInfo;
    }

    @Override
    boolean isReadyToTakeAOrder(boolean force, long newOrderTime) {
        if (force) return true;
        boolean isReady = newOrderTime - this.orderedTime > 13000;
        if (isReady) this.orderedTime = newOrderTime;
        return isReady;
    }

    @Override
    FACTORY_ORDER_TYPE getFitFactoryOrderType(GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        String floorMovingType = guideInfoPurchaseOrder.getNavigatingInformation().getFloorMovingType();
        if (floorMovingType.toUpperCase().contains("STAIR")) return FACTORY_ORDER_TYPE.STAIR;
        else if (floorMovingType.toUpperCase().contains("ELEVATOR")) return FACTORY_ORDER_TYPE.ELEVATOR;
        else if (floorMovingType.toUpperCase().contains("ESCALATOR")) return FACTORY_ORDER_TYPE.ESCALATOR;
        else return FACTORY_ORDER_TYPE.STAIR;
    }

    @Override
    boolean notifyOtherStore(long orderTime, NavInfo navInfo, Map<String, GuideInfoStore> guideInfoStoreMap) {
        return false;
    }
}
