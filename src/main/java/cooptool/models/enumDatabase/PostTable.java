package cooptool.models.enumDatabase;

/**
 * PostTable enum
 */
public enum PostTable implements TableInterface {

    ID_POST("id_post"),
    DESCRIPTION_POST("description_post"),
    DATE_POST("date_post"),
    TYPE_POST("type_post"),
    ID_USER_CREATOR("id_user_creator"),
    ID_SUBJECT("id_subject");

    @Override
    public String toString() {
        return path;
    }

    private final String path;

    PostTable(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
