package it.polimi.stopit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.stopit.model.Achievement;
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

    // TABLE CIGARETTE
    private static final String TABLE_CIGARETTES = "cigarettes";

    private static final String CIGARETTE_ID = "id";
    private static final String CIGARETTE_DATE = "date";
    private static final String CIGARETTE_TYPE = "type";

    // TABLE MONEY TARGETS
    private static final String TABLE_MONEY_TARGETS = "moneytargets";

    private static final String MONEYTARGET_ID = "id";
    private static final String MONEYTARGET_NAME = "name";
    private static final String MONEYTARGET_AMOUNT = "amount";
    private static final String MONEYTARGET_SAVED = "saved";
    private static final String MONEYTARGET_DURATION = "duration";
    private static final String MONEYTARGET_IMAGE = "image";

    // TABLE MONEY CATEGORIES
    private static final String TABLE_MONEY_CATEGORIES = "moneycategories";

    private static final String MONEYCAT_ID = "id";
    private static final String MONEYCAT_NAME = "name";
    private static final String MONEYCAT_IMAGE = "image";

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
                + CONTACT_IMAGE + " TEXT," + CONTACT_POINTS + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        // Create Cigarettes table
        String CREATE_CIGARETTES_TABLE = "CREATE TABLE " + TABLE_CIGARETTES + "("
                + CIGARETTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CIGARETTE_DATE + " TEXT," + CIGARETTE_TYPE + " TEXT"
                + ")";
        db.execSQL(CREATE_CIGARETTES_TABLE);

        // Create Money Targets table
        String CREATE_MONEY_TABLE = "CREATE TABLE " + TABLE_MONEY_TARGETS + "("
                + MONEYTARGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + MONEYTARGET_NAME + " TEXT," + MONEYTARGET_AMOUNT + " INTEGER,"
                + MONEYTARGET_SAVED + " INTEGER," + MONEYTARGET_DURATION + " INTEGER,"+ MONEYTARGET_IMAGE + " INTEGER" + ")";
        db.execSQL(CREATE_MONEY_TABLE);

        // Create Money Targets categories table
        String CREATE_MONEYCAT_TABLE = "CREATE TABLE " + TABLE_MONEY_CATEGORIES + "("
                + MONEYCAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + MONEYCAT_NAME + " TEXT," + MONEYCAT_IMAGE + " INTEGER" + ")";
        db.execSQL(CREATE_MONEYCAT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CIGARETTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONEY_TARGETS);

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
        values.put(CIGARETTE_DATE, cigarette.getDate().toString());
        values.put(CIGARETTE_TYPE, cigarette.getType());

        // Inserting Row
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

            // Inserting Row
            db.insert(TABLE_CONTACTS, null, values);
            db.close();
        }
    }

    // GET SINGLE ROW

    public Cigarette getCigarette(int id){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CIGARETTES, new String[] { CIGARETTE_ID,
                        CIGARETTE_DATE, CIGARETTE_TYPE }, CIGARETTE_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(cursor.getString(1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Cigarette cigarette = new Cigarette(Integer.parseInt(cursor.getString(0)),date,cursor.getString(2));

        return cigarette;

    }

    public Achievement getAchievement(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ACHIEVEMENTS, new String[] { ACHIEVEMENT_ID,
                        ACHIEVEMENT_TITLE, ACHIEVEMENT_DESCRIPTION, ACHIEVEMENT_POINTS,ACHIEVEMENT_IMAGE,ACHIEVEMENT_OBTAINED }, ACHIEVEMENT_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Achievement achievement = new Achievement(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5)!=0);

        return achievement;
    }

    public User getContact(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{CONTACT_ID,
                        CONTACT_NAME, CONTACT_SURNAME, CONTACT_IMAGE, CONTACT_POINTS}, CONTACT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User contact = new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),Long.parseLong(cursor.getString(4)));

        return contact;
    }

    public MoneyTarget getMoneyTarget(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MONEY_TARGETS, new String[]{MONEYTARGET_ID,
                        MONEYTARGET_NAME, MONEYTARGET_AMOUNT, MONEYTARGET_SAVED, MONEYTARGET_DURATION, MONEYTARGET_IMAGE}, MONEYTARGET_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MoneyTarget moneyTarget = new MoneyTarget(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5));

        return moneyTarget;
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
                User contact = new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),Long.parseLong(cursor.getString(4)));
                contactList.add(contact);

            } while (cursor.moveToNext());
        }

        return contactList;
    }

    // GET ALL TARGETS EXCEPT DEFAULT
    public ArrayList<MoneyTarget> getAllTargets() {

        ArrayList<MoneyTarget> targetList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_MONEY_TARGETS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MoneyTarget target = new MoneyTarget(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5));
                targetList.add(target);

            } while (cursor.moveToNext());
        }

        return targetList;
    }

    // GET ALL TARGETS EXCEPT DEFAULT
    public ArrayList<MoneyTarget> getAllCategories() {

        ArrayList<MoneyTarget> targetList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_MONEY_CATEGORIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MoneyTarget target = new MoneyTarget(cursor.getInt(0),cursor.getString(1),100,100,100,cursor.getInt(2));
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

    //DELETE ROW

    public void deleteAchievement(Achievement achievement) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACHIEVEMENTS, ACHIEVEMENT_ID + " = ?",
                new String[]{String.valueOf(achievement.getId())});
        db.close();
    }

}
