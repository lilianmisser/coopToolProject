package cooptool.business.controllers.mentoringDemandManagement;

import cooptool.business.ViewLoader;
import cooptool.business.ViewPath;
import cooptool.business.facades.MentoringDemandFacade;
import cooptool.business.facades.NotificationFacade;
import cooptool.business.facades.PostFacade;
import cooptool.business.facades.UserFacade;
import cooptool.exceptions.CommentFormatException;
import cooptool.models.objects.*;
import cooptool.utils.TimeUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * MentoringDemandController class
 */
public class MentoringDemandController implements Initializable {

    @FXML
    private Text descriptionArea;

    @FXML
    private Label creatorLabel,subjectLabel,participationLabel,infoLabel,errorLabel;

    @FXML
    private Button learnButton,teachButton,suppressParticipationButton,addScheduleButton,editDescriptionButton,deleteButton,commentButton;

    @FXML
    private GridPane schedulesPane;

    @FXML
    private ScrollPane commentsPane;

    @FXML
    private TextArea commentArea;

    /**
     * Mentoring demand
     */
    private MentoringDemand demand;

    /**
     * Attribute to access to the mentoring demand facade
     */
    private final MentoringDemandFacade mentoringDemandFacade = MentoringDemandFacade.getInstance();

    /**
     * Attribute to access to the post facade
     */
    private final PostFacade postFacade = PostFacade.getInstance();

    /**
     * Attribute to access to the user facade
     */
    private final UserFacade userFacade = UserFacade.getInstance();

    /**
     * Attribute to access to the notification facade
     */
    private final NotificationFacade notificationFacade = NotificationFacade.getInstance();

