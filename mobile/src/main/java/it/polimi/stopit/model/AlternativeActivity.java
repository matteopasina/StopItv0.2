package it.polimi.stopit.model;

import android.graphics.Bitmap;

/**
 * Created by matteo on 09/01/16.
 */
public class AlternativeActivity {

    private int ID;
    private String title;
    private String description;
    private String category;
    private int bonusPoints;
    private int frequency;
    private int image;

    public AlternativeActivity(int ID,String title,  String description, String category, int bonusPoints,  int frequency, int image) {
        this.title = title;
        this.ID = ID;
        this.description = description;
        this.bonusPoints = bonusPoints;
        this.category = category;
        this.frequency = frequency;
        this.image = image;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
