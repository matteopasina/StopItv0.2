package it.polimi.stopit.Receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.internal.LockOnGetVariable;
import com.firebase.client.Firebase;

import org.joda.time.MutableDateTime;

import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Challenge;

public class ChallengeAcceptReceiver extends BroadcastReceiver {

    private NotificationManager mNM;

    public ChallengeAcceptReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseHandler dbh=new DatabaseHandler(context);
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        if (mNM == null)
            mNM = (NotificationManager)  context.getSystemService(context.NOTIFICATION_SERVICE);
        mNM.cancel(intent.getIntExtra("notificationID", 0));

        Challenge challenge=dbh.getActiveChallengeByOpponentID(intent.getStringExtra("opponent"));

        Log.v("CHALLENGEACCEPTED",""+intent.getBooleanExtra("accepted",false));
        if(intent.getBooleanExtra("accepted",false)){

            Firebase.setAndroidContext(context);
            final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges");
            Firebase newChallenge = fire.push();

            MutableDateTime now = new MutableDateTime();

            challenge.setID(newChallenge.getKey());
            challenge.setAccepted(true);
            challenge.setStartTime(now.getMillis());
            challenge.setEndTime(now.getMillis() + challenge.getEndTime());

            newChallenge.setValue(challenge);
            newChallenge.child("id").setValue(p.getString("ID", null));

            dbh.updateChallenge(challenge);

            final Firebase accept = new Firebase("https://blazing-heat-3084.firebaseio.com/Accepted/" + challenge.getOpponentID());
            accept.child("accepted").setValue(newChallenge.getKey());

            Controller controller = new Controller(context);
            controller.setChallengeAlarm(challenge.getEndTime(),
                    newChallenge.getKey());

        }else{

            dbh.deleteChallenge(intent.getStringExtra("opponent"));

            final Firebase decline = new Firebase("https://blazing-heat-3084.firebaseio.com/Accepted/" + challenge.getOpponentID() );
            decline.child("declined").setValue(p.getString("ID",null));
        }
    }
}
