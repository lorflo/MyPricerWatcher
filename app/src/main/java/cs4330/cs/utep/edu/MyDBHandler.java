package cs4330.cs.utep.edu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper
{

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "itemsInfo";
    // Table name
    private static final String TABLE_NAME = "itemsTable";
    // Items Table Columns names
    private static final String COL0_ID = "Id";
    private static final String COL1_NAME = "Name";
    private static final String COL2_IPRICE = "InitialPrice";
    private static final String COL3_NPRICE = "NewPrice";
    private static final String COL4_PERCENT = "PercentDiff";
    private static final String COL5_URL = "URL";


    public MyDBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COL0_ID + " Integer PRIMARY KEY,"
                + COL1_NAME + " TEXT,"
                + COL2_IPRICE + " Double,"
                + COL3_NPRICE + " DOUBLE,"
                + COL4_PERCENT + " DOUBLE,"
                + COL5_URL + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
// Creating tables again
        onCreate(db);
    }

    // Adding new item into table
    public boolean addItemData(int id,String name, Double iPrice,Double nPrice,Double percent, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL0_ID, id);//Items Id
        values.put(COL1_NAME, name); // Items Name
        values.put(COL2_IPRICE, iPrice); // Items initial Price
        values.put(COL3_NPRICE, nPrice); // Items  new Price
        values.put(COL4_PERCENT, percent); // Items Percent change
        values.put(COL5_URL, url); // Items URL
// Inserting Row
        long result = db.insert(TABLE_NAME, null, values);
        if(result == -1)
        {
            db.close(); // Closing database connection
            return false;
        }
        else
        {
            db.close(); // Closing database connection
            return true;
        }

    }

     //Getting All Items
    public Cursor getAllItemData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public boolean updateData(int id, String name, Double iPrice,Double nPrice,Double percent, String url)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL0_ID, id); // Items id
        values.put(COL1_NAME, name); // Items Name
        values.put(COL2_IPRICE, iPrice); // Items initial Price
        values.put(COL3_NPRICE, nPrice); // Items  new Price
        values.put(COL4_PERCENT, percent); // Items Percent change
        values.put(COL5_URL, url); // Items URL

        db.update(TABLE_NAME,values,COL0_ID +" = ?",
                new String[] {String.valueOf(id)});
        db.close(); // Closing database connection
        return true;

    }
    public Integer deleteData(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

       return db.delete(TABLE_NAME,COL0_ID +" = ?",new String[] {String.valueOf(id)});
    }

}
