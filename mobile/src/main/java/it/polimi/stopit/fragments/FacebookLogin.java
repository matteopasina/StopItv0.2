package it.polimi.stopit.fragments;

/**
 * Created by matteo on 05/12/15.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.joda.time.MutableDateTime;

import java.util.Calendar;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.FirstLoginSettingsActivity;
import it.polimi.stopit.activities.NavigationActivity;
import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.User;

public class FacebookLogin extends Fragment {

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fb, container, false);

        if(Profile.getCurrentProfile() != null){

            if (new DatabaseHandler(getActivity()).getAllAchievements().size() != 0 ) {

                Intent intent = new Intent(getContext(), NavigationActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();

            } else {

                Intent intent = new Intent(getContext(), FirstLoginSettingsActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        }


        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();

                        MutableDateTime now = new MutableDateTime();
                        Calendar calendar = Calendar.getInstance();
                        Controller controller = new Controller(getActivity());
                        now.setMonthOfYear(calendar.get(Calendar.MONTH) + 1);

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        settings.edit().clear();
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("ID", Profile.getCurrentProfile().getId());
                        editor.putString("name", Profile.getCurrentProfile().getFirstName());
                        editor.putString("surname", Profile.getCurrentProfile().getLastName());
                        editor.putLong("points", 0);
                        editor.putString("image", "https://graph.facebook.com/" + Profile.getCurrentProfile().getId() + "/picture?type=large");
                        editor.putLong("dayPoints", 0);
                        editor.putLong("weekPoints", 0);
                        editor.putString("lastDayCheck", controller.getStringTime(now));
                        editor.putString("lastWeekCheck", controller.getSundayStringTime(now));
                        editor.commit();

                        final User user=new User();
                        user.setID(settings.getString("ID", null));
                        user.setName(settings.getString("name", null));
                        user.setSurname(settings.getString("surname", null));
                        user.setPoints(settings.getLong("points", 0));
                        user.setProfilePic(settings.getString("image", null));
                        user.setDayPoints(settings.getLong("dayPoints", 0));
                        user.setWeekPoints(settings.getLong("weekPoints", 0));
                        user.setLastDayCheck(settings.getString("lastDayCheck", null));
                        user.setLastWeekCheck(settings.getString("lastWeekCheck", null));

                        Firebase.setAndroidContext(getActivity());
                        final Firebase myFirebaseRef = new Firebase("https://blazing-heat-3084.firebaseio.com/Users");

                        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                if (!snapshot.child(user.getID()).exists()) {

                                    myFirebaseRef.child(user.getID()).setValue(user);

                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });


                        Intent intent = new Intent(getContext(), FirstLoginSettingsActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getActivity(), "Login canceled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getActivity(), "Login error", Toast.LENGTH_SHORT).show();
                    }
                }

        );

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
