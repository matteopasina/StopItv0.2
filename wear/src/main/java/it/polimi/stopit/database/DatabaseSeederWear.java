package it.polimi.stopit.database;

import android.content.Context;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.model.Achievement;
import it.polimi.stopit.model.User;

import static it.polimi.stopit.R.drawable.artist;
import static it.polimi.stopit.R.drawable.food;

/**
 * Created by matteo on 30/01/16.
 */
public class DatabaseSeederWear {

    private DatabaseHandlerWear db;
    private Context context;

    public DatabaseSeederWear(Context context){

        db=new DatabaseHandlerWear(context);
        this.context=context;
    }

    public void seedAchievements() {

        boolean obtained=false;
        db.addAchievement(new Achievement(1, "One Day Stop", "Don't smoke for one day", 200, R.drawable.stop_one_day, obtained));
        db.addAchievement(new Achievement(2, "Three Days Stop", "Don't smoke for three days", 400, R.drawable.stop_three_days, obtained));
        db.addAchievement(new Achievement(3, "One Week Stop", "Don't smoke for one week", 600, R.drawable.stop_one_week, obtained));
        db.addAchievement(new Achievement(4, "Two Weeks Stop", "Don't smoke for two weeks", 800, R.drawable.stop_two_weeks, obtained));
        db.addAchievement(new Achievement(5, "One Month Stop", "Don't smoke for one month", 1000, R.drawable.stop_one_month, obtained));
        db.addAchievement(new Achievement(6, "Saver", "Complete a money target", 300, R.drawable.saver, obtained));
        db.addAchievement(new Achievement(7, "Super Saver", "Complete 3 money targets", 800, R.drawable.super_saver, obtained));
        db.addAchievement(new Achievement(8, "Top10", "In all time leaderboard", 100, R.drawable.top10, obtained));
        db.addAchievement(new Achievement(9, "Top3", "In all time leaderboard", 250, R.drawable.top3, obtained));
        db.addAchievement(new Achievement(10, "Winner", "First in all time leaderboard", 500, R.drawable.winner, obtained));
        db.addAchievement(new Achievement(11, "Beginner", "Do an alternative activity", 100, R.drawable.alternative, obtained));
        db.addAchievement(new Achievement(12, "10 Activities", "Do 10 alternative activities", 200, R.drawable.alternative10, obtained));
        db.addAchievement(new Achievement(13, "20 Activities", "Do 20 alternative activities", 400, R.drawable.alternative20, obtained));
        db.addAchievement(new Achievement(14, "50 Activities", "Do 50 alternative activities", 600, R.drawable.alternative50, obtained));
        db.addAchievement(new Achievement(15, "Sportsman", "Do 10 sport activities", 500, R.drawable.sportsman, obtained));
        db.addAchievement(new Achievement(16, "Social", "Do 10 social activities", 500, R.drawable.social, obtained));
        db.addAchievement(new Achievement(17, "Artist", "Do 10 art activities", 500, artist, obtained));
        db.addAchievement(new Achievement(18, "Food", "Do 10 food activities", 500, food, obtained));
        db.addAchievement(new Achievement(19, "Novice", "Reach level 10", 200, R.drawable.novice, obtained));
        db.addAchievement(new Achievement(20, "Apprendice", "Reach level 25", 500, R.drawable.apprendice, obtained));
        db.addAchievement(new Achievement(21, "Master", "Reach level 50", 2000, R.drawable.master, obtained));
        db.addAchievement(new Achievement(22, "Legend", "Reach level 100", 5000, R.drawable.shield, obtained));
        db.addAchievement(new Achievement(23, "Challenger", "Win a challenge", 250, R.drawable.challenge, obtained));
        db.addAchievement(new Achievement(24, "Super Challenger", "Win 5 challenges", 1300, R.drawable.five_challenge, obtained));

    }

    public void seedContacts(){

        db.addContact(new User("1", "Paulo", "Dybala", "paulo", Long.parseLong("34110"), Long.parseLong("888"), Long.parseLong("17575"),"",""));
        db.addContact(new User("2", "Lionel", "Messi", "messi", Long.parseLong("89670"), Long.parseLong("3200"), Long.parseLong("18480"),"",""));
        db.addContact(new User("3", "Eden", "Hazard", "hazard", Long.parseLong("17923"), Long.parseLong("2280"), Long.parseLong("7920"),"",""));
        db.addContact(new User("4", "Scarlett", "Johansson", "scarlett", Long.parseLong("24560"), Long.parseLong("1277"), Long.parseLong("6800"),"",""));
        db.addContact(new User("5", "Guido", "Meda", "guidone", Long.parseLong("79800"), Long.parseLong("560"), Long.parseLong("18340"),"",""));
        db.addContact(new User("6", "Federica", "Nargi", "nargi", Long.parseLong("48267"), Long.parseLong("650"), Long.parseLong("12700"),"",""));
        db.addContact(new User("7", "Alessandro", "Del Piero", "alexxxx", Long.parseLong("68880"), Long.parseLong("721"), Long.parseLong("8180"),"",""));
        db.addContact(new User("8", "Gianluigi", "Buffon", "gigione", Long.parseLong("11450"), Long.parseLong("1000"), Long.parseLong("3200"),"",""));
        db.addContact(new User("9", "Stephen", "Curry", "steph128", Long.parseLong("43200"), Long.parseLong("100"), Long.parseLong("2950"),"",""));
        db.addContact(new User("10", "Joe", "Bastianich", "joe128", Long.parseLong("54277"), Long.parseLong("-250"), Long.parseLong("1200"),"",""));

    }

}
