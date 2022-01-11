package lab.stem.vim.guideFactory;

import java.util.List;
import lab.stem.vim.core.User;
import lab.stem.vim.core.DetectedPoiInfo;
import lab.stem.vim.core.Indicator;

abstract public class PoiGuideInfoFactory implements GuideInfoFactory{
    abstract protected String getPOIGuideMessage(boolean isKorean, String distanceForm, DetectedPoiInfo detectedPoiInfo);
    abstract protected String getPOIGuideMessages(boolean isKorean, String distanceForm, boolean ignoreDetected, User user, List<DetectedPoiInfo> detectedPoiInfoList);
    protected Indicator indicator;
}
