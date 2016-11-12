package com.huy.monthlyfinance.ProcessData;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huy.monthlyfinance.Database.DAO.ProductDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huy nguyen on 10/9/2016.
 */
public class ProcessDataMining {
    SQLiteDatabase db;
    ProductDAO productDAO;
    // ham lay du lieu can mining
    public List getDatamining(){
        List<ObjectDataMining> objectDataMiningList=new ArrayList<>();
        String sql="\n" +
                "SELECT  a.malichsumuahang,c.tensanpham,a.ngaymua\n" +
                "from lichsuchitieu a\n" +
                "left join chitietsanpham b on a.malichsumuahang=b.malichsumuahang\n" +
                "left join sanpham c on b.masanpham=c.masanpham";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                ObjectDataMining objectDataMining = new ObjectDataMining();
                objectDataMining.setTID(cursor.getString(cursor.getColumnIndex("malichsumuahang")));
                objectDataMining.setProduct_item(cursor.getString(cursor.getColumnIndex("tensanpham")));
                objectDataMining.setDate(cursor.getString(cursor.getColumnIndex("ngaymua")));
                // Adding contact to list
                objectDataMiningList.add(objectDataMining);
            } while (cursor.moveToNext());
        }
        // return nhom san pham list
        return objectDataMiningList;
    }
    //ham set metadata cho product
    public List getDataProduct( ){
        List<Object> listproduct=new ArrayList<>();
        String sql="select tensanpham from sanpham";
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do {
                listproduct.add(cursor.getString(cursor.getColumnIndex("tensanpham")));
            }while(cursor.moveToNext());
        }
        return listproduct;
    }
    // ham convert data sang binary
    // ham run thuat toan
}
