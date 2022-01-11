package lab.stem.vim.guideFactory;

import java.util.Map;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.VIMSTATE;

public class ExplainGuideInfoStore extends GuideInfoStore {
    @Override
    GuideInfo createGuideInfo(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        GuideInfo guideInfo = null;
        GuideInfoFactory guideInfoFactory = new ExplainGuideInfoFactory();
        this.state = VIMSTATE.EXPLAIN;
        switch (factory_order_type){
            case ROUTE_EXPLAIN:
                guideInfo = new ExplainGuideInfo(guideInfoFactory);
                break;
            default:
                break;
        }

        return guideInfo;
    }

    @Override
    boolean isReadyToTakeAOrder(boolean force, long newOrderTime) {
        if (force) return true;
        boolean isReady = newOrderTime - this.orderedTime > 5000;
        if (isReady) this.orderedTime = newOrderTime;
        return isReady;
    }

    @Override
    FACTORY_ORDER_TYPE getFitFactoryOrderType(GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        return FACTORY_ORDER_TYPE.ROUTE_EXPLAIN;
    }

    @Override
    boolean notifyOtherStore(long orderTime, NavInfo navInfo, Map<String, GuideInfoStore> guideInfoStoreMap) {
        guideInfoStoreMap.forEach((key, guideInfoStore) -> {
//            guideInfoStore.resetOrderedTime();
            guideInfoStore.setOrderedTime(orderedTime - 3000);
        });
        return true;
    }
}
