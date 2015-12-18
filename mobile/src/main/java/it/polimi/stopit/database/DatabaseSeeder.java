package it.polimi.stopit.database;

import android.content.Context;

import com.facebook.AccessToken;
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

    public void loadContacts(){

        friends=new JSONArray();
        facebookFriends=new ArrayList<>();
        contacts=db.getAllContacts();

        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try {
                            friends = response.getJSONObject().getJSONArray("data");
                        } catch (JSONException e) {
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

        db.addMoneyCategory("Phone",R.drawable.iphone);
        db.addMoneyCategory("Laptop",R.drawable.macbook);
        db.addMoneyCategory("Travel",R.drawable.travel);
        db.addMoneyCategory("Other",R.drawable.other);
    }
}
