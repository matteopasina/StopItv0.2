package it.polimi.stopit.controller;

import android.content.Context;

import java.util.ArrayList;

import it.polimi.stopit.database.DatabaseHandler;
import it.polimi.stopit.model.MoneyTarget;

/**
 * Created by alessiorossotti on 21/12/15.
 */
public class Controller {

    DatabaseHandler db;
    Context context;

    public Controller(Context context){

        this.context=context;
        db=new DatabaseHandler(context);
    }

    public void dailyControl(){

    }

    public void updateMoneyTarget(Context context,int moneySaved){

        ArrayList<MoneyTarget> moneyTargets=db.getAllTargets();
        MoneyTarget currentTarget=new MoneyTarget();
        boolean first=false;

        for(MoneyTarget target:moneyTargets){

            if((target.getMoneySaved()!=target.getMoneyAmount()) && first==false){

                first=true;
                currentTarget=target;
            }

        }

        if(first==false) return;


        long newMoney=currentTarget.getMoneySaved()+moneySaved;

        if(newMoney>=currentTarget.getMoneyAmount()){

            currentTarget.setMoneySaved(currentTarget.getMoneyAmount());
            currentTarget.setDuration(0);
        }
        else{
            currentTarget.setMoneySaved(newMoney);
        }


        db.updateMoneyTarget(currentTarget);
    }
}
