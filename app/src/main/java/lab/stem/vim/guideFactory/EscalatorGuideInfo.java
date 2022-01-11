package lab.stem.vim.guideFactory;

public class EscalatorGuideInfo extends GuideInfo{
    GuideInfoFactory guideInfoFactory;

    public EscalatorGuideInfo(GuideInfoFactory guideInfoFactory) {
        this.guideInfoFactory = guideInfoFactory;
    }

    @Override
    public void assemble(FACTORY_ORDER_TYPE factoryOrder, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        this.vimState = guideInfoFactory.createVIMSTATE();
        this.message = guideInfoFactory.createMessage(factoryOrder, guideInfoPurchaseOrder);
    }
}
