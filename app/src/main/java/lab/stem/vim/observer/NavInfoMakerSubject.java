package lab.stem.vim.observer;

import lab.stem.vim.core.Route;

public interface NavInfoMakerSubject {
    void registerNavInfoMakerObserver(NavInfoMakerObserver observer);
    void notifyUpdateRoute(Route route);
}
