package lab.stem.vim.guideFactory;

import lab.stem.vim.core.VIMSTATE;

public abstract class GuideInfo implements Comparable {
    protected VIMSTATE vimState;
    protected String type;
    protected String message;

    public abstract void assemble(FACTORY_ORDER_TYPE factoryOrder, GuideInfoPurchaseOrder guideInfoPurchaseOrder);

    public VIMSTATE getState() {
        return vimState;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public boolean isNothing() {
        if (message == null) return true;
        return message.length() <= 0;
    }

    @Override
    public int compareTo(Object o) {
        GuideInfo guideInfo = (GuideInfo) o;
        return this.vimState.compareTo(guideInfo.vimState);
    }

    @Override
    public String toString() {
        return "GuideInfo{" +
                "vimState=" + vimState +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
