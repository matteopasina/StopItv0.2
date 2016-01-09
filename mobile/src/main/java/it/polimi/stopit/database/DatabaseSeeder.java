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

        db.addAchievement(new Achievement(1, "One Day Stop", "Don't smoke for one day", 200, R.drawable.stop_one_day, false));
        db.addAchievement(new Achievement(2, "Three Days Stop", "Don't smoke for three days", 400, R.drawable.stop_three_days, false));
        db.addAchievement(new Achievement(3, "One Week Stop", "Don't smoke for one week", 600, R.drawable.stop_one_week, false));
        db.addAchievement(new Achievement(4, "Two Weeks Stop", "Don't smoke for two weeks", 800, R.drawable.stop_two_weeks, false));
        db.addAchievement(new Achievement(5, "One Month Stop", "Don't smoke for one month", 1000, R.drawable.stop_one_month, false));
        db.addAchievement(new Achievement(6, "Beginner", "Do an alternative activity", 100, R.drawable.achievement_star, false));
        db.addAchievement(new Achievement(7, "10 Activities", "Do 10 alternative activities", 200, R.drawable.achievement_star, false));
        db.addAchievement(new Achievement(8, "20 Activities", "Do 20 alternative activities", 400, R.drawable.achievement_star, false));
        db.addAchievement(new Achievement(9, "50 Activities", "Do 50 alternative activities", 600, R.drawable.achievement_star, false));
        db.addAchievement(new Achievement(10, "Sportsman", "Do 10 sport activities", 1000, R.drawable.sportsman, false));
        db.addAchievement(new Achievement(11, "Social", "Do 10 social activities", 1000, R.drawable.social, false));
        db.addAchievement(new Achievement(12, "Artist", "Do 10 art activities", 1000, R.drawable.artist, false));
        db.addAchievement(new Achievement(13, "Food", "Do 10 food activities", 1000, R.drawable.food, false));
        db.addAchievement(new Achievement(14, "Saver", "Complete a money target", 1000, R.drawable.saver, false));
        db.addAchievement(new Achievement(15, "Super Saver", "Complete 3 money targets", 3000, R.drawable.super_saver, false));
        db.addAchievement(new Achievement(16, "Top10", "In all time leaderboard", 100, R.drawable.winner, false));
        db.addAchievement(new Achievement(17, "Top3", "In all time leaderboard", 250, R.drawable.winner, false));
        db.addAchievement(new Achievement(18, "Winner", "First in all time leaderboard", 500, R.drawable.winner, false));
        db.addAchievement(new Achievement(19, "Challenger", "Win a challenge", 500, R.drawable.challenge, false));
        db.addAchievement(new Achievement(20, "Super Challenger", "Win 5 challenges", 2000, R.drawable.five_challenge, false));

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
