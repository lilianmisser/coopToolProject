package cooptool.models.daos;

public class MySQLDAOFactory extends AbstractDAOFactory {

    public MySQLDAOFactory() {
    }

    @Override
    public UserDAO getUserDAO() {
        return new MySQLUserDAO();
    }
}
