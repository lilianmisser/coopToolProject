package cooptool.models.objects;

import java.time.LocalDate;
import java.util.ArrayList;

public class MentoringDemand {

    private int id;
    private Subject subject;
    private String description;
    private LocalDate creationDate;
    private ArrayList<Schedule> schedules;
    private User creator;

    public int getId(){
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getCreationDate(){
        return creationDate;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public String schedulesToString(){
        String res = "";
        for(Schedule schedule : schedules){
            res += schedule.getDate().toString() + "\n";
        }
        return res;
    }

    public User getCreator() {
        return creator;
    }

    public MentoringDemand(int id, Subject subject, String description, LocalDate creationDate, ArrayList<Schedule> schedules, User creator){
        this.id = id;
        this.subject = subject;
        this.description = description;
        this.schedules = schedules;
        this.creator = creator;
    }

    public void addSchedule(Schedule schedule){

    }

    public void removeSchedule(Schedule schedule){

    }
}
