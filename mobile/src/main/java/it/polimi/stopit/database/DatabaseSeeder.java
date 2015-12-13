package it.polimi.stopit.database;

import android.content.Context;

import it.polimi.stopit.model.Achievement;

public class DatabaseSeeder {

    private DatabaseHandler db;

    public DatabaseSeeder(Context context){

        db=new DatabaseHandler(context);
    }

    public void seedAchievements(){

        db.addAchievement(new Achievement(1,"One Day Stop","Not smoke for one day",200,"void",false));
        db.addAchievement(new Achievement(2,"Three Days Stop","Not smoke for three days",400,"void",false));
        db.addAchievement(new Achievement(3,"One Week Stop","Not smoke for one week",600,"void",false));
        db.addAchievement(new Achievement(4,"Two Weeks Stop","Not smoke for two weeks",800,"void",false));
        db.addAchievement(new Achievement(5,"One Month Stop","Not smoke for one month",1000,"void",false));
        db.addAchievement(new Achievement(6,"Beginner","Do an alternative activity",100,"void",false));
        db.addAchievement(new Achievement(6,"10 Activities","Do 10 alternative activities",200,"void",false));
        db.addAchievement(new Achievement(7,"20 Activities","Do 20 alternative activities",400,"void",false));
        db.addAchievement(new Achievement(8,"50 Activities","Do 50 alternative activities",600,"void",false));

    }
}
