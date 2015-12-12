package it.polimi.stopit.fragments;

/**
 * Created by matteo on 05/12/15.
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import it.polimi.stopit.R;
import it.polimi.stopit.activities.FirstLoginSettingsActivity;
import it.polimi.stopit.activities.NavigationActivity;

public class FacebookLogin extends Fragment {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    public static final String PREFS_NAME = "StopItPrefs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fb, container, false);

        if(Profile.getCurrentProfile()!=null){
            Intent intent = new Intent(getContext(),NavigationActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                settings.edit().clear();
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("ID", Profile.getCurrentProfile().getId());
                editor.putString("name", Profile.getCurrentProfile().getFirstName());
                editor.putString("surname", Profile.getCurrentProfile().getLastName());
                editor.putLong("points", 0);
                editor.putString("image", "https://graph.facebook.com/" + Profile.getCurrentProfile().getId() + "/picture?type=large");
                // Commit the edits!
                editor.commit();
                Intent intent = new Intent(getContext(),FirstLoginSettingsActivity.class);
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
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
