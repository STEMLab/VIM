package lab.stem.vim.guideFactory;

import java.util.Map;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.VIMSTATE;

public class NextTurningGuideInfoStore extends GuideInfoStore {
    @Override
    GuideInfo createGuideInfo(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        GuideInfo guideInfo = null;
        GuideInfoFactory guideInfoFactory = new NextTurningGuideInfoFactory();
        this.state = VIMSTATE.GO;
        switch (factory_order_type){
            case NEXT_TURNING:
                guideInfo = new NextTurningGuideInfo(guideInfoFactory);
                break;
            case NEXT_PLUS_NEXT_TURNING:
                guideInfo = new NextTurningPlusNextGuideInfo(guideInfoFactory);
                break;
            default:
                break;
        }

        return guideInfo;
    }

    @Override
    boolean isReadyToTakeAOrder(boolean force, long newOrderTime) {
        if (force) return true;
        boolean isReady = newOrderTime - this.orderedTime > 10000 ? true : false;
        if (isReady) this.orderedTime = newOrderTime;
        return isReady;
    }

    @Override
    FACTORY_ORDER_TYPE getFitFactoryOrderType(GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        FACTORY_ORDER_TYPE factoryOrderType;
        if (guideInfoPurchaseOrder.getNavigatingInformation().getCloseWithNextPoint() == null) {
            factoryOrderType = FACTORY_ORDER_TYPE.NEXT_TURNING;
        } else {
            factoryOrderType = FACTORY_ORDER_TYPE.NEXT_PLUS_NEXT_TURNING;
        }
        return factoryOrderType;
    }

    @Override
    boolean notifyOtherStore(long orderTime, NavInfo navInfo, Map<String, GuideInfoStore> guideInfoStoreMap) {
        guideInfoStoreMap.get("GoGuideInfoStore").setOrderedTime(orderTime);
        if (navInfo.isBrailleBlocks()) guideInfoStoreMap.get("TurningGuideInfoStore").setOrderedTime(orderedTime + 4000);
        else guideInfoStoreMap.get("GoGuideInfoStore").resetOrderedTime();
        return true;
    }
}
