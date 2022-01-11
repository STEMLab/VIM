package lab.stem.vim.observer;

public interface GuideInfoSubject {
    void registerGuideInfoObserver(GuideInfoObserver observer);
    void removeGuideInfoObserver (GuideInfoObserver observer);
}
