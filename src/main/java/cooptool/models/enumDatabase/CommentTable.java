package cooptool.models.enumDatabase;

public enum CommentTable {

    ID_COMMENT("id_comment"),
    CONTENT_COMMENT("content_comment"),
    DATE_COMMENT("date_comment"),
    ID_USER_CREATOR("id_user_creator"),
    ID_POST("id_post");

    private final String path;

    CommentTable(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}