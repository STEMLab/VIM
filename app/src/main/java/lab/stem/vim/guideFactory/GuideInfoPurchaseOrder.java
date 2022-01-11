package lab.stem.vim.guideFactory;

import lab.stem.vim.core.User;
import lab.stem.vim.core.NavInfo;

public class GuideInfoPurchaseOrder {
    private User user;
    private NavInfo navInfo;
    private boolean isKorean;
    private long orderTime;

    public GuideInfoPurchaseOrder(User user, NavInfo navInfo, boolean isKorean, long orderTime) {
        this.user = user;
        this.navInfo = navInfo;
        this.isKorean = isKorean;
        this.orderTime = orderTime;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NavInfo getNavigatingInformation() {
        return navInfo;
    }

    public void setNavigatingInformation(NavInfo navInfo) {
        this.navInfo = navInfo;
    }

    public boolean isKorean() {
        return isKorean;
    }

    public void setKorean(boolean korean) {
        isKorean = korean;
    }
}
