package it.polimi.stopit.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;

import java.util.ArrayList;

import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.Cigarette;
import it.polimi.stopit.model.MoneyTarget;

/**
 * Created by alessiorossotti on 21/12/15.
 */
public class Controller {

    DatabaseHandler db;
    Context context;
    SharedPreferences settings;

    public Controller(Context context){

        this.context = context;
        db = new DatabaseHandler(context);
        settings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void dailyMoneyControl(){

        Instant instant=new Instant();
        int year=instant.get(DateTimeFieldType.year());
        int month=instant.get(DateTimeFieldType.monthOfYear());
        int day=instant.get(DateTimeFieldType.dayOfMonth());

        int cigPD=Integer.parseInt(settings.getString("CPD", null));

        int numSmoked=0;

        ArrayList<Cigarette> todayCig=db.getDailyCigarettes(year,month,day);

        for(Cigarette cig:todayCig){

            if(cig.getType().equals("smoke")){

                numSmoked++;

            }
        }

        updateMoneyTarget((cigPD-numSmoked));

    }

    // updates money saved, saved is true if the user has smoked less or equal than his cpd

    public void updateMoneyTarget(int notsmoked){

        ArrayList<MoneyTarget> moneyTargets=db.getAllTargets();
        MoneyTarget currentTarget=new MoneyTarget();
        boolean first=false;
        int cigCost=Integer.parseInt(settings.getString("cigcost", null));

        for(MoneyTarget target:moneyTargets){

            if((target.getMoneySaved()!=target.getMoneyAmount()) && first==false){

                first=true;
                currentTarget=target;
            }

        }

        if(first==false) return;

        int moneySaved=(currentTarget.getCigReduced()+notsmoked)*cigCost;

        long newMoney=currentTarget.getMoneySaved()+moneySaved;

        if(newMoney>=currentTarget.getMoneyAmount()){

            currentTarget.setMoneySaved(currentTarget.getMoneyAmount());
            currentTarget.setDuration(0);
        }
        else{
            currentTarget.setMoneySaved(newMoney);
            currentTarget.setDuration(currentTarget.getDuration()-1);
        }


        db.updateMoneyTarget(currentTarget);
    }
}
