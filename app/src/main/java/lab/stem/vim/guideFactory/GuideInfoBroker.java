package lab.stem.vim.guideFactory;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lab.stem.vim.core.User;
import lab.stem.vim.core.DetectedPoiInfo;
import lab.stem.vim.core.NavInfo;
import lab.stem.vim.observer.GuideInfoObserver;
import lab.stem.vim.observer.GuideInfoSubject;

public class GuideInfoBroker implements GuideInfoSubject {
    private final List<GuideInfoObserver> guideInfoObservers;
    Map<String, GuideInfoStore> guideInfoStoreMap;

    public GuideInfoBroker() {
        guideInfoObservers = new ArrayList<>();
        guideInfoStoreMap = new HashMap<>();
        guideInfoStoreMap.put("ExplainGuideInfoStore", new ExplainGuideInfoStore());
        guideInfoStoreMap.put("ArrivalGuideInfoStore", new ArrivalGuideInfoStore());
        guideInfoStoreMap.put("SpatialCanMoveFloorGuideInfoStore", new SpatialCanMoveFloorGuideInfoStore());
        guideInfoStoreMap.put("TurningGuideInfoStore", new TurningGuideInfoStore());
        guideInfoStoreMap.put("NextTurningGuideInfoStore", new NextTurningGuideInfoStore());
        guideInfoStoreMap.put("GoGuideInfoStore", new GoGuideInfoStore());
        guideInfoStoreMap.put("LandmarkGuideInfoStore", new LandmarkGuideInfoStore());
        guideInfoStoreMap.put("SafetyGuideInfoStore", new SafetyGuideInfoStore());
    }

    public List<GuideInfo> takeAOrder(boolean force, long orderTime, User user, NavInfo navInfo, boolean isKorean) {
        List<GuideInfoStore> guideInfoStores = connectPoiGuideStores(force, user, navInfo, orderTime);
        List<GuideInfo> guideInfoList = new ArrayList<>();

        guideInfoStores.add(connectGuideStore(force, user, navInfo, orderTime));

        GuideInfoPurchaseOrder guideInfoPurchaseOrder = new GuideInfoPurchaseOrder(user, navInfo, isKorean, orderTime);
        guideInfoStores.stream().filter(Objects::nonNull).forEach(guideInfoStore -> {
            FACTORY_ORDER_TYPE factory_order_type = guideInfoStore.getFitFactoryOrderType(guideInfoPurchaseOrder);
            GuideInfo guideInfo = guideInfoStore.orderGuideInfo(factory_order_type, guideInfoPurchaseOrder);
            if (guideInfo != null) guideInfoList.add(guideInfo);
        });

        return guideInfoList;
    }

    public List<GuideInfo> takeAOrderOnlyPoi(boolean force, long orderTime, boolean isKorean, User user, NavInfo navInfo) {
        List<GuideInfo> guideInfoList = new ArrayList<>();
        List<GuideInfoStore> guideInfoStores = connectPoiGuideStores(force, user, navInfo, orderTime);
        GuideInfoPurchaseOrder guideInfoPurchaseOrder = new GuideInfoPurchaseOrder(user, navInfo, isKorean, orderTime);
        guideInfoStores.stream().filter(Objects::nonNull).forEach(guideInfoStore -> {
            FACTORY_ORDER_TYPE factory_order_type = guideInfoStore.getFitFactoryOrderType(guideInfoPurchaseOrder);
            GuideInfo guideInfo = guideInfoStore.orderGuideInfo(factory_order_type, guideInfoPurchaseOrder);
            if (guideInfo != null) guideInfoList.add(guideInfo);
        });
        return guideInfoList;
    }

    private GuideInfoStore connectGuideStore(boolean force, User user, NavInfo navInfo, long orderTime) {
        GuideInfoStore guideInfoStore;
        if (user.isWantExplain()){
            guideInfoStore = guideInfoStoreMap.get("ExplainGuideInfoStore");
        } else if (navInfo.isNeedFloorMoving()){
            guideInfoStore = guideInfoStoreMap.get("SpatialCanMoveFloorGuideInfoStore");
        } else if (navInfo == null || navInfo.isArrived()) {
            guideInfoStore = guideInfoStoreMap.get("ArrivalGuideInfoStore");
        } else if (navInfo.isNeedTurn()) {
            guideInfoStore = guideInfoStoreMap.get("TurningGuideInfoStore");
        } else if (navInfo.needNextTurningGuide()) {
            guideInfoStore = guideInfoStoreMap.get("NextTurningGuideInfoStore");
        } else {
            guideInfoStore = guideInfoStoreMap.get("GoGuideInfoStore");
        }

        if (guideInfoStore.isReadyToTakeAOrder(force, orderTime)) {
            guideInfoStore.notifyOtherStore(orderTime, navInfo, guideInfoStoreMap);
            return guideInfoStore;
        } else return null;
    }

    private List<GuideInfoStore> connectPoiGuideStores(boolean force, User user, NavInfo navInfo, long orderTime){
        List<GuideInfoStore> guideInfoStores = new ArrayList<>();
//        if (navigatingInformation.isArrived()) return guideInfoStores;
        GuideInfoStore guideInfoStore;
        List<DetectedPoiInfo> nearByPoiList = navInfo.getNearByPoiList();
        List<DetectedPoiInfo> nearBySafetyList = navInfo.getNearBySafetyList();

        long noneDetectedPoiCount = nearByPoiList.stream().filter(detectedPoiInformation -> !user.getDetectedPOI().contains(detectedPoiInformation.getId())).count();
//        List<DetectedPoiInformation> noneDetectedPoiList = nearByPoiList.stream().filter(detectedPoiInformation -> !user.getDetectedPOI().contains(detectedPoiInformation.getId())).collect(Collectors.toList());
//        navigatingInformation.setNearByPoiList(noneDetectedPoiList);


        if (force || noneDetectedPoiCount > 0) {
            guideInfoStore = guideInfoStoreMap.get("LandmarkGuideInfoStore");
            if (guideInfoStore.isReadyToTakeAOrder(force, orderTime)) guideInfoStores.add(guideInfoStore);
        }
        if (force || (nearBySafetyList != null && nearBySafetyList.size() > 0)) {
            guideInfoStore = guideInfoStoreMap.get("SafetyGuideInfoStore");
            if (guideInfoStore.isReadyToTakeAOrder(force, orderTime)) guideInfoStores.add(guideInfoStore);

        }

        return guideInfoStores;
    }

    @Override
    public void registerGuideInfoObserver(GuideInfoObserver observer) {
        guideInfoObservers.add(observer);
    }

    @Override
    public void removeGuideInfoObserver(GuideInfoObserver observer) {
        guideInfoObservers.remove(observer);
    }

}
