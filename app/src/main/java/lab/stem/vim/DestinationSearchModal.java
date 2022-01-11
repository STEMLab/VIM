package lab.stem.vim;

import ir.mirrajabi.searchdialog.core.Searchable;
import lab.stem.vim.core.DestinationInfo;

public class DestinationSearchModal implements Searchable, Comparable{
    private final DestinationInfo destinationInfo;
    private final String language;

    public DestinationSearchModal(String language, DestinationInfo destinationInfo) {
        this.language = language;
        this.destinationInfo = destinationInfo;
    }

    @Override
    public String getTitle() {
        return this.destinationInfo.getName(this.language);
    }

    public String getId() {
        return this.destinationInfo.getId();
    }

    public DestinationInfo getDestinationInfo() {
        return destinationInfo;
    }

    @Override
    public int compareTo(Object o) {
        DestinationSearchModal needCompareObject = (DestinationSearchModal) o;
        return this.getTitle().compareTo(needCompareObject.getTitle());
    }
}
