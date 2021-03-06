/**
 * Copyright 2014 Jonas Uekoetter
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package de.lazyheroproductions.campuscardreader.Logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreditDatabase extends SQLiteOpenHelper {

    //sqlite stuff
    private static final String DB_NAME = "creditdatabase";
    private static final String ID = "id";
    private static final String LAST_TRANSACTION = "lastTransaction";
    private static final String CREDIT = "credit";
    private static final String DATE = "date";
    private static final String DATE_LONG = "dateLong";
    private static final String ADDITIONAL_INFO = "additionalInfo";
    private static final String DROP_TABLES = "DROP TABLE IF EXISTS";
    private static final String LIMIT = "7";
    public static final int ID_COLUMN_ID = 0;
    public static final int CREDIT_COLUMN_ID = 1;
    public static final int LAST_TRANSACTION_COLUMN_ID = 2;
    public static final int DATE_COLUMN_ID = 3;
    public static final int DATE_LONG_COLUMN_ID = 4;
    public static final int ADDITIONAL_INFO_COLUMN_ID = 5;

    public CreditDatabase(Context appContext){
        super(appContext, DB_NAME+".db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + DB_NAME + " ( " + ID + " INTEGER PRIMARY KEY, " + CREDIT + " DOUBLE, " +
                LAST_TRANSACTION + " DOUBLE, " + DATE + " TEXT, " + DATE_LONG + " INTEGER, " + ADDITIONAL_INFO + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // gets executed if an database update is necessary
    }

    public void resetDatabase(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(DROP_TABLES + " " + DB_NAME);
        onCreate(database);
    }

    public void addEntry(double credit, double lastTransaction, long date, String additionalInfo){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CREDIT, credit);
        values.put(LAST_TRANSACTION, lastTransaction);
        values.put(DATE_LONG, date);
        values.put(DATE, makeDateHumanReadable(date));
        values.put(ADDITIONAL_INFO, additionalInfo);

        database.insert(DB_NAME, null, values);
        database.close();
    }

    public CreditData getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_NAME + " ORDER BY "+DATE_LONG+" DESC LIMIT "+LIMIT, null);
        // this query has no string parameter because it breaks the application.
        // at least if the limit gets paramterized
        CreditData cData = new CreditData((int)DatabaseUtils.queryNumEntries(db, DB_NAME));
        if (cursor.moveToFirst()) {
            do {
                cData.addCredit(cursor.getFloat(CREDIT_COLUMN_ID));
                cData.addTransaction(cursor.getFloat(LAST_TRANSACTION_COLUMN_ID));
                cData.addDate(cursor.getLong(DATE_LONG_COLUMN_ID));
                cData.addDateHumanReadable(cursor.getString(DATE_COLUMN_ID));
                cData.addInfos(cursor.getString(ADDITIONAL_INFO_COLUMN_ID));
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor = db.rawQuery("SELECT TOTAL(" + CREDIT + ") FROM " + DB_NAME, null);
        if(cursor.moveToFirst()){
            cData.setSumCredit(cursor.getFloat(0));
        }
        cursor.close();
        cursor = db.rawQuery("SELECT TOTAL(" + LAST_TRANSACTION + ") FROM " + DB_NAME, null);
        if(cursor.moveToFirst()){
            cData.setSumTransactions(cursor.getFloat(0));
        }
        cursor.close();
        db.close();
        return cData;
    }

    private String makeDateHumanReadable(long date){
        Date d = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy", Locale.GERMAN);
        d.setTime(date);
        return df.format(d);
    }

    public Cursor getListCursor(){

        SQLiteDatabase db = this.getWritableDatabase();
        // we need the select id as _id
        // because the cursoradapter throws an exeption if id isn't called _id
        Cursor tCursor = db.rawQuery(   "SELECT id as _id, credit, lastTransaction, date, dateLong, additionalInfo " +
                                        "FROM " + DB_NAME + " " +
                                        "ORDER BY " + DATE_LONG + " DESC", null);
        // if we close the connectoin we can't use the cursor :(
        //db.close();
        return tCursor;
    }

    public void deleteEntry(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_NAME, "id = ?", new String[]{id});
        db.close();
    }
}
