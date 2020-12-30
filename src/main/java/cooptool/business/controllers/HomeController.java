package cooptool.business.controllers;

import cooptool.business.ViewLoader;
import cooptool.business.ViewPath;
import cooptool.business.facades.NotificationFacade;
import cooptool.business.facades.UserFacade;
import cooptool.models.objects.Notification;
import cooptool.models.objects.StudentRole;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    Pane header_student, header_admin;

    @FXML
    Label notificationNumber;

    private final NotificationFacade notificationFacade = NotificationFacade.getInstance();
    private final ObservableList<Notification> notifications = notificationFacade.getNotifications();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (UserFacade.getInstance().getCurrentUser().getRole() instanceof StudentRole){
            header_admin.setVisible(false);
        } else {
            header_student.setVisible(false);
        }

        notifications.addListener((ListChangeListener<Notification>) c -> {
            int nbNotifications = c.getList().size();
            notificationNumber.setText(nbNotifications != 0 ? String.valueOf(nbNotifications) : "");
        });

        /*notificationFacade.getNbNotifications().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                notificationNumber.setText(newValue.toString());
            }
        });*/
    }

    public void displayMentoringDemand() {
        ViewLoader.getInstance().load(ViewPath.MENTORING_DEMAND_HOME_PAGE);
    }

    public void displayQuickHelp() {

    }

    public void goToNotificationPage(MouseEvent mouseEvent) {

        Object[] tabNotifications = notifications.toArray();

        ViewLoader.getInstance().load(ViewPath.NOTIFICATIONS, tabNotifications);
    }
}
