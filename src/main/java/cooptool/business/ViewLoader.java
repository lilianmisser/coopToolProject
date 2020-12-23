package cooptool.business;
import cooptool.utils.MapResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class ViewLoader extends Parent {

    private static ViewLoader viewLoader = null;

    private Stage stage;

    private LinkedList<ViewPath> previousView = new LinkedList<ViewPath>();

    private ViewLoader() {

    }

    public static ViewLoader getInstance() {

        if (viewLoader == null) {
            viewLoader = new ViewLoader();
        }

        return viewLoader;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void load(ViewPath view, Object... objects) {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(view.getPath()));

        //loader.setController(loader.getController());
        loader.setResources(new MapResourceBundle(objects));

        Parent root;

        previousView.add(view);

        try {
            root = loader.load();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ViewPath getPreviousPath (){
        this.previousView.removeLast();
        return this.previousView.getLast();
    }

}
