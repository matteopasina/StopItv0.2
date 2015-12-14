package it.polimi.stopit.model;

/**
 * Created by alessiorossotti on 14/12/15.
 */
public class Challenge {

    private String ID;
    private String name,surname;
    private String profilePic;
    private long myPoints;
    private long opponentPoints;
    private String startTime;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public long getMyPoints() {
        return myPoints;
    }

    public void setMyPoints(long myPoints) {
        this.myPoints = myPoints;
    }

    public long getOpponentPoints() {
        return opponentPoints;
    }

    public void setOpponentPoints(long opponentPoints) {
        this.opponentPoints = opponentPoints;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

}
