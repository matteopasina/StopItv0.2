package it.polimi.stopit.database;

import android.content.Context;

import it.polimi.stopit.R;
import it.polimi.stopit.model.Achievement;

public class DatabaseSeeder {

    private DatabaseHandler db;

    public DatabaseSeeder(Context context){

        db=new DatabaseHandler(context);
    }

    public void seedAchievements(){

        db.addAchievement(new Achievement(1,"One Day Stop","Not smoke for one day",200, R.drawable.achievement_stop,true));
        db.addAchievement(new Achievement(2,"Three Days Stop","Not smoke for three days",400,R.drawable.achievement_stop,true));
        db.addAchievement(new Achievement(3,"One Week Stop","Not smoke for one week",600,R.drawable.achievement_stop,false));
        db.addAchievement(new Achievement(4,"Two Weeks Stop","Not smoke for two weeks",800,R.drawable.achievement_stop,false));
        db.addAchievement(new Achievement(5,"One Month Stop","Not smoke for one month",1000,R.drawable.achievement_stop,false));
        db.addAchievement(new Achievement(6,"Beginner","Do an alternative activity",100,R.drawable.achievement_star,true));
        db.addAchievement(new Achievement(7,"10 Activities","Do 10 alternative activities",200,R.drawable.achievement_star,false));
        db.addAchievement(new Achievement(8,"20 Activities","Do 20 alternative activities",400,R.drawable.achievement_star,false));
        db.addAchievement(new Achievement(9,"50 Activities","Do 50 alternative activities",600,R.drawable.achievement_star,false));

    }
}
