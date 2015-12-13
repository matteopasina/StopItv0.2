package it.polimi.stopit.model;

/**
 * Created by alessiorossotti on 12/12/15.
 */
public class Achievement {

    private int id;
    private String title;
    private String description;
    private long points;
    private String image;
    private boolean obtained;

    public Achievement(){

    }

    public Achievement(int id,String title,String description,long points,String image,boolean obtained){

        this.id=id;
        this.title=title;
        this.description=description;
        this.points=points;
        this.image=image;
        this.obtained=obtained;
    }
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
}