    /**
     * Attribute to access to the View loader
     */
    private final ViewLoader viewLoader = ViewLoader.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!userFacade.isCurrentUserStudent()){
            disableStudentRights();
        }

        try {
            int idDemand = (int) resources.getObject("1");
            demand = mentoringDemandFacade.getMentoringDemand(idDemand);
            setDescription();
            setCreatorInfos();
            setSubjectInfos();
            setParticipantsInfos();
            setComments();

            //Disabling edition and deletion is the current user is not the creator of the demand
            if(!mentoringDemandFacade.isCurrentUserCreatorOfDemand(demand)) {
                editDescriptionButton.setVisible(false);
                deleteButton.setVisible(false);
            }

            Participation currentUserParticipation = mentoringDemandFacade.getCurrentUserParticipation(demand);
            //User participates to the mentoring demand
            if(currentUserParticipation != null){
                learnButton.setVisible(false);
                teachButton.setVisible(false);
                loadSchedulesPaneParticipant(currentUserParticipation);
            }
            //User doesn't participate to the mentoring demand
            else{
                suppressParticipationButton.setVisible(false);
                loadDefaultSchedulesPane();
            }


        } catch (MissingResourceException ignored) {
        }
    }

    /**
     * Function called by the javafx view to suppress the current user participation
     */
    public void suppressParticipation() {
        mentoringDemandFacade.suppressCurrentUserParticipation(demand);
        refresh();
    }

    /**
     * Function called by the javafx view to participate to the demand
     * @param actionEvent Action event
     */
    public void participate(ActionEvent actionEvent) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        for(Node node:schedulesPane.getChildren()) {
            if(GridPane.getColumnIndex(node) == 0){
                CheckBox checkBox = (CheckBox) node;
                if(checkBox.isSelected()){
                    LocalDateTime checkBoxDate = TimeUtils.parse(checkBox.getText());
                    schedules.add(new Schedule(checkBoxDate,null));
                }
            }
        }
        Button sourceButton = (Button) actionEvent.getSource();
        int participationType = sourceButton == learnButton ? MentoringDemand.STUDENT : MentoringDemand.TUTOR;
        if(schedules.size() == 0){
            if(participationType == MentoringDemand.STUDENT){
                errorLabel.setText("You must select a schedule before participating");
            }
            else{
                errorLabel.setText("You must select a schedule before participating");
                addSchedule();
            }
        }
        else{
            mentoringDemandFacade.participate(demand,participationType,schedules);
            refresh();
        }
    }

    private void refresh(){
        viewLoader.load(ViewPath.GET_MENTORING_DEMAND,demand.getId());
    }

    /**
     * Function called by the javafx view to add a schedule to the demand
     */
    public void addSchedule(){
        List<Integer> hours = TimeUtils.getHoursArrayList();
        List<Integer> minutes = TimeUtils.getMinutesArrayList();

        // Create the custom dialog.
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Add new schedule");
        dialog.setHeaderText("Insert fields and submit to create new schedule.");


        // Set the button types.
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        // Create the inside
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Integer> hourBox = new ComboBox<>(FXCollections.observableList(hours));
        ComboBox<Integer> minBox = new ComboBox<>(FXCollections.observableList(minutes));
        DatePicker datePicker = new DatePicker();

        grid.add(new Label("Date:"),0,0);
        grid.add(datePicker,1,0);
        grid.add(new Label("Hour:"), 0, 1);
        grid.add(hourBox, 1, 1);
        grid.add(new Label("Minutes:"), 0, 2);
        grid.add(minBox, 1, 2);


        Node submitButton = dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.setDisable(true);

        hourBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && minBox.getValue() != null && datePicker.getValue() != null){
                submitButton.setDisable(false);
            }
        });

        minBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && hourBox.getValue() != null && datePicker.getValue() != null){
                submitButton.setDisable(false);
            }
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && hourBox.getValue() != null && minBox.getValue() != null){
                submitButton.setDisable(false);
            }
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(hourBox::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return LocalDateTime.of(datePicker.getValue(), LocalTime.of(hourBox.getValue(), minBox.getValue()));
            }
            return null;
        });

        Optional<LocalDateTime> result = dialog.showAndWait();

        result.ifPresent(localDateTime -> {
            try {
                mentoringDemandFacade.addSchedule(demand,localDateTime);
                refresh();
            } catch (Exception exception) {
                errorLabel.setText(exception.getMessage());
            }
        });
    }

    private void addScheduleDeletionButtonIfCreator(Schedule schedule,int counter){
        if(mentoringDemandFacade.isCurrentUserCreatorOfSchedule(schedule)){
            Button deleteSchedule = new Button("Delete schedule");
            deleteSchedule.setOnAction(event -> {
                mentoringDemandFacade.deleteSchedule(demand,schedule);
                refresh();
            });
            schedulesPane.add(deleteSchedule,2,counter);
        }
    }

    /**
     * Function called by the javafx view to edit the description of the demand
     */
    public void editDescription() {
        // Create the custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update description");
        dialog.setHeaderText("Insert new description and submit it to update.");


        // Set the button types.
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the inside
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea description = new TextArea(demand.getDescription());

        grid.add(new Label("Description:"),0,0);
        grid.add(description,1,0);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(description::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return description.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(updatedDesc -> {
            mentoringDemandFacade.updateDescription(demand,updatedDesc);
            refresh();
        });
    }

    /**
     * Function called by the javafx view to delete the demand
     */
    public void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete mentoring demand");
        alert.setHeaderText("Are you sure want to delete this demand?");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();

        if(option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                mentoringDemandFacade.delete(demand);
                viewLoader.load(ViewPath.MENTORING_DEMAND_HOME_PAGE);
            }
        }
    }

    private void setDescription(){
        descriptionArea.setText(String.format("Description :\n%s",demand.getDescription()));
    }

    private void setCreatorInfos(){
        StudentRole creatorStudentRole = (StudentRole) demand.getCreator().getRole();
        creatorLabel.setText(creatorStudentRole.getStudentRepresentation());
    }

    private void setSubjectInfos(){
        Subject demandSubject = demand.getSubject();
        String subjectString = String.format(
                "Subject : %s",
                demandSubject.getName()
        );
        subjectLabel.setText(subjectString);
    }

    private void setParticipantsInfos(){
        ArrayList<Participation> participationArray = demand.getParticipationArray();
        StringBuilder participants = new StringBuilder("Participants :\n");
        int studentsNumber = 0;
        int tutorsNumber = 0;
        for(Participation participation : participationArray){
            String participationType = participation.getParticipationType() == MentoringDemand.STUDENT ? "Student" : "Tutor";
            StudentRole participantRole = (StudentRole) participation.getParticipant().getRole();
            participants.append(String.format(
                    "%s %s %s\n",
                    participantRole.getFirstName(),
                    participantRole.getLastName(),
                    participationType
            ));
            if(participation.getParticipationType() == MentoringDemand.STUDENT){
                studentsNumber += 1;
            }
            else{
                tutorsNumber += 1;
            }
        }
        participationLabel.setText(participants.toString());
        infoLabel.setText(String.format("Students : %d, Tutors : %d",studentsNumber,tutorsNumber));
    }

    private void loadSchedulesPaneParticipant(Participation currentUserParticipation){
        int counter = 0;
        ArrayList<Schedule> schedules = demand.getSchedules();
        for(Schedule schedule:schedules){
            boolean selectedSchedule = false;
            for(Schedule participationSchedule : currentUserParticipation.getParticipationSchedules()){
                if (participationSchedule.getDateTime().equals(schedule.getDateTime())) {
                    selectedSchedule = true;
                    break;
                }
            }
            Text scheduleText = new Text(schedule.toString());
            Color color = selectedSchedule ? Color.GREEN : Color.RED;
            scheduleText.setFill(color);
            schedulesPane.add(
                    scheduleText,0,counter
            );
            Button button;
            if(!selectedSchedule){
                button = new Button("Not available");
                button.setOnAction(event -> {
                    mentoringDemandFacade.participateToSchedule(
                            demand,currentUserParticipation.getParticipationType(),schedule
                    );
                    refresh();
                });
            }
            else{
                button = new Button("I'm available");
                button.setOnAction(event -> {
                    mentoringDemandFacade.quitSchedule(
                            demand,schedule
                    );
                    refresh();
                });
            }
            schedulesPane.add(button,1,counter);
            addScheduleDeletionButtonIfCreator(schedule,counter);
            setNumberOfParticipants(schedule,counter);
            counter++;
        }
    }

    private void loadDefaultSchedulesPane(){
        ArrayList<Schedule> schedules = demand.getSchedules();
        int counter =0;
        for(Schedule schedule: schedules){
            CheckBox checkBox = new CheckBox(schedule.toString());
            schedulesPane.add(checkBox,0,counter);
            addScheduleDeletionButtonIfCreator(schedule,counter);
            setNumberOfParticipants(schedule,counter);
            counter++;
        }
    }

    private void setComments(){
        ArrayList<Comment> comments = demand.getComments();
        GridPane gridPane = new GridPane();
        commentsPane.setContent(gridPane);
        int counter = 0;
        for(Comment comment : comments){
            StudentRole studentRole = (StudentRole) comment.getCreator().getRole();
            Node nodeText = new Text(comment.getContent());
            gridPane.add(nodeText,0,counter);
            GridPane.setMargin(nodeText, new Insets(10, 10, 10, 10));
            Node nodeLabel = new Label(studentRole.getStudentRepresentation());
            gridPane.add(nodeLabel,1,counter);
            GridPane.setMargin(nodeLabel, new Insets(10, 10, 10, 10));
            if(comment.getCreator().getId() == userFacade.getCurrentUser().getId()){
                Button deletionButton = new Button("Delete");
                deletionButton.setStyle("-fx-background-color: #F14521");
                deletionButton.setOnAction(event -> {
                    postFacade.deleteComment(comment);
                    refresh();
                });
                gridPane.add(deletionButton,2,counter);
                GridPane.setMargin(deletionButton, new Insets(10, 10, 10, 10));
            }
            counter++;
        }
    }

    private void setNumberOfParticipants(Schedule schedule, int index){
        int numberOfStudents = 0;
        int numberOfTutors = 0;
        for(Participation participation : demand.getParticipationArray()){
            ArrayList<Schedule> schedulesParticipation = participation.getParticipationSchedules();
            for(Schedule scheduleParticipation : schedulesParticipation){
                if(schedule.getDateTime().equals(scheduleParticipation.getDateTime())){
                    if(participation.getParticipationType() == MentoringDemand.STUDENT){
                        numberOfStudents++;
                    }
                    else if(participation.getParticipationType() == MentoringDemand.TUTOR){
                        numberOfTutors++;
                    }
                }
            }
        }
        schedulesPane.add(new Label(numberOfStudents + " etudiants"),3,index);
        schedulesPane.add(new Label(numberOfTutors + " tuteur"),4,index);
    }

    /**
     * Function called by the javafx view to comment the current demand
     */
    public void comment() {
        try {
            postFacade.comment(commentArea.getText(),demand);
            String text = "nouveau commentaire de " + ((StudentRole)userFacade.getCurrentUser().getRole()).getFirstName() + " concernant votre demande de tutorat : " + demand.getSubject().getName();
            notificationFacade.create(demand.getCreator(), text, demand.getId(), NotificationType.MENTORING_DEMAND);
            refresh();
        } catch (CommentFormatException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void disableStudentRights(){
        commentButton.setVisible(false);
        learnButton.setVisible(false);
        teachButton.setVisible(false);
        addScheduleButton.setVisible(false);
        commentArea.setVisible(false);
    }

    /**
     * Function called by the javafx view to load the previous page
     */
    public void goBack() {
        viewLoader.load(ViewPath.MENTORING_DEMAND_HOME_PAGE);
    }
}
