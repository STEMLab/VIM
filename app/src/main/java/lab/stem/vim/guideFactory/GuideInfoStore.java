package lab.stem.vim.guideFactory;

import java.util.Map;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.VIMSTATE;

public abstract class GuideInfoStore {
    protected long orderedTime;
    protected VIMSTATE state;

    public GuideInfoStore() {
        this.orderedTime = 0;
        this.state = VIMSTATE.STOP;
    }

    public GuideInfo orderGuideInfo(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder){
        GuideInfo guideInfo;
        guideInfo = createGuideInfo(factory_order_type, guideInfoPurchaseOrder);

        guideInfo.assemble(factory_order_type, guideInfoPurchaseOrder);

        return !guideInfo.isNothing() ? guideInfo : null;
    }

    abstract GuideInfo createGuideInfo(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder);

    abstract boolean isReadyToTakeAOrder(boolean force, long newOrderTime);

    abstract FACTORY_ORDER_TYPE getFitFactoryOrderType(GuideInfoPurchaseOrder guideInfoPurchaseOrder);

    abstract boolean notifyOtherStore(long orderTime, NavInfo navInfo, Map<String, GuideInfoStore> guideInfoStoreMap);

    public void resetOrderedTime(){
        this.orderedTime = 0;
    }

    public void setOrderedTime(long time) { this.orderedTime = time; }

    public long getOrderedTime() {
        return orderedTime;
    }

    public VIMSTATE getState() {
        return state;
    }

    public void setState(VIMSTATE state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
