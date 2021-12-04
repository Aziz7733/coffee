package com.example.coffee;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.time.LocalDateTime;

public class MySQLiteHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "DataShop";
    //TABLES NAME
    private static final String TABLE_FAVORITE="favorites";
    private static final String TABLE_COUPON="coupons";

    //context and database variables
    Context ctx;
    SQLiteDatabase myDb;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create favorite table
        String CREATE_Favorite_TABLE = "CREATE TABLE "+TABLE_FAVORITE +"  ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "coffee  TEXT, "+
                "lati  TEXT ,"+
                "longti  Text )";
        // table Coupons
        String Create_Coupons_TABLE="CREATE TABLE "+TABLE_COUPON +"  ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "coffee  TEXT, "+
                "take_date  TEXT ,"+
                "end_date  TEXT )";

        // create Favorite table
        db.execSQL(CREATE_Favorite_TABLE);
       // create Coupons table
        db.execSQL(Create_Coupons_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older Favorite and coupons table if existed
        db.execSQL("DROP TABLE IF EXISTS Favorite");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_COUPON);
        // create fresh Favorite table
        this.onCreate(db);
    }

    public void insertCoupons(String coffee, String take,String end) {
        myDb=getWritableDatabase();
        myDb.execSQL("insert into "+TABLE_COUPON+"(coffee,take_date,end_date) values('"+coffee+"','"+take+"','"+end+"');");
        Toast.makeText(ctx,"Coupon received successfully!",Toast.LENGTH_SHORT).show();

    }

    ///
    public boolean insertFavorite(String coffee, String lat,String lon){

        myDb=getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("coffee", coffee);
        value.put("lati", lat);
        value.put("longti", lon);
        long result =myDb.insert(TABLE_FAVORITE, null, value);
        if (result == -1)
            return false;
        else
            return true;
    }


    //delete Favorite
    public void deleteFavorite( int id) {
        myDb=getWritableDatabase();
        myDb.execSQL("DELETE FROM "+TABLE_FAVORITE+" WHERE id=" + id);
        Toast.makeText(ctx,"Delete successfully!",Toast.LENGTH_SHORT).show();
    }


    //get all data
    public Cursor fetchAllData(String tableName) {
        myDb=getReadableDatabase();
        String query = "SELECT * FROM '" + tableName + "'";
        Cursor row = myDb.rawQuery(query, null);
        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }
    public boolean checkFavoriteExit(String coffeeName){
        myDb=getReadableDatabase();
        Cursor cr =  myDb.rawQuery("SELECT * FROM favorites WHERE coffee = '" + coffeeName + "' ", null);
        if(cr.getCount() > 0)
            return true;
        else
            return false;
    }
}

