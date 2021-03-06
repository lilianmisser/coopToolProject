package cooptool.models.daos;

import cooptool.models.daos.persistent.PostDAO;
import cooptool.models.enumDatabase.BrowsingHistoryTable;
import cooptool.models.objects.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQLPostDAO class
 */
public class MySQLPostDAO extends PostDAO {

    Connection connection = MySQLConnection.getInstance();

    protected MySQLPostDAO() {
        super();
    }

    @Override
    public List<Post> findPostByUser(User user) {

        String query = "SELECT * " +
                "FROM browsing_history b, post p, subject s " +
                "WHERE b.id_post = p.id_post " +
                "AND s.id_subject = p.id_subject " +
                "AND b.id_user = ?;";

        List<Post> result = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result.add(MySQLFactoryObject.createPost(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean deleteOneFromBrowsingHistory(User user, Post post) {

        String query = "DELETE FROM browsing_history WHERE %s = ? AND %s = ?;";

        query = String.format(query,
                BrowsingHistoryTable.ID_USER,
                BrowsingHistoryTable.ID_POST
        );

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, post.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteAllFromBrowsingHistory(User user) {
        String query = "DELETE FROM browsing_history WHERE %s = ?;";

        query = String.format(query,
                BrowsingHistoryTable.ID_USER
        );

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void comment(Comment comment, Post post) {
        String query =
                "INSERT INTO comment (content_comment,date_comment,id_user_creator,id_post) " +
                "VALUES (?,?,?,?)";
        try {
            PreparedStatement insertStatement = connection.prepareStatement(query);
            insertStatement.setString(1, comment.getContent());
            insertStatement.setTimestamp(2, Timestamp.valueOf(comment.getCreationDate()));
            insertStatement.setInt(3,comment.getCreator().getId());
            insertStatement.setInt(4,post.getId());
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getComments(Post post) {
        String query =
                "SELECT * " +
                        "FROM comment " +
                        "JOIN user ON comment.id_user_creator = user.id_user " +
                        "JOIN department ON user.id_department = department.id_department " +
                        "WHERE comment.id_post = ?";
        try {
            PreparedStatement selectStatement = connection.prepareStatement(query);
            selectStatement.setInt(1,post.getId());
            ResultSet res = selectStatement.executeQuery();
            while(res.next()) {
                post.addComment(MySQLFactoryObject.createComment(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteComment(Comment comment) {
        String query =
                "DELETE FROM comment WHERE id_comment = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, comment.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
