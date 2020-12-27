package cooptool.models.enumDatabase;

public enum BrowsingHistoryTable implements PapaInterface {

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
