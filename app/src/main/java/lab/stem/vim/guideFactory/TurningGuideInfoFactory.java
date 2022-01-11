package lab.stem.vim.guideFactory;

import android.util.Log;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.Indicator;
import lab.stem.vim.core.VIMSTATE;

public class TurningGuideInfoFactory implements GuideInfoFactory{
    private Indicator indicator;

    @Override
    public VIMSTATE createVIMSTATE() {
        return VIMSTATE.TURN;
    }

    @Override
    public String createMessage(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        NavInfo navInfo = guideInfoPurchaseOrder.getNavigatingInformation();
        boolean isKorean = guideInfoPurchaseOrder.isKorean();
        boolean isBrailleBlocks = navInfo.isBrailleBlocks();
        this.indicator = navInfo.getIndicator();

        String message;
        switch (factory_order_type) {
            case TURNING:
                message = getTurningMessage(isKorean, isBrailleBlocks, navInfo.getAngleDirection());
                break;
            default:
                message = null;
                break;
        }
        return message;
    }


    public String getTurningMessage(boolean isKorean, boolean isBrailleBlocks, double angle) {
        return isKorean
                ? (!isBrailleBlocks
                ? indicator.getAngleToWordDirection(true, true, angle) + " 직진하세요."
                : indicator.getAngleToWordDirection(true, true, angle) + " 선형블록을 따라가세요.")
                : indicator.getAngleToWordDirection(false, true, angle)+ " and go ahead.";
    }
}
