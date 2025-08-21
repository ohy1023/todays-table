package store.myproject.onlineshop.domain;

public enum AwsFolder {

    BRAND("brand"),
    ITEM("item"),
    RECIPE("recipe");

    private final String folderName;

    AwsFolder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
