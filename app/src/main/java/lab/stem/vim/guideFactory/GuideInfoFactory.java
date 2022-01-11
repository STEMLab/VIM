package lab.stem.vim.guideFactory;

import lab.stem.vim.core.VIMSTATE;

public interface GuideInfoFactory {
    VIMSTATE createVIMSTATE();
    String createMessage(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder);
}
