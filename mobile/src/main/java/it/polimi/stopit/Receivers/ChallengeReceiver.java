package it.polimi.stopit.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.joda.time.Interval;

import it.polimi.stopit.controller.Controller;
import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Challenge;

/**
 * Created by matteo on 04/01/16.
 */
public class ChallengeReceiver extends BroadcastReceiver {

    String name;
    Challenge challenge;

    @Override
    public void onReceive(Context context, Intent intent) {

        final Controller controller=new Controller(context);
        final DatabaseHandler db=new DatabaseHandler(context);

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        final String challengeKey=intent.getExtras().getString("challengekey");

        Firebase.setAndroidContext(context);
        final Firebase fire = new Firebase("https://blazing-heat-3084.firebaseio.com/Challenges/"+challengeKey);

        fire.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot challengeSnapshot) {

                challenge = db.getChallenge(challengeKey);
                challenge.setOver(true);

                //se tu sei opponent
                if (challengeSnapshot.child("opponentID").getValue().toString().equals(settings.getString("ID", null))) {

                    Firebase users = new Firebase("https://blazing-heat-3084.firebaseio.com/Users/" +
                            challengeSnapshot.child("id").getValue().toString());
                    users.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            name = snapshot.child("name").getValue().toString();

                            //se ha vinto/perso/pareggiato l'opponent e tu sei l'opponent
                            if ((long) challengeSnapshot.child("opponentPoints").getValue() > (long) challengeSnapshot.child("myPoints").getValue()) {
                                controller.sendCustomNotification("Challenge over!", "Your challenge vs " + name + " is over. You won!");
                                challenge.setWon(true);
                            } else if ((long) challengeSnapshot.child("opponentPoints").getValue() < (long) challengeSnapshot.child("myPoints").getValue()) {
                                controller.sendCustomNotification("Challenge over!", "Your challenge vs " + name + " is over. You lose");
                            } else if ((long) challengeSnapshot.child("opponentPoints").getValue() == (long) challengeSnapshot.child("myPoints").getValue()) {
                                controller.sendCustomNotification("Challenge over!", "Your challenge vs " + name + " is over. Tie game!");
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }

                    });
                }
                //se sei il lanciatore della sfida
                else if (challengeSnapshot.child("id").getValue().toString().equals(settings.getString("ID", null))) {

                    Firebase users = new Firebase("https://blazing-heat-3084.firebaseio.com/Users/" +
                            challengeSnapshot.child("opponentID").getValue().toString());
                    users.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            name = snapshot.child("name").getValue().toString();

                            //se ha vinto/perso/pareggiato
                            if ((long) challengeSnapshot.child("opponentPoints").getValue() < (long) challengeSnapshot.child("myPoints").getValue()) {
                                controller.sendCustomNotification("Challenge over!", "Your challenge vs " + name + " is over. You won!");
                                challenge.setWon(true);
                            } else if ((long) challengeSnapshot.child("opponentPoints").getValue() > (long) challengeSnapshot.child("myPoints").getValue()) {
                                controller.sendCustomNotification("Challenge over!", "Your challenge vs " + name + " is over. You lose");
                            } else if ((long) challengeSnapshot.child("opponentPoints").getValue() == (long) challengeSnapshot.child("myPoints").getValue()) {
                                controller.sendCustomNotification("Challenge over!", "Your challenge vs " + name + " is over. Tie game!");
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }

                    });
                }
                db.updateChallenge(challenge);
                fire.removeValue();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }

        });
    }
}
