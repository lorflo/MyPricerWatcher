package cs4330.cs.utep.edu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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
    private static final String COL2_PRICE = "Price";
    private static final String COL3_URL = "URL";


    public MyDBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
        + COL0_ID + "Integer PRIMARY KEY AUTOINCREMENT," + COL1_NAME + "TEXT,"
        + COL2_PRICE + "Double," + COL3_URL + "TEXT" + ")";
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
    public boolean addItemData(String name, Double price, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL1_NAME, name); // Items Name
        values.put(COL2_PRICE, price); // Items Price
        values.put(COL3_URL, url); // Items URL
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

    // Getting one item
    public Item getItem(String itName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] { COL3_URL }, COL3_URL + "=?",
                new String[] { itName }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Item item = new Item(cursor.getString(2));
// return Item
        return item;
    }

    // Getting All Items
    public List<Item> getAllItemData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Item> itemList = new ArrayList<Item>();


// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Item item = new Item();
                item.setPrice(Double.parseDouble(cursor.getString(0)));
                item.setName(cursor.getString(1));
                item.setUrl(cursor.getString(2));
// Adding contact to list
                itemList.add(item);
            } while (cursor.moveToNext());
        }
// return contact list
        return itemList;
    }
}