package lab.stem.vim.guideFactory;

import java.util.ArrayList;
import java.util.List;

import lab.stem.vim.core.User;
import lab.stem.vim.core.DetectedPoiInfo;
import lab.stem.vim.core.NavInfo;
import lab.stem.vim.core.VIMSTATE;

public class LandmarkGuideInfoFactory extends PoiGuideInfoFactory{
    @Override
    protected String getPOIGuideMessage(boolean isKorean, String distanceForm, DetectedPoiInfo detectedPoiInfo) {
        String wordDirection = indicator.getAngleToWordDirection(isKorean, false, detectedPoiInfo.getAngle());
        String poiName = detectedPoiInfo.getName();
        double distance = detectedPoiInfo.getDistance();
        String distanceMessage = indicator.getLeftDistanceWord(distanceForm, distance);

        if (wordDirection == null) return isKorean
                ? "근처 " + distanceMessage  + " 내에 " + poiName + " 가 있습니다."
                : "There is " + poiName + " within " + distanceMessage  + " nearby.";
        return isKorean
                ? wordDirection+ " " + distanceMessage + " 내에 " + poiName + " 가 있습니다."
                : "There is " + poiName+ " " + distanceMessage + " " + wordDirection;
    }

    @Override
    protected String getPOIGuideMessages(boolean isKorean, String distanceForm, boolean ignoreDetected, User user, List<DetectedPoiInfo> detectedPoiInfoList) {
        List<String> messages = new ArrayList<>();
        detectedPoiInfoList.forEach(detectedPoiInformation -> {
            if (ignoreDetected) {
                messages.add(getPOIGuideMessage(isKorean, distanceForm, detectedPoiInformation));
            } else if (!user.getDetectedPOI().contains(detectedPoiInformation.getId())) {
                user.getDetectedPOI().add(detectedPoiInformation.getId());
                messages.add(getPOIGuideMessage(isKorean, distanceForm, detectedPoiInformation));
            }
        });
        return messages.toString().replace("[", "").replace("]", "");
    }

    @Override
    public VIMSTATE createVIMSTATE() {
        return VIMSTATE.LANDMARK;
    }

    @Override
    public String createMessage(FACTORY_ORDER_TYPE factory_order_type, GuideInfoPurchaseOrder guideInfoPurchaseOrder) {
        NavInfo navInfo = guideInfoPurchaseOrder.getNavigatingInformation();
        this.indicator = navInfo.getIndicator();
        boolean isKorean = guideInfoPurchaseOrder.isKorean();
        boolean ignoreDetected = navInfo.isIgnoreDetected();
        List<DetectedPoiInfo> detectedPoiInfoList = navInfo.getNearByPoiList();
        User user = guideInfoPurchaseOrder.getUser();
        String distanceForm = navInfo.getDistanceForm();

        String message;
        switch (factory_order_type) {
            case LANDMARK:
                message = getPOIGuideMessages(isKorean, distanceForm, ignoreDetected, user, detectedPoiInfoList);
                break;
            default:
                message = null;
                break;
        }
        return message;
    }
}
