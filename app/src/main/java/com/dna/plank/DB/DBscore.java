package com.dna.plank.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dna.plank.Modeles.Historique;

import java.util.LinkedList;
import java.util.List;

public class DBscore   extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Correnth.db";
    private static final int DATABASE_VERSION = 3 ;
    public static final String TABLE_NAME = "historique";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_nom_exr = "nom_ex";
    public static final String COLUMN_temps_ecoulee = "temps_ec";
    public static final String COLUMN_temps_exercise = "temps_ex";
    public static final String COLUMN_pourc_reussit = "ratio_reussite";

    public static final String TABLE_NOM= "Admin";
    public static final String COLUMN_ID_Admin = "id";
    public static final String COLUMN_ADMIN_NOM="nom";
    public static final String COLUMN_ADMIN_OCCUPATION= "occupation_admin";
    public static final String COLUMN_ADMIN_AGE = "age_admin";
    public static final String COLUMN_ADMIN_mdp = "mdp";

    public DBscore(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(" CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_nom_exr  + " TEXT NOT NULL, " +
                COLUMN_temps_ecoulee + " TEXT NOT NULL, " +
                COLUMN_temps_exercise + " TEXT NOT NULL, " +
                COLUMN_pourc_reussit + " TEXT NOT NULL);"
        );
        db.execSQL("INSERT INTO "+TABLE_NAME + " ( " + COLUMN_nom_exr + " , " +
                COLUMN_temps_ecoulee + " , " + COLUMN_temps_exercise+ " , " +COLUMN_pourc_reussit+ "  ) VALUES ('Planche','02.13','06.32','13');");
        db.execSQL("INSERT INTO "+TABLE_NAME + " ( " + COLUMN_nom_exr + " , " +
                COLUMN_temps_ecoulee + " , " + COLUMN_temps_exercise+ " , " +COLUMN_pourc_reussit+ "  ) VALUES ('Planche','02.13','06.32','13');");
        db.execSQL("INSERT INTO "+TABLE_NAME + " ( " + COLUMN_nom_exr + " , " +
                COLUMN_temps_ecoulee + " , " + COLUMN_temps_exercise+ " , " +COLUMN_pourc_reussit+ "  ) VALUES ('Planche','02.13','06.32','13');");






    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);

    }


    public List<Historique> peopleList(String filter) {
        String query;
        if(filter.equals("")){
            //regular query
            query = "SELECT  * FROM " + TABLE_NAME;
        }else{
            //filter results by filter option provided
            query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY "+ filter;
        }

        List<Historique> personLinkedList = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Historique person;

        if (cursor.moveToFirst()) {
            do {
                person = new Historique();

                person.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                person.setNomEx(cursor.getString(cursor.getColumnIndex(COLUMN_nom_exr)));
                person.setDureeEx(cursor.getString(cursor.getColumnIndex(COLUMN_temps_exercise)));
                person.setDureepose(cursor.getString(cursor.getColumnIndex(COLUMN_temps_ecoulee)));
                person.setRatio(cursor.getString(cursor.getColumnIndex(COLUMN_pourc_reussit)));

                personLinkedList.add(person);
            } while (cursor.moveToNext());
        }


        return personLinkedList;
    }


    public void saveNewPerson(Historique histo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_nom_exr, histo.getNomEx());
        values.put(COLUMN_temps_exercise, histo.getDureeEx());
        values.put(COLUMN_temps_ecoulee, histo.getDureepose());
        values.put(COLUMN_pourc_reussit, histo.getRatio());

        // insertion
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

}
