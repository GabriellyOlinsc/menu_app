package com.example.cardario_m2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cardario_m2.models.Dish;

import java.util.ArrayList;
import java.util.List;

public class DBConnetion extends SQLiteOpenHelper {
    private static final String dbname="db_restaurant";
    private static final int version = 1;

    public DBConnetion(Context context) {
        super(context, dbname, null, version);
    }

    public static SQLiteDatabase getDBInstance(Activity activity){
        DBConnetion  db = new DBConnetion(activity.getBaseContext());
        return db.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table menu_items(_id integer primary key autoincrement," +
                "name varchar(50),price double, image_url varchar(100) ) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists menu_items");
        onCreate(db);
    }

    public void insertDish(Dish dish){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", dish.getName());
        values.put("price", dish.getPrice());
        values.put("image_url", dish.getImage());
        db.insert("menu_items", null, values);
        db.close();
    }

    public List<Dish> getAllMenuItems(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<Dish> menu = new ArrayList<>();
        Cursor cursor = db.query("menu_items", new String[]{"name", "price","image_url"}, null, null, null, null, null);
        while(cursor.moveToNext()){
            Dish dish = new Dish();
            dish.setName(cursor.getString(0));
            dish.setPrice(cursor.getDouble(1));
            dish.setImage(cursor.getString(2));
            menu.add(dish);
        }
        cursor.close();
        db.close();
        return menu;
    }
    public void clearMenuItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from menu_items");
        db.close();
    }

}
