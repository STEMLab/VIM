package lab.stem.vim.guideFactory;

import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.User;
import lab.stem.vim.core.Indicator;
import lab.stem.vim.core.VIMSTATE;

public class ExplainGuideInfoFactory implements GuideInfoFactory{
    private Indicator indicator;

    @Override
    public VIMSTATE createVIMSTATE() {
        return VIMSTATE.EXPLAIN;
    }

    @Override
    public String createMessage(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        NavInfo navInfo = guideInfoPurchaseOrder.getNavigatingInformation();
        this.indicator = navInfo.getIndicator();
        User user = guideInfoPurchaseOrder.getUser();
        user.setWantExplain(false);
        boolean isKorean = guideInfoPurchaseOrder.isKorean();
        String distanceForm = navInfo.getDistanceForm();
        double leftDistance = user.getTotalDistance();
        String destination = isKorean ? user.getDestinationInfo().getKoreanName() : user.getDestinationInfo().getEnglishName();

        String message;
        switch (factory_order_type) {
            case ROUTE_EXPLAIN:
                message = getRouteExplainMessage(isKorean, distanceForm, destination, leftDistance);
                break;
            default:
                message = null;
                break;
        }
        return message;
    }

    public String getRouteExplainMessage(boolean isKorean, String distanceForm, String destination, double leftDistance) {
        String distanceMessage = indicator.getLeftDistanceWord(distanceForm, leftDistance);

        return isKorean
                ? destination + " 까지 안내를 시작합니다. 총 거리 " + distanceMessage + " 입니다."
                : "Start the route guidance to the " + destination + ". total distance is " + distanceMessage + " what you need to go.";
    }
}
