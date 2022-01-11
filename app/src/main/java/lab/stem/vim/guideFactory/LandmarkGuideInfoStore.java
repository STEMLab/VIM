package lab.stem.vim.guideFactory;

import java.util.Map;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.VIMSTATE;

public class LandmarkGuideInfoStore extends GuideInfoStore {

    @Override
    GuideInfo createGuideInfo(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        GuideInfo guideInfo = null;
        GuideInfoFactory guideInfoFactory = new LandmarkGuideInfoFactory();
        this.state = VIMSTATE.LANDMARK;

        switch (factory_order_type){
            case LANDMARK:
                guideInfo = new LandmarkGuideInfo(guideInfoFactory);
                break;
            default:
                break;
        }

        return guideInfo;
    }

    @Override
    boolean isReadyToTakeAOrder(boolean force, long newOrderTime) {
        if (force) return true;
        boolean isReady = newOrderTime - this.orderedTime > 2000;
        if (isReady) this.orderedTime = newOrderTime;
        return isReady;
    }

    @Override
    FACTORY_ORDER_TYPE getFitFactoryOrderType(GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        return FACTORY_ORDER_TYPE.LANDMARK;
    }

    @Override
    boolean notifyOtherStore(long orderTime, NavInfo navInfo, Map<String, GuideInfoStore> guideInfoStoreMap) {
        return false;
    }
}
