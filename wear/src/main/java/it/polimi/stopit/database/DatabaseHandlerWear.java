package it.polimi.stopit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import it.polimi.stopit.model.Achievement;
import it.polimi.stopit.model.Challenge;
import it.polimi.stopit.model.User;

/**
 * Created by matteo on 30/01/16.
 */
public class DatabaseHandlerWear extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StopIt_DB";

    // TABLE ACHIEVEMENTS
    private static final String TABLE_ACHIEVEMENTS = "achievements";

    private static final String ACHIEVEMENT_ID = "id";
    private static final String ACHIEVEMENT_TITLE = "title";
    private static final String ACHIEVEMENT_DESCRIPTION = "description";
    private static final String ACHIEVEMENT_POINTS = "points";
    private static final String ACHIEVEMENT_IMAGE = "image";
    private static final String ACHIEVEMENT_OBTAINED = "obtained";

    // TABLE CONTACTS
    private static final String TABLE_CONTACTS = "contacts";

    private static final String CONTACT_ID = "id";
    private static final String CONTACT_NAME = "name";
    private static final String CONTACT_SURNAME = "surname";
    private static final String CONTACT_IMAGE = "image";
    private static final String CONTACT_POINTS = "points";
    private static final String CONTACT_DAYPOINTS = "daypoints";
    private static final String CONTACT_WEEKPOINTS = "weekpoints";

    // TABLE CHALLENGES
    private static final String TABLE_CHALLENGES = "challenges";

    private static final String CHALLENGE_ID = "id";
    private static final String CHALLENGE_OPPONENTID = "opponentid";
    private static final String CHALLENGE_POINTS = "points";
    private static final String CHALLENGE_OPPONENT_POINTS = "opponentpoints";
    private static final String CHALLENGE_START_TIME = "starttime";
    private static final String CHALLENGE_END_TIME = "endtime";
    private static final String CHALLENGE_ACCEPTED = "challengeaccepted";
    private static final String CHALLENGE_CHALLENGER = "challengechallenger";
    private static final String CHALLENGE_OVER = "challengeover";
    private static final String CHALLENGE_WON = "challengewon";

    public DatabaseHandlerWear(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Achievements table
        String CREATE_ACHIEVEMENTS_TABLE = "CREATE TABLE " + TABLE_ACHIEVEMENTS + "("
                + ACHIEVEMENT_ID + " INTEGER PRIMARY KEY," + ACHIEVEMENT_TITLE + " TEXT," + ACHIEVEMENT_DESCRIPTION + " TEXT,"
                + ACHIEVEMENT_POINTS + " INTEGER," + ACHIEVEMENT_IMAGE + " INTEGER," + ACHIEVEMENT_OBTAINED + " INTEGER" + ")";
        db.execSQL(CREATE_ACHIEVEMENTS_TABLE);

        // Create Contacts table
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + CONTACT_ID + " INTEGER PRIMARY KEY," + CONTACT_NAME + " TEXT," + CONTACT_SURNAME + " TEXT,"
                + CONTACT_IMAGE + " TEXT," + CONTACT_POINTS + " INTEGER," + CONTACT_DAYPOINTS + " INTEGER," + CONTACT_WEEKPOINTS + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);


        // Create Challenges table
        String CREATE_CHALLENGES_TABLE = "CREATE TABLE " + TABLE_CHALLENGES + "("
                + CHALLENGE_ID + " TEXT," + CHALLENGE_OPPONENTID + " TEXT," + CHALLENGE_POINTS + " INTEGER," +
                CHALLENGE_OPPONENT_POINTS + " INTEGER," + CHALLENGE_START_TIME + " TEXT," + CHALLENGE_END_TIME + " TEXT,"
                + CHALLENGE_ACCEPTED + " TEXT," + CHALLENGE_CHALLENGER + " TEXT,"
                + CHALLENGE_OVER + " TEXT," + CHALLENGE_WON + " TEXT" + ")";
        db.execSQL(CREATE_CHALLENGES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHALLENGES);

        // Create tables again
        onCreate(db);
    }


    // ADD ROW

    public void addAchievement(Achievement achievement) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACHIEVEMENT_ID, achievement.getId());
        values.put(ACHIEVEMENT_TITLE, achievement.getTitle());
        values.put(ACHIEVEMENT_DESCRIPTION, achievement.getDescription());
        values.put(ACHIEVEMENT_POINTS, achievement.getPoints());
        values.put(ACHIEVEMENT_IMAGE, achievement.getImage());
        int boolValue = (achievement.isObtained()) ? 1 : 0;
        values.put(ACHIEVEMENT_OBTAINED, boolValue);

        // Inserting Row
        db.insert(TABLE_ACHIEVEMENTS, null, values);
        db.close();
    }


    public void addContact(User contact) {

        ArrayList<User> contacts = this.getAllContacts();
        boolean alreadyAdded = false;

        for (User user : contacts) {

            if (user.getID().equals(contact.getID())) {
                alreadyAdded = true;
            }
        }
        if (!alreadyAdded) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CONTACT_ID, contact.getID());
            values.put(CONTACT_NAME, contact.getName());
            values.put(CONTACT_SURNAME, contact.getSurname());
            values.put(CONTACT_IMAGE, contact.getProfilePic());
            values.put(CONTACT_POINTS, contact.getPoints());
            values.put(CONTACT_DAYPOINTS, contact.getDayPoints());
            values.put(CONTACT_WEEKPOINTS, contact.getWeekPoints());

            // Inserting Row
            db.insert(TABLE_CONTACTS, null, values);
            db.close();
        }
    }

    public void addChallenge(Challenge challenge) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CHALLENGE_ID, challenge.getID());
        values.put(CHALLENGE_OPPONENTID, challenge.getOpponentID());
        values.put(CHALLENGE_POINTS, challenge.getMyPoints());
        values.put(CHALLENGE_OPPONENT_POINTS, challenge.getOpponentPoints());
        values.put(CHALLENGE_START_TIME, challenge.getStartTime());
        values.put(CHALLENGE_END_TIME, challenge.getEndTime());
        String accepted = (challenge.isAccepted()) ? "true" : "false";
        values.put(CHALLENGE_ACCEPTED, accepted);
        String challenger = (challenge.isChallenger()) ? "true" : "false";
        values.put(CHALLENGE_CHALLENGER, challenger);
        String over = (challenge.isOver()) ? "true" : "false";
        values.put(CHALLENGE_OVER, over);
        String won = (challenge.isWon()) ? "true" : "false";
        values.put(CHALLENGE_WON, won);

        // Inserting Row
        db.insert(TABLE_CHALLENGES, null, values);
        db.close();
    }

    // GET SINGLE ROW

    public Achievement getAchievement(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ACHIEVEMENTS, new String[]{ACHIEVEMENT_ID,
                        ACHIEVEMENT_TITLE, ACHIEVEMENT_DESCRIPTION, ACHIEVEMENT_POINTS, ACHIEVEMENT_IMAGE, ACHIEVEMENT_OBTAINED}, ACHIEVEMENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        Achievement achievement;
        try {
            cursor.moveToFirst();
            achievement = new Achievement(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5) != 0);

        } finally {
            cursor.close();
        }


        return achievement;
    }

    public Challenge getChallenge(String id) {


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CHALLENGES, new String[]{CHALLENGE_ID,
                        CHALLENGE_OPPONENTID, CHALLENGE_POINTS, CHALLENGE_OPPONENT_POINTS, CHALLENGE_START_TIME, CHALLENGE_END_TIME,
                        CHALLENGE_ACCEPTED, CHALLENGE_CHALLENGER, CHALLENGE_OVER, CHALLENGE_WON}, CHALLENGE_ID + "=?",
                new String[]{id}, null, null, null, null);

        Challenge challenge = null;

        try {
            cursor.moveToFirst();
            challenge = new Challenge(cursor.getString(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(3), cursor.getLong(4),
                    cursor.getLong(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));

        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            cursor.close();
        }

        return challenge;

    }

    public Challenge getChallengeByOpponentID(String id) {


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CHALLENGES, new String[]{CHALLENGE_ID,
                        CHALLENGE_OPPONENTID, CHALLENGE_POINTS, CHALLENGE_OPPONENT_POINTS, CHALLENGE_START_TIME, CHALLENGE_END_TIME,
                        CHALLENGE_ACCEPTED, CHALLENGE_CHALLENGER, CHALLENGE_OVER, CHALLENGE_WON}, CHALLENGE_OPPONENTID + "=?",
                new String[]{id}, null, null, null, null);

        Challenge challenge;

        try {
            cursor.moveToFirst();
            challenge = new Challenge(cursor.getString(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(3), cursor.getLong(4),
                    cursor.getLong(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));

        } finally {
            cursor.close();
        }

        return challenge;

    }

    public Challenge getActiveChallengeByOpponentID(String id) {


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CHALLENGES, new String[]{CHALLENGE_ID,
                        CHALLENGE_OPPONENTID, CHALLENGE_POINTS, CHALLENGE_OPPONENT_POINTS, CHALLENGE_START_TIME, CHALLENGE_END_TIME,
                        CHALLENGE_ACCEPTED, CHALLENGE_CHALLENGER, CHALLENGE_OVER, CHALLENGE_WON}, CHALLENGE_OPPONENTID + "=?" +
                        " and " + CHALLENGE_OVER + "=?",
                new String[]{id, "false"}, null, null, null, null);

        Challenge challenge;

        try {
            cursor.moveToFirst();
            challenge = new Challenge(cursor.getString(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(3), cursor.getLong(4),
                    cursor.getLong(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));

        } finally {
            cursor.close();
        }

        return challenge;

    }

    // GET ALL ROWS

    public ArrayList<Achievement> getAllAchievements() {

        ArrayList<Achievement> achievementList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_ACHIEVEMENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Achievement achievement = new Achievement(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5) != 0);
                achievementList.add(achievement);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return achievementList;
    }

    public ArrayList<User> getAllContacts() {

        ArrayList<User> contactList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User contact = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), Long.parseLong(cursor.getString(4)), Long.parseLong(cursor.getString(5)), Long.parseLong(cursor.getString(6)),"","");
                contactList.add(contact);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return contactList;
    }

    public List<Challenge> getAllChallenges() {

        List<Challenge> challengeList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_CHALLENGES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Challenge challenge = new Challenge(cursor.getString(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(3), cursor.getLong(4),
                        cursor.getLong(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
                challengeList.add(challenge);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return challengeList;
    }

    public List<Challenge> getAllWonChallenges() {

        List<Challenge> challengeList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_CHALLENGES + " WHERE " + CHALLENGE_WON + " = 'true' ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Challenge challenge = new Challenge(cursor.getString(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(3), cursor.getLong(4),
                        cursor.getLong(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
                challengeList.add(challenge);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return challengeList;
    }

    public List<Challenge> getActiveChallenges() {

        List<Challenge> challengeList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_CHALLENGES + " WHERE " + CHALLENGE_OVER + " = 'false' ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Challenge challenge = new Challenge(cursor.getString(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(3), cursor.getLong(4),
                        cursor.getLong(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
                challengeList.add(challenge);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return challengeList;
    }



    // GET ROW COUNT

    public int getAchievementsObtCount() {

        String countQuery = "SELECT  * FROM " + TABLE_ACHIEVEMENTS + " WHERE " + ACHIEVEMENT_OBTAINED + " = '1'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;

    }

    // UPDATE ROW

    public int updateChallenge(Challenge challenge) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CHALLENGE_ID, challenge.getID());
        values.put(CHALLENGE_OPPONENTID, challenge.getOpponentID());
        values.put(CHALLENGE_POINTS, challenge.getMyPoints());
        values.put(CHALLENGE_OPPONENT_POINTS, challenge.getOpponentPoints());
        values.put(CHALLENGE_START_TIME, challenge.getStartTime());
        values.put(CHALLENGE_END_TIME, challenge.getEndTime());
        String accepted = (challenge.isAccepted()) ? "true" : "false";
        values.put(CHALLENGE_ACCEPTED, accepted);
        String challenger = (challenge.isChallenger()) ? "true" : "false";
        values.put(CHALLENGE_CHALLENGER, challenger);
        String over = (challenge.isOver()) ? "true" : "false";
        values.put(CHALLENGE_OVER, over);
        String won = (challenge.isWon()) ? "true" : "false";
        values.put(CHALLENGE_WON, won);

        // updating row
        return db.update(TABLE_CHALLENGES, values, CHALLENGE_OPPONENTID + " = ?" + " and " + CHALLENGE_OVER + " =?",
                new String[]{challenge.getOpponentID(), "false"});
    }

    public int updateAchievement(Achievement achievement) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACHIEVEMENT_ID, achievement.getId());
        values.put(ACHIEVEMENT_TITLE, achievement.getTitle());
        values.put(ACHIEVEMENT_DESCRIPTION, achievement.getDescription());
        values.put(ACHIEVEMENT_POINTS, achievement.getPoints());
        values.put(ACHIEVEMENT_IMAGE, achievement.getImage());
        int boolValue = (achievement.isObtained()) ? 1 : 0;
        values.put(ACHIEVEMENT_OBTAINED, boolValue);

        // updating row
        return db.update(TABLE_ACHIEVEMENTS, values, ACHIEVEMENT_ID + " = ?",
                new String[]{String.valueOf(achievement.getId())});
    }

    public int updateContact(User contact) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTACT_ID, contact.getID());
        values.put(CONTACT_NAME, contact.getName());
        values.put(CONTACT_SURNAME, contact.getSurname());
        values.put(CONTACT_IMAGE, contact.getProfilePic());
        values.put(CONTACT_POINTS, contact.getPoints());
        values.put(CONTACT_DAYPOINTS, contact.getDayPoints());
        values.put(CONTACT_WEEKPOINTS, contact.getWeekPoints());

        // updating row
        return db.update(TABLE_CONTACTS, values, CONTACT_ID + " = ?",
                new String[]{String.valueOf(contact.getID())});
    }



    //DELETE ROW

    public void deleteChallenge(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHALLENGES, CHALLENGE_ID + " = ?",
                new String[]{id});
        db.close();
    }

    public void deleteChallengeByOpponentId(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHALLENGES, CHALLENGE_OPPONENTID + " = ?",
                new String[]{id});
        db.close();
    }


}
