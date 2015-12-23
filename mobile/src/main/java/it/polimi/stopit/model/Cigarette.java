package it.polimi.stopit.model;

import org.joda.time.DateTime;

/**
 * Created by matteo on 15/12/15.
 */
public class Cigarette {

    private int id;
    private DateTime date;
    private String type;

    public Cigarette(){}

    public Cigarette(int id,DateTime date,String type){

        this.id=id;
        this.date=date;
        this.type=type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
