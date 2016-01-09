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
import it.polimi.stopit.model.AlternativeActivity;
import it.polimi.stopit.model.Challenge;
import it.polimi.stopit.model.Cigarette;
import it.polimi.stopit.model.MoneyTarget;
import it.polimi.stopit.model.User;

public class DatabaseHandler extends SQLiteOpenHelper{

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

    // TABLE CIGARETTE
    private static final String TABLE_CIGARETTES = "cigarettes";

    private static final String CIGARETTE_ID = "id";
    private static final String CIGARETTE_YEAR = "year";
    private static final String CIGARETTE_MONTH = "month";
    private static final String CIGARETTE_DAY = "day";
    private static final String CIGARETTE_HOUR = "hour";
    private static final String CIGARETTE_MINUTES = "minutes";
    private static final String CIGARETTE_TYPE = "type";

    // TABLE MONEY TARGETS
    private static final String TABLE_MONEY_TARGETS = "moneytargets";

    private static final String MONEYTARGET_ID = "id";
    private static final String MONEYTARGET_NAME = "name";
    private static final String MONEYTARGET_AMOUNT = "amount";
    private static final String MONEYTARGET_SAVED = "saved";
    private static final String MONEYTARGET_DURATION = "duration";
    private static final String MONEYTARGET_IMAGE = "image";
    private static final String MONEYTARGET_CIGREDUCED = "reduced";

    // TABLE MONEY CATEGORIES
    private static final String TABLE_MONEY_CATEGORIES = "moneycategories";

    private static final String MONEYCAT_ID = "id";
    private static final String MONEYCAT_NAME = "name";
    private static final String MONEYCAT_IMAGE = "image";

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

    //TABLE ALTERNATIVEACTIVITIES
    private static final String TABLE_ALTERNATIVE_ACTIVITIES = "alternativeactivities";

    private static final String ALTERNATIVE_ID = "id";
    private static final String ALTERNATIVE_TITLE = "title";
    private static final String ALTERNATIVE_DESCRIPTION = "description";
    private static final String ALTERNATIVE_CATEGORY = "category";
    private static final String ALTERNATIVE_BONUSPOINTS = "bonuspoints";
    private static final String ALTERNATIVE_FREQUENCY = "frequency";
    private static final String ALTERNATIVE_IMAGE = "alternative";

    public DatabaseHandler(Context context) {
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

        // Create Cigarettes table
        String CREATE_CIGARETTES_TABLE = "CREATE TABLE " + TABLE_CIGARETTES + "("
                + CIGARETTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CIGARETTE_YEAR + " INTEGER," + CIGARETTE_MONTH + " INTEGER," + CIGARETTE_DAY + " INTEGER," + CIGARETTE_HOUR + " INTEGER," + CIGARETTE_MINUTES + " INTEGER," + CIGARETTE_TYPE + " TEXT"
                + ")";
        System.out.println(CREATE_CIGARETTES_TABLE);
        db.execSQL(CREATE_CIGARETTES_TABLE);

        // Create Money Targets table
        String CREATE_MONEY_TABLE = "CREATE TABLE " + TABLE_MONEY_TARGETS + "("
                + MONEYTARGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + MONEYTARGET_NAME + " TEXT," + MONEYTARGET_AMOUNT + " INTEGER,"
                + MONEYTARGET_SAVED + " INTEGER," + MONEYTARGET_DURATION + " INTEGER,"+ MONEYTARGET_IMAGE + " INTEGER," + MONEYTARGET_CIGREDUCED + " INTEGER"+ ")";
        db.execSQL(CREATE_MONEY_TABLE);

        // Create Money Targets categories table
        String CREATE_MONEYCAT_TABLE = "CREATE TABLE " + TABLE_MONEY_CATEGORIES + "("
                + MONEYCAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + MONEYCAT_NAME + " TEXT," + MONEYCAT_IMAGE + " INTEGER" + ")";
        db.execSQL(CREATE_MONEYCAT_TABLE);

        // Create Challenges table
        String CREATE_CHALLENGES_TABLE = "CREATE TABLE " + TABLE_CHALLENGES + "("
                + CHALLENGE_ID + " TEXT," + CHALLENGE_OPPONENTID + " TEXT," + CHALLENGE_POINTS + " INTEGER,"+
                CHALLENGE_OPPONENT_POINTS + " INTEGER,"+ CHALLENGE_START_TIME + " TEXT," + CHALLENGE_END_TIME + " TEXT,"
                + CHALLENGE_ACCEPTED + " TEXT,"+ CHALLENGE_CHALLENGER + " TEXT" + ")";
        db.execSQL(CREATE_CHALLENGES_TABLE);

        // Create Challenges table
        String CREATE_ALTERNATIVE_TABLE = "CREATE TABLE " + TABLE_ALTERNATIVE_ACTIVITIES + "("
                + ALTERNATIVE_ID + " INTEGER PRIMARY KEY," + ALTERNATIVE_TITLE + " TEXT," + ALTERNATIVE_DESCRIPTION + " TEXT,"+
                ALTERNATIVE_CATEGORY + " TEXT,"+ ALTERNATIVE_BONUSPOINTS + " INTEGER," + ALTERNATIVE_FREQUENCY + " INTEGER,"
                + ALTERNATIVE_IMAGE + " INTEGER" + ")";
        db.execSQL(CREATE_ALTERNATIVE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CIGARETTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONEY_TARGETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHALLENGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALTERNATIVE_ACTIVITIES);

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
        int boolValue= (achievement.isObtained()) ? 1 : 0;
        values.put(ACHIEVEMENT_OBTAINED, boolValue);

        // Inserting Row
        db.insert(TABLE_ACHIEVEMENTS, null, values);
        db.close();
    }

    public void addMoneyTarget(MoneyTarget target) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MONEYTARGET_NAME, target.getName());
        values.put(MONEYTARGET_AMOUNT, target.getMoneyAmount());
        values.put(MONEYTARGET_SAVED, target.getMoneySaved());
        values.put(MONEYTARGET_DURATION, target.getDuration());
        values.put(MONEYTARGET_IMAGE, target.getImageResource());
        values.put(MONEYTARGET_CIGREDUCED, target.getCigReduced());

        // Inserting Row
        db.insert(TABLE_MONEY_TARGETS, null, values);
        db.close();
    }

    public void addMoneyCategory(String name, int imageResource) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MONEYCAT_NAME, name);
        values.put(MONEYCAT_IMAGE, imageResource);

        // Inserting Row
        db.insert(TABLE_MONEY_CATEGORIES, null, values);
        db.close();
    }

    public void addCigarette(Cigarette cigarette) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CIGARETTE_YEAR, cigarette.getDate().getYear());
        values.put(CIGARETTE_MONTH, cigarette.getDate().getMonthOfYear());
        values.put(CIGARETTE_DAY, cigarette.getDate().getDayOfMonth());
        values.put(CIGARETTE_HOUR, cigarette.getDate().getHourOfDay());
        values.put(CIGARETTE_MINUTES, cigarette.getDate().getMinuteOfHour());
        values.put(CIGARETTE_TYPE, cigarette.getType());

        // Inserting Row

        System.out.println("New Cigarette Inserted : "+ values.toString());
        db.insert(TABLE_CIGARETTES, null, values);
        db.close();
    }

    public void addContact(User contact) {

        ArrayList<User> contacts=this.getAllContacts();
        boolean alreadyAdded=false;

        for(User user:contacts){

            if(user.getID().equals(contact.getID())){
                alreadyAdded=true;
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
        String accepted= (challenge.isAccepted()) ? "true" : "false";
        values.put(CHALLENGE_ACCEPTED, accepted);
        String challenger= (challenge.isChallenger()) ? "true" : "false";
        values.put(CHALLENGE_CHALLENGER, challenger);

        // Inserting Row
        db.insert(TABLE_CHALLENGES, null, values);
        db.close();
    }

    public void addAlternative(AlternativeActivity alternativeActivity) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ALTERNATIVE_ID, alternativeActivity.getID());
        values.put(ALTERNATIVE_TITLE, alternativeActivity.getTitle());
        values.put(ALTERNATIVE_DESCRIPTION, alternativeActivity.getDescription());
        values.put(ALTERNATIVE_CATEGORY, alternativeActivity.getCategory());
        values.put(ALTERNATIVE_FREQUENCY, alternativeActivity.getFrequency());
        values.put(ALTERNATIVE_BONUSPOINTS, alternativeActivity.getBonusPoints());
        values.put(ALTERNATIVE_IMAGE, alternativeActivity.getImage());


        // Inserting Row
        db.insert(TABLE_ALTERNATIVE_ACTIVITIES, null, values);
        db.close();
    }

    // GET SINGLE ROW

    public Cigarette getCigarette(int id){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CIGARETTES, new String[] { CIGARETTE_ID,
                        CIGARETTE_YEAR,CIGARETTE_MONTH,CIGARETTE_DAY,CIGARETTE_HOUR, CIGARETTE_MINUTES,CIGARETTE_TYPE }, CIGARETTE_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DateTime date= new DateTime(cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5));

        return new Cigarette(cursor.getInt(0),date,cursor.getString(6));

    }

    public Achievement getAchievement(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ACHIEVEMENTS, new String[] { ACHIEVEMENT_ID,
                        ACHIEVEMENT_TITLE, ACHIEVEMENT_DESCRIPTION, ACHIEVEMENT_POINTS,ACHIEVEMENT_IMAGE,ACHIEVEMENT_OBTAINED }, ACHIEVEMENT_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new Achievement(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5)!=0);
    }

    public User getContact(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{CONTACT_ID,
                        CONTACT_NAME, CONTACT_SURNAME, CONTACT_IMAGE, CONTACT_POINTS,CONTACT_DAYPOINTS,CONTACT_WEEKPOINTS}, CONTACT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),Long.parseLong(cursor.getString(4)),Long.parseLong(cursor.getString(5)),Long.parseLong(cursor.getString(6)));
    }

    public MoneyTarget getMoneyTarget(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MONEY_TARGETS, new String[]{MONEYTARGET_ID,
                        MONEYTARGET_NAME, MONEYTARGET_AMOUNT, MONEYTARGET_SAVED, MONEYTARGET_DURATION, MONEYTARGET_IMAGE,MONEYTARGET_CIGREDUCED}, MONEYTARGET_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();


        return new MoneyTarget(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6));
    }

    public Challenge getChallenge(String id){


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CHALLENGES, new String[] { CHALLENGE_ID,
                        CHALLENGE_OPPONENTID, CHALLENGE_POINTS,CHALLENGE_OPPONENT_POINTS,CHALLENGE_START_TIME,CHALLENGE_END_TIME,
                CHALLENGE_ACCEPTED,CHALLENGE_CHALLENGER}, CHALLENGE_ID + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Challenge challenge = new Challenge(cursor.getString(0),cursor.getString(1),cursor.getLong(2),cursor.getLong(3),cursor.getLong(4),
                cursor.getLong(5),cursor.getString(6),cursor.getString(7));

        return challenge;

    }

    public AlternativeActivity getAlternative(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALTERNATIVE_ACTIVITIES, new String[] { ALTERNATIVE_ID,
                        ALTERNATIVE_TITLE, ALTERNATIVE_DESCRIPTION, ALTERNATIVE_CATEGORY,ALTERNATIVE_BONUSPOINTS,ALTERNATIVE_FREQUENCY,ALTERNATIVE_IMAGE }, ALTERNATIVE_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new AlternativeActivity(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),
                cursor.getInt(4),cursor.getInt(5),cursor.getInt(6));
    }

    public Challenge getChallengeByOpponentID(String id){


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CHALLENGES, new String[] { CHALLENGE_ID,
                        CHALLENGE_OPPONENTID, CHALLENGE_POINTS,CHALLENGE_OPPONENT_POINTS,CHALLENGE_START_TIME,CHALLENGE_END_TIME,
                        CHALLENGE_ACCEPTED,CHALLENGE_CHALLENGER}, CHALLENGE_OPPONENTID + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Challenge challenge = new Challenge(cursor.getString(0),cursor.getString(1),cursor.getLong(2),cursor.getLong(3),cursor.getLong(4),
                cursor.getLong(5),cursor.getString(6),cursor.getString(7));

        return challenge;

    }

    // GET ALL ROWS

    public List<Achievement> getAllAchievements() {

        List<Achievement> achievementList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_ACHIEVEMENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Achievement achievement = new Achievement(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5)!=0);
                achievementList.add(achievement);

            } while (cursor.moveToNext());
        }

        return achievementList;
    }

    public ArrayList<User> getAllContacts() {

        ArrayList<User> contactList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User contact = new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),Long.parseLong(cursor.getString(4)),Long.parseLong(cursor.getString(5)),Long.parseLong(cursor.getString(6)));
                contactList.add(contact);

            } while (cursor.moveToNext());
        }

        return contactList;
    }

    public List<Challenge> getAllChallenges() {

        List<Challenge> challengeList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_CHALLENGES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Challenge challenge = new Challenge(cursor.getString(0),cursor.getString(1),cursor.getLong(2),cursor.getLong(3),cursor.getLong(4),
                        cursor.getLong(5),cursor.getString(6),cursor.getString(7));
                challengeList.add(challenge);

            } while (cursor.moveToNext());
        }

        return challengeList;
    }

    public List<AlternativeActivity> getAllAlternative() {

        List<AlternativeActivity> alternativeActivityList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_ALTERNATIVE_ACTIVITIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AlternativeActivity alternativeActivity = new AlternativeActivity(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6));
                alternativeActivityList.add(alternativeActivity);

            } while (cursor.moveToNext());
        }

        return alternativeActivityList;
    }

    public ArrayList<MoneyTarget> getAllTargets() {

        ArrayList<MoneyTarget> targetList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_MONEY_TARGETS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MoneyTarget target = new MoneyTarget(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6));
                targetList.add(target);

            } while (cursor.moveToNext());
        }

        return targetList;
    }

    public boolean targetAlreadyInProgress() {

        String selectQuery = "SELECT  * FROM " + TABLE_MONEY_TARGETS + " WHERE NOT "+MONEYTARGET_AMOUNT + " = " + MONEYTARGET_SAVED + "";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor.moveToFirst();
    }

    public ArrayList<Cigarette> getDailyCigarettes(int year,int month,int day) {

        ArrayList<Cigarette> cigList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_CIGARETTES + " WHERE "+CIGARETTE_YEAR+"="+year+" AND "+CIGARETTE_MONTH+"="+month+" AND "+CIGARETTE_DAY+"="+day+" ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                DateTime date= new DateTime(cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5));
                cigList.add(new Cigarette(cursor.getInt(0),date,cursor.getString(6)));

            } while (cursor.moveToNext());
        }

        return cigList;
    }

    public int getCigarettesAvoided() {

        String selectQuery = "SELECT  * FROM " + TABLE_CIGARETTES + " WHERE "+CIGARETTE_TYPE+"='notsmoke'";
        int count=0;
        long days=0;
        boolean first=true;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                count++;

            } while (cursor.moveToNext());
        }

        return count;
    }

    // GET ALL CATEGORIES
    public ArrayList<MoneyTarget> getAllCategories() {

        ArrayList<MoneyTarget> targetList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_MONEY_CATEGORIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MoneyTarget target = new MoneyTarget(cursor.getInt(0),cursor.getString(1),100,100,100,cursor.getInt(2),0);
                targetList.add(target);

            } while (cursor.moveToNext());
        }

        return targetList;
    }

    // GET ROW COUNT

    public int getAchievementsCount() {

        String countQuery = "SELECT  * FROM " + TABLE_ACHIEVEMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();

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
        String accepted= (challenge.isAccepted()) ? "true" : "false";
        values.put(CHALLENGE_ACCEPTED, accepted);
        String challenger= (challenge.isChallenger()) ? "true" : "false";
        values.put(CHALLENGE_CHALLENGER, challenger);

        // updating row
        return db.update(TABLE_CHALLENGES, values, CHALLENGE_OPPONENTID + " = ?",
                new String[] { challenge.getOpponentID() });
    }

    public int updateAchievement(Achievement achievement) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACHIEVEMENT_ID, achievement.getId());
        values.put(ACHIEVEMENT_TITLE, achievement.getTitle());
        values.put(ACHIEVEMENT_DESCRIPTION, achievement.getDescription());
        values.put(ACHIEVEMENT_POINTS, achievement.getPoints());
        values.put(ACHIEVEMENT_IMAGE, achievement.getImage());
        int boolValue= (achievement.isObtained()) ? 1 : 0;
        values.put(ACHIEVEMENT_OBTAINED, boolValue);

        // updating row
        return db.update(TABLE_ACHIEVEMENTS, values, ACHIEVEMENT_ID + " = ?",
                new String[] { String.valueOf(achievement.getId()) });
    }

    public int updateMoneyTarget(MoneyTarget target) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();;
        values.put(MONEYTARGET_NAME, target.getName());
        values.put(MONEYTARGET_AMOUNT, target.getMoneyAmount());
        values.put(MONEYTARGET_SAVED, target.getMoneySaved());
        values.put(MONEYTARGET_DURATION, target.getDuration());
        values.put(MONEYTARGET_IMAGE, target.getImageResource());
        values.put(MONEYTARGET_CIGREDUCED, target.getCigReduced());

        // updating row
        return db.update(TABLE_MONEY_TARGETS, values, MONEYTARGET_ID + " = ?",
                new String[] { String.valueOf(target.getId()) });
    }

    //DELETE ROW

    public void deleteAlternative(AlternativeActivity alternativeActivity) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALTERNATIVE_ACTIVITIES, ALTERNATIVE_ID + " = ?",
                new String[]{String.valueOf(alternativeActivity.getID())});
        db.close();
    }

    public void deleteChallenge(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHALLENGES, CHALLENGE_ID + " = ?",
                new String[]{ id });
        db.close();
    }

    public void deleteAchievement(Achievement achievement) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACHIEVEMENTS, ACHIEVEMENT_ID + " = ?",
                new String[]{String.valueOf(achievement.getId())});
        db.close();
    }

    public void deleteMoneyTarget(MoneyTarget target) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MONEY_TARGETS, MONEYTARGET_ID + " = ?",
                new String[]{String.valueOf(target.getId())});
        db.close();
    }

    public void deleteAllContacts() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + CONTACT_ID + " INTEGER PRIMARY KEY," + CONTACT_NAME + " TEXT," + CONTACT_SURNAME + " TEXT,"
                + CONTACT_IMAGE + " TEXT," + CONTACT_POINTS + " INTEGER," + CONTACT_DAYPOINTS + " INTEGER," + CONTACT_WEEKPOINTS + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.close();
    }

}
