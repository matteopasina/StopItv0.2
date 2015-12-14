package it.polimi.stopit.model;

/**
 * Created by alessiorossotti on 12/12/15.
 */
public class Achievement {

    private int id;
    private String title;
    private String description;
    private long points;
    private int imageResource;
    private boolean obtained;

    public Achievement(){

    }

    public Achievement(int id,String title,String description,long points,int imageResource,boolean obtained){

        this.id=id;
        this.title=title;
        this.description=description;
        this.points=points;
        this.imageResource=imageResource;
        this.obtained=obtained;
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
}
