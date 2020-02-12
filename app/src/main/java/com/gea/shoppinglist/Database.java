package com.gea.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "db1";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
        //setForcedUpgrade();
    }


    public ArrayList<ProductModel> getList(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"Id", "Name", "Amount", "Checked"};
        String sqlTable = "Product";
        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect,null,null,null,null,"Name DESC");

        final ArrayList<ProductModel> result = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                result.add(new ProductModel(
                        c.getString(c.getColumnIndex("Id")),
                        c.getString(c.getColumnIndex("Name")),
                        c.getString(c.getColumnIndex("Amount")),
                        c.getString(c.getColumnIndex("Checked"))
                ));
            }while (c.moveToNext());
        }
        return result;
    }

    public void  addToList(ProductModel productModel){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Product(Id, Name, Amount, Checked) VALUES ('%s','%s','%s','%s');",
                productModel.getId(),
                productModel.getName(),
                productModel.getAmount(),
                productModel.getChecked());

        db.execSQL(query);
        db.close();
    }

    public void cleanList(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "DELETE FROM Product";
        db.execSQL(query);
        db.close();
    }
    public void deleteListItem(String _id){
        SQLiteDatabase db = getReadableDatabase();
        String query = "DELETE FROM Product WHERE Id='"+_id+"'";
        db.execSQL(query);
        db.close();
    }
    public void updateListItem(String _id, String checkValue){
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Checked", checkValue);
        //db.insert("contacts", null, cv);
        db.update("Product", cv, "Id='"+_id+"'", null);
        db.close();
    }

}
