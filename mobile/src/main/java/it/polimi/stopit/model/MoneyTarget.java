package it.polimi.stopit.model;

/**
 * Created by alessiorossotti on 17/12/15.
 */
public class MoneyTarget {

    private int id;
    private long moneyAmount;
    private long moneySaved;
    private int duration;
    private String name;
    private int imageResource;

    public MoneyTarget(){}

    public MoneyTarget(int id,String name,long moneyAmount,long moneySaved,int duration,int imageResource){

        this.id=id;
        this.moneyAmount=moneyAmount;
        this.moneySaved=moneySaved;
        this.duration=duration;
        this.name=name;
        this.imageResource=imageResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMoneySaved() {
        return moneySaved;
    }

    public void setMoneySaved(long moneySaved) {
        this.moneySaved = moneySaved;
    }

    public long getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(long moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int image) {
        this.imageResource = image;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


}
