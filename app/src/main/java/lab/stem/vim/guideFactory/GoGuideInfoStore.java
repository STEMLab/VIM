package lab.stem.vim.guideFactory;

import java.util.Map;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.VIMSTATE;

public class GoGuideInfoStore extends GuideInfoStore {
    @Override
    GuideInfo createGuideInfo(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        GuideInfo guideInfo = null;
        GuideInfoFactory guideInfoFactory = new GoGuideInfoFactory();
        this.state = VIMSTATE.GO;
        switch (factory_order_type){
            case GO:
                guideInfo = new GoGuideInfo(guideInfoFactory);
                break;
            default:
                break;
        }

        return guideInfo;
    }

    @Override
    boolean isReadyToTakeAOrder(boolean force, long newOrderTime) {
        if (force) return true;
        boolean isReady = newOrderTime - this.orderedTime > 10000;
        if (isReady) this.orderedTime = newOrderTime;
        return isReady;
    }

    @Override
    FACTORY_ORDER_TYPE getFitFactoryOrderType(GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        return FACTORY_ORDER_TYPE.GO;
    }

    @Override
    boolean notifyOtherStore(long orderTime, NavInfo navInfo, Map<String, GuideInfoStore> guideInfoStoreMap) {
        guideInfoStoreMap.get("NextTurningGuideInfoStore").setOrderedTime(orderTime - 7000);
        guideInfoStoreMap.get("TurningGuideInfoStore").setOrderedTime(orderTime - 5000);
        return true;
    }

}
