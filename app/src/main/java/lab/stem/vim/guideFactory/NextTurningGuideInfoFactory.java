package lab.stem.vim.guideFactory;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.Indicator;
import lab.stem.vim.core.VIMSTATE;

public class NextTurningGuideInfoFactory implements GuideInfoFactory{
    private Indicator indicator;

    @Override
    public VIMSTATE createVIMSTATE() {
        return VIMSTATE.GO;
    }

    @Override
    public String createMessage(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        NavInfo navInfo = guideInfoPurchaseOrder.getNavigatingInformation();
        boolean isKorean = guideInfoPurchaseOrder.isKorean();
        boolean isBrailleBlocks = navInfo.isBrailleBlocks();
        double angle = navInfo.getAngle();
        double nextAngle  = 0.0;
        this.indicator = navInfo.getIndicator();
//        if (navigatingInformation.getCloseWithNextPoint() != null) {
//            Log.d("TAG", "createMessage: hello" );
//            nextAngle = navigatingInformation.getNextAngle();
//        }
        String message;
        switch (factory_order_type) {
            case NEXT_TURNING:
                message = getNextTurningMessage(isKorean, isBrailleBlocks, angle);
                break;
            case NEXT_PLUS_NEXT_TURNING:
                nextAngle = navInfo.getNextAngle();
                message = getNextPlusNextTurningMessage(isKorean, isBrailleBlocks, angle, nextAngle);
                break;
            default:
                message = null;
                break;
        }
        return message;
    }

    public String getNextTurningMessage(boolean isKorean, boolean isBrailleBlocks, double angle) {
        return isKorean
                ?  (isBrailleBlocks
                        ? "잠시후 점형블록에서 " + indicator.getAngleToWordDirection(true, true, angle) + " 이동하세요."
                        : "잠시후 " +  indicator.getAngleToWordDirection(true, true, angle) + " 이동하세요.")
                : indicator.getAngleToWordDirection(false, true, angle) + " a little while.";
    }

    public String getNextPlusNextTurningMessage(boolean isKorean, boolean isBrailleBlocks, double angle, double nextAngle){
        return isKorean
                ? (isBrailleBlocks
                    ? ("잠시후 점형블록에서 "
                        + indicator.getAngleToWordDirection(true, true, angle)
                        + " 점형 블록까지 이동한 후 " + indicator.getAngleToWordDirection(true, true, nextAngle)
                        +" 이동하세요.")
                    : "잠시후 " + indicator.getAngleToWordDirection(true, true, angle) + " 이동 한 후 " + indicator.getAngleToWordDirection(true, true, nextAngle) +" 이동하세요.")
                : (isBrailleBlocks
                    ? "After a while," + indicator.getAngleToWordDirection(false, true, angle) + " at dot blocks and " + indicator.getAngleToWordDirection(false, true, nextAngle) + " at next dot blocks and along the linear blocks."
                    : "After a while," + indicator.getAngleToWordDirection(false, true, angle) + " and " + indicator.getAngleToWordDirection(false, true, nextAngle) + " a little while and go ahead.");
    }
}
