package lab.stem.vim.guideFactory;

import java.util.Map;

import lab.stem.vim.core.DestinationInfo;
import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.VIMSTATE;

public class ArrivalGuideInfoStore extends GuideInfoStore {

    @Override
    GuideInfo createGuideInfo(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        GuideInfo guideInfo = null;
        GuideInfoFactory guideInfoFactory = new ArrivalGuideInfoFactory();
        this.state = VIMSTATE.ARRIVE;

        switch (factory_order_type){
            case ARRIVAL_MOVING_OBJECT:
                guideInfo = new ArrivalSpatialCanMoveFloorGuideInfo(guideInfoFactory);
                break;
            case ARRIVAL_NEAR:
                guideInfo = new ArrivalNearGuideInfo(guideInfoFactory);
                break;
            case ARRIVAL:
                guideInfo = new ArrivalGuideInfo(guideInfoFactory);
                break;
            default:
                break;
        }

        return guideInfo;
    }

    @Override
    boolean isReadyToTakeAOrder(boolean force, long newOrderTime) {
        if (force) return true;
        boolean isReady = newOrderTime - this.orderedTime > 4000;
        if (isReady) this.orderedTime = newOrderTime;
        return isReady;
    }

    @Override
    FACTORY_ORDER_TYPE getFitFactoryOrderType(GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        DestinationInfo destinationInfo = guideInfoPurchaseOrder.getUser().getDestinationInfo();
        if (destinationInfo.hasSpatialCanMoveFloor()){
            return FACTORY_ORDER_TYPE.ARRIVAL_MOVING_OBJECT;
        } else if (destinationInfo.hasTarget()){
            return FACTORY_ORDER_TYPE.ARRIVAL_NEAR;
        } else {
            return FACTORY_ORDER_TYPE.ARRIVAL;
        }
    }

    @Override
    boolean notifyOtherStore(long orderTime, NavInfo navInfo, Map<String, GuideInfoStore> guideInfoStoreMap) {
        return false;
    }
}
