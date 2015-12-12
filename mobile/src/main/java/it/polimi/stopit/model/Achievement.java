package it.polimi.stopit.model;

/**
 * Created by alessiorossotti on 12/12/15.
 */
public class Achievement {

    private String id;
    private String title;
    private String description;
    private long points;
    private boolean obtained;

    public boolean isObtained() {
        return obtained;
    }

    public void setObtained(boolean obtained) {
        this.obtained = obtained;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    public String getId() {
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

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPoints(long points) {
        this.points = points;
    }
}
