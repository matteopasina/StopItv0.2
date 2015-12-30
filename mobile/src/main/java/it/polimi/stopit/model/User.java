package it.polimi.stopit.model;

/**
 * Created by matteo on 05/12/15.
 */
public class User {

    private String ID;
    private String name,surname;
    private String profilePic;
    private Long points;
    private Long weekPoints;
    private Long dayPoints;

    public User(){}

    public User(String id,String name,String surname,String profilePic,Long points){

        this.ID=id;
        this.name=name;
        this.surname=surname;
        this.profilePic=profilePic;
        this.points=points;
    }
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public Long getWeekPoints() {
        return weekPoints;
    }

    public void setWeekPoints(long weekPoints) {
        this.weekPoints = weekPoints;
    }

    public Long getDayPoints() {
        return dayPoints;
    }

    public void setDayPoints(long dayPoints) {
        this.dayPoints = dayPoints;
    }
}

