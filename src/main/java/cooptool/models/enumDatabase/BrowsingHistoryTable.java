package cooptool.models.enumDatabase;

/**
 * BrowsingHistoryTable enum
 */
public enum BrowsingHistoryTable implements TableInterface {

    ID_USER("id_user"),
    ID_POST("id_post");

    @Override
    public String toString() {
        return path;
    }

    private final String path;

    BrowsingHistoryTable(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
