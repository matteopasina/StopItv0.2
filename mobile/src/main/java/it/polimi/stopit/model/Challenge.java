package it.polimi.stopit.model;

/**
 * Created by alessiorossotti on 14/12/15.
 */
public class Challenge {

    private String ID;
    private String opponentID;
    private long myPoints;
    private long opponentPoints;
    private long startTime;
    private long endTime;
    private Boolean accepted;

    public Challenge(String ID, String opponentID, long points, long opponentpoints, long starttime, long endtime, String accepted) {
        this.ID=ID;
        this.opponentID=opponentID;
        this.myPoints=points;
        this.opponentPoints=opponentpoints;
        this.startTime=starttime;
        this.endTime=endtime;
        this.accepted=Boolean.valueOf(accepted);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getOpponentID() {
        return opponentID;
    }

    public void setOpponentID(String opponentID) {
        this.opponentID = opponentID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

}
