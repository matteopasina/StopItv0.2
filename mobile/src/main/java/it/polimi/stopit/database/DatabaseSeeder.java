package it.polimi.stopit.database;

import android.content.Context;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.model.Achievement;
import it.polimi.stopit.model.AlternativeActivity;
import it.polimi.stopit.model.User;

public class DatabaseSeeder {

    private DatabaseHandler db;
    private JSONArray friends;
    private ArrayList<String> facebookFriends;
    private Context context;
    List<User> contacts;

    public DatabaseSeeder(Context context){

        db=new DatabaseHandler(context);
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
        db.addAchievement(new Achievement(17, "Artist", "Do 10 art activities", 500, R.drawable.artist, obtained));
        db.addAchievement(new Achievement(18, "Food", "Do 10 food activities", 500, R.drawable.food, obtained));
        db.addAchievement(new Achievement(19, "Novice", "Reach level 10", 200, R.drawable.novice, obtained));
        db.addAchievement(new Achievement(20, "Apprendice", "Reach level 25", 500, R.drawable.apprendice, obtained));
        db.addAchievement(new Achievement(21, "Master", "Reach level 50", 2000, R.drawable.master, obtained));
        db.addAchievement(new Achievement(22, "Legend", "Reach level 100", 5000, R.drawable.shield, obtained));
        db.addAchievement(new Achievement(23, "Challenger", "Win a challenge", 250, R.drawable.challenge, obtained));
        db.addAchievement(new Achievement(24, "Super Challenger", "Win 5 challenges", 1300, R.drawable.five_challenge, obtained));

        /*db.addMoneyTarget(new MoneyTarget(50, "Phone", (long) 600, (long) 600, 0, R.drawable.phone, 0));
        db.addMoneyTarget(new MoneyTarget(60,"Travel",(long)350,(long)350,0,R.drawable.travel,0));*/
    }

    public void loadContacts(){

        friends=new JSONArray();
        facebookFriends=new ArrayList<>();
        contacts=db.getAllContacts();

        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(context);
        }

        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try {
                            friends = response.getJSONObject().getJSONArray("data");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(friends!=null){

                            for(int i=0;i<friends.length();i++){

                                JSONObject friend= null;

                                try {

                                    friend = friends.getJSONObject(i);
                                    facebookFriends.add(friend.getString("id"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            Firebase.setAndroidContext(context);
                            final Firebase firebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

                            firebaseRef.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    for(String friend:facebookFriends) {

                                        User contact = snapshot.child(friend).getValue(User.class);

                                        if(!contacts.contains(contact)) {

                                            db.addContact(contact);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                    }
                }
        );

        request.executeAsync();

    }

    public void seedMoneyCategories(){

        db.addMoneyCategory("Travel",R.drawable.travel);
        db.addMoneyCategory("Phone",R.drawable.phone);
        db.addMoneyCategory("Tickets",R.drawable.ticket);
        db.addMoneyCategory("Laptop",R.drawable.macbook);
        db.addMoneyCategory("Clothes",R.drawable.clothes);
        db.addMoneyCategory("Bicycle",R.drawable.bicycle);
        db.addMoneyCategory("Cash",R.drawable.money);
        db.addMoneyCategory("Other", R.drawable.other);
    }

    public void seedAlternatives() {

        db.addAlternative(new AlternativeActivity(1, "Eat a Banana", "Instead of smoking, eat a banana","food", 200, 7, R.drawable.banana));
        db.addAlternative(new AlternativeActivity(2, "Run for 20 minutes", "Go for a run", "sport", 400, 5, R.drawable.banana));
        db.addAlternative(new AlternativeActivity(3, "Paint", "Paint something","art", 300, 3, R.drawable.banana));
        db.addAlternative(new AlternativeActivity(4, "Magic points", "Easy points","various", 1000, 1, R.drawable.banana));
        db.addAlternative(new AlternativeActivity(5, "Selfie!", "Take a selfie","social", 200, 7, R.drawable.banana));

    }
}
