package it.polimi.stopit.activities;

/**
 * Created by matteo on 05/12/15.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import it.polimi.stopit.R;
import it.polimi.stopit.model.User;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        User user=new User();

        user.setID(getIntent().getExtras().getString("userID"));
        user.setName(getIntent().getExtras().getString("name"));
        user.setSurname(getIntent().getExtras().getString("surname"));
        user.setProfilePic(getIntent().getExtras().getString("imageURL"));

        TextView username = (TextView) findViewById(R.id.username);

        username.setText(""+user.getName()+" "+user.getSurname());
        CircularImageView profilepic=(CircularImageView) findViewById(R.id.profilepic);
        Picasso.with(getApplicationContext()).load(user.getProfilePic()).into(profilepic);

    }
}

