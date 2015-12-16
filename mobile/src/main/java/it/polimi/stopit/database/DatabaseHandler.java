package it.polimi.stopit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.facebook.Profile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.stopit.model.Achievement;
import it.polimi.stopit.model.Cigarette;
import it.polimi.stopit.model.User;

public class DatabaseHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = ""+ Profile.getCurrentProfile().getId()+"_DB";

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

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Achievements table
        String CREATE_ACHIEVEMENTS_TABLE = "CREATE TABLE " + TABLE_ACHIEVEMENTS + "("
                + ACHIEVEMENT_ID + " INTEGER PRIMARY KEY," + ACHIEVEMENT_TITLE + " TEXT," + ACHIEVEMENT_DESCRIPTION + " TEXT,"
                + ACHIEVEMENT_POINTS + " TEXT," + ACHIEVEMENT_IMAGE + " TEXT," + ACHIEVEMENT_OBTAINED + " INTEGER" + ")";
        db.execSQL(CREATE_ACHIEVEMENTS_TABLE);

        // Create Achievements table
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + CONTACT_ID + " INTEGER PRIMARY KEY," + CONTACT_NAME + " TEXT," + CONTACT_SURNAME + " TEXT,"
                + CONTACT_IMAGE + " TEXT," + CONTACT_POINTS + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        // Create Cigarettes table
        String CREATE_CIGARETTES_TABLE = "CREATE TABLE " + TABLE_CIGARETTES + "("
                + CIGARETTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CIGARETTE_DATE + " TEXT," + CIGARETTE_TYPE + " TEXT"
                + ")";
        db.execSQL(CREATE_CIGARETTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CIGARETTES);

        // Create tables again
        onCreate(db);
    }

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

    public void addCigarette(Cigarette cigarette) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CIGARETTE_DATE, cigarette.getDate().toString());
        values.put(CIGARETTE_TYPE, cigarette.getType());

        System.out.println("NEW CIGARETTE INSERTED: date = "+cigarette.getDate().toString()+" type = "+cigarette.getType());
        // Inserting Row
        db.insert(TABLE_CIGARETTES, null, values);
        db.close();
    }

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

    public Achievement getAchievement(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ACHIEVEMENTS, new String[] { ACHIEVEMENT_ID,
                        ACHIEVEMENT_TITLE, ACHIEVEMENT_DESCRIPTION, ACHIEVEMENT_POINTS,ACHIEVEMENT_IMAGE,ACHIEVEMENT_OBTAINED }, ACHIEVEMENT_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Achievement achievement = new Achievement(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2),Long.parseLong(cursor.getString(3)),Integer.parseInt(cursor.getString(4)),cursor.getInt(5)!=0);

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

    public List<Achievement> getAllAchievements() {

        List<Achievement> achievementList = new ArrayList<Achievement>();

        String selectQuery = "SELECT  * FROM " + TABLE_ACHIEVEMENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Achievement achievement = new Achievement(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2),Long.parseLong(cursor.getString(3)),Integer.parseInt(cursor.getString(4)),cursor.getInt(5)!=0);
                achievementList.add(achievement);

            } while (cursor.moveToNext());
        }

        return achievementList;
    }

    public ArrayList<User> getAllContacts() {

        ArrayList<User> contactList = new ArrayList<User>();

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

    public int getAchievementsCount() {

        String countQuery = "SELECT  * FROM " + TABLE_ACHIEVEMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();

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

    public void deleteAchievement(Achievement achievement) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACHIEVEMENTS, ACHIEVEMENT_ID + " = ?",
                new String[]{String.valueOf(achievement.getId())});
        db.close();
    }

}
