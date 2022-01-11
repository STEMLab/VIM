package lab.stem.vim.indoorGML;

public class GmlInfo {
    private String description;
    private String name;

    public GmlInfo() {
    }

    public GmlInfo(String description, String name) {
        this.description = description;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
