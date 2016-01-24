package it.polimi.stopit.model;

import android.graphics.Bitmap;

import com.google.android.gms.wearable.DataMap;

import java.io.Serializable;

/**
 * Created by matteo on 05/12/15.
 */
public class User implements Serializable{

    private String ID;
    private String name,surname;
    private String profilePic;
    private Long points;
    private Long weekPoints;
    private Long dayPoints;

    private Bitmap img;

    public User(){}

    public User(String id,String name,String surname,String profilePic,Long points,Long dayPoints,Long weekPoints){

        this.ID=id;
        this.name=name;
        this.surname=surname;
        this.profilePic=profilePic;
        this.points=points;
        this.dayPoints=dayPoints;
        this.weekPoints = weekPoints;

    }

    public User(DataMap map) {
        this(map.getString("ID"),
                map.getString("name"),map.getString("surname"),map.getString("profilePic"),
                map.getLong("points"),map.getLong("weekPoints"),map.getLong("dayPoints")
        );
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
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

    public DataMap putToDataMap(DataMap map) {
        map.putString("ID",this.getID());
        map.putString("name",this.getName());
        map.putString("surname", this.getSurname());
        map.putString("profilePic", this.getProfilePic());
        map.putLong("points", this.getPoints());
        map.putLong("dayPoints", this.getDayPoints());
        map.putLong("weekPoints", this.getWeekPoints());
        return map;
    }
}


