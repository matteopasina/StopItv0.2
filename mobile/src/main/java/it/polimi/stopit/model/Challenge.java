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
    private Boolean challenger;
    private Boolean over;
    private Boolean won;

    public Challenge(String ID, String opponentID, long points, long opponentpoints, long starttime, long endtime, String accepted,
                     String challenger,String over,String won) {
        this.ID=ID;
        this.opponentID=opponentID;
        this.myPoints=points;
        this.opponentPoints=opponentpoints;
        this.startTime=starttime;
        this.endTime=endtime;
        this.accepted=Boolean.valueOf(accepted);
        this.challenger=Boolean.valueOf(challenger);
        this.over=Boolean.valueOf(over);
        this.won=Boolean.valueOf(won);
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

    public boolean isChallenger() {
        return challenger;
    }

    public void setChallenger(boolean challenger) {
        this.challenger = challenger;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }
}
