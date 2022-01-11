package lab.stem.vim.observer;

public interface VIMIndoorFeaturesSubject {
    void registerVIMIndoorFeaturesObserver(VIMIndoorFeaturesObserver observer);
    void removeVIMIndoorFeaturesObserver (VIMIndoorFeaturesObserver observer);
    void notifyVIMIndoorFeaturesObserver();
}
