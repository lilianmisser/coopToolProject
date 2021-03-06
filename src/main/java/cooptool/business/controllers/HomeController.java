package cooptool.business.controllers;

import cooptool.business.ViewLoader;
import cooptool.business.ViewPath;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * HomeController class
 */
public class HomeController {

    @FXML
    private Button mentoringDemandButton;

    @FXML
    private Button quickHelpPostButton;
    
    /**
     * Switch to the mentoring demand home page view
     */
    public void displayMentoringDemand() {
        ViewLoader.getInstance().load(ViewPath.MENTORING_DEMAND_HOME_PAGE);
    }

    /**
     * Switch to the quick help post home page view
     */
    public void displayQuickHelpPost() {
        ViewLoader.getInstance().load(ViewPath.QUICK_HELP_POST_HOME_PAGE);
    }
}
