package models.mysql;

import models.AbstractDAOFactory;
import models.UserDAO;

public class SQLDAOFactory extends AbstractDAOFactory {

    @Override
    public UserDAO getUserDAO() {
        return null;
    }
}
