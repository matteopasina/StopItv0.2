package it.polimi.stopit.model;

import android.graphics.Bitmap;

import com.google.android.gms.wearable.DataMap;

import java.io.Serializable;

/**
 * Created by matteo on 24/01/16.
 */
public class Achievement implements Serializable{

    private int id;
    private String title;
    private String description;
    private long points;
    private int imageResource;
    private boolean obtained;
    private Bitmap img;

    public Achievement(){

    }

    public Achievement(int id,String title,long points,String description,boolean obtained){

        this.id=id;
        this.title=title;
        this.description=description;
        this.points=points;
        this.obtained=obtained;
        this.img=null;

    }

    public Achievement(int id,String title,String description,long points,int imageResource,boolean obtained){

        this.id=id;
        this.title=title;
        this.description=description;
        this.points=points;
        this.imageResource=imageResource;
        this.obtained=obtained;
    }

    public Achievement(DataMap map) {
        this(   map.getInt("ID"),
                map.getString("title"),
                map.getLong("points"),
                map.getString("description"),
                map.getBoolean("obtained")
        );
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public boolean isObtained() {
        return obtained;
    }

    public void setObtained(boolean obtained) {
        this.obtained = obtained;
    }

    public int getImage() {
        return imageResource;
    }

    public void setImage(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getPoints() {
        return points;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public DataMap putToDataMap(DataMap map) {
        map.putInt("ID", this.getId());
        map.putString("title", this.getTitle());
        map.putLong("points", this.getPoints());
        map.putString("description", this.getDescription());
        return map;
    }
}