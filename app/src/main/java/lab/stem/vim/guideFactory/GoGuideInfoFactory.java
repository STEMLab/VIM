package lab.stem.vim.guideFactory;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.Indicator;
import lab.stem.vim.core.VIMSTATE;

public class GoGuideInfoFactory implements GuideInfoFactory{
    private Indicator indicator;

    @Override
    public VIMSTATE createVIMSTATE() {
        return VIMSTATE.GO;
    }

    @Override
    public String createMessage(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        NavInfo navInfo = guideInfoPurchaseOrder.getNavigatingInformation();
        this.indicator = navInfo.getIndicator();
        boolean isKorean = guideInfoPurchaseOrder.isKorean();
        boolean isBrailleBlocks = navInfo.isBrailleBlocks();
        double leftDistance = navInfo.getLeftDistance();
        String distanceForm = navInfo.getDistanceForm();
        String message;
        switch (factory_order_type) {
            case GO:
                message = getGoMessage(isKorean, isBrailleBlocks, distanceForm, leftDistance);
                break;
            default:
                message = null;
                break;
        }
        return message;
    }

//    public String getGoMessage(boolean isKorean, boolean isBrailleBlocks, int distance){
//        String message = null;
//        if (distance < 4) {
//            message = isKorean ? "조금 앞으로 가세요." : "keep going forward.";
//        } else {
//            message = isKorean
//                    ? (isBrailleBlocks
//                    ? "선형 블록을 따라 " + String.format("%d", distance) + "미터 이동하세요."
//                    : String.format("%d", distance) + " 미터 앞으로 가세요.")
//                    : "Go " + String.format("%d", distance) + " meter forward";
//        }
//        return  message;
//    }

    public String getGoMessage(boolean isKorean, boolean isBrailleBlocks, String distanceForm, double leftDistance){
        boolean isAlmostReach = indicator.isAlmostReach(distanceForm, leftDistance);
        String distanceMessage = indicator.getLeftDistanceWord(distanceForm, leftDistance);
        String message;

        if (isAlmostReach) {
            message = isKorean ? "조금 앞으로 가세요." :  "keep going forward a little.";
        } else {
            message = isKorean
                    ? (isBrailleBlocks
                        ? "선형 블록을 따라 " + distanceMessage + " 이동하세요."
                        : distanceMessage + " 앞으로 이동하세요.")
                    : (isBrailleBlocks
                        ? "Go " + distanceMessage + " along the linear blocks."
                        : "Go " + distanceMessage + " forward.");
        }
        return  message;
    }
}
