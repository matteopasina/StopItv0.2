package it.polimi.stopit.model;

import java.util.Date;

/**
 * Created by matteo on 15/12/15.
 */
public class Cigarette {

    private int id;
    private Date date;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
