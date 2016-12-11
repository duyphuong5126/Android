package com.huy.monthlyfinance.Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by huy nguyen on 9/15/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // database version
    private static final int DATABASE_VERSION = 2;
    //ten co so du lieu
    public static final String DATABASE_NAME = "QuanLyTaiChinh.db";

    // tables
    public static final String tblProduct = "SanPham";
    public static final String tblProductGroup = "NhomSanPham";
    public static final String tblProductDetail = "ChiTietSanPham";
    public static final String tblAccount = "TaiKhoan";
    public static final String tblAccountDetail = "ChiTietTaiKhoan";
    public static final String tblUser = "NguoiDung";
    public static final String tblExpensesHistory = "LichSuChiTieu";

    // Product table
    public static final String productID = "MaSanPham";
    public static final String productNameEN = "ProductName";
    public static final String productNameVI = "TenSanPham";
    public static final String productGroupID = "MaNhomSanPham";
    public static final String productCalculationUnit = "DonViTinh";
    public static final String productImage = "HinhAnhSanPham";
    public static final String productGroupImage = "HinhAnhNhomSanPham";
    public static final String productGroupNameEN = "ProductGroupName";
    public static final String productGroupNameVI = "TenNhomSanPham";

    //Product Detail table
    public static final String productDetailID = "MaChiTietSanPham";
    public static final String productCost = "Gia";
    public static final String productQuantity = "SoLuong";

    //Account table
    public static final String accountID = "MaTaiKhoan";
    public static final String userID = "MaNguoiDung";
    public static final String accountName = "Tentaikhoan";
    public static final String accountType = "Loaitaikhoan";
    public static final String accountCurrency = "LoaiTienTe";
    public static final String accountNote = "GhiChu";
    public static final String accountState = "TrangThai";
    public static final String accountInitBalance = "SoDuDau";
    public static final String accountCurrentBalance = "SoDuHienTai";

    public static final String accountDetailID = "MaChiTietTaiKhoan";
    public static final String accountTransactionDate = "NgayGiaoDich";
    public static final String accountTotal = "TongTien";

    //User table
    public static final String userLoginName = "TenDangNhap";
    public static final String userPassword = "MatKhau";
    public static final String userEmail = "Email";

    //Expense History table
    public static final String expenseHistoryID = "MaLichSuChiTieu";
    public static final String expenseDate = "NgayMua";
    public static final String expenseTotalCost = "GiaTri";

    // Create table commands
    private static final String CREATE_TABLE_PRODUCT =
            "create table " + tblProduct + "(" +
                    productID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ","
                    + productNameEN + " TEXT NOT NULL" + "," + productNameVI + " TEXT NOT NULL" + "," +
                    productGroupID + " INTEGER" + "," + productCalculationUnit +
                    " TEXT" + "," + productImage + " TEXT ," + " FOREIGN KEY(" + productGroupID + ") REFERENCES " +
                    tblProductGroup + "(" + productGroupID + "))";

    private static final String CREATE_TABLE_PRODUCT_GROUP =
            "create table " + tblProductGroup + "(" + productGroupID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ","
                    + productGroupNameEN + " TEXT NOT NULL" + "," + productGroupNameVI + " TEXT NOT NULL" + "," + productGroupImage + " TEXT" + ")";

    private static final String CREATE_TABLE_PRODUCT_DETAIL =
            "create table " + tblProductDetail + "(" + productDetailID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ","
                    + productCost + " INTEGER" + "," + productID + " INTEGER" + "," + expenseHistoryID + " INTEGER" + ","
                    + productQuantity + " INTEGER ," + " FOREIGN KEY(" + productID + ") REFERENCES " + tblProduct
                    + "(" + productID + ")," + " FOREIGN KEY(" + expenseHistoryID + ") REFERENCES " + tblExpensesHistory
                    + "(" + expenseHistoryID + "))";

    private static final String CREATE_TABLE_ACCOUNT =
            "create table " + tblAccount + "(" + accountID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ","
                    + accountName + " TEXT NOT NULL" + "," + accountType + " TEXT NOT NULL" + "," + accountCurrency + " TEXT NOT NULL" + ","
                    + accountInitBalance + " INTEGER" + "," + accountCurrentBalance + " INTEGER" + "," + accountNote + " TEXT" + ","
                    + accountState + " TEXT" + "," + userID + " INTEGER ," + " FOREIGN KEY(" + userID + ") " +
                    "REFERENCES " + tblUser + "(" + userID + "))";

    private static final String CREATE_TABLE_ACCOUNT_DETAIL =
            "create table " + tblAccountDetail + "(" + accountDetailID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ","
                    + expenseHistoryID + " INTEGER" + "," + accountID + " INTEGER" + "," + accountTransactionDate + " TEXT NOT NULL ,"
                    + accountTotal + " REAL," + " FOREIGN KEY(" + accountID + ")" +
                    " REFERENCES " + tblAccount + "(" + accountID + "),"
                    + " FOREIGN KEY(" + expenseHistoryID + ") REFERENCES " + tblExpensesHistory + "(" + expenseHistoryID + "))";

    private static final String CREATE_TABLE_USER =
            "create table " + tblUser + "(" + userID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ","
                    + userLoginName + " TEXT NOT NULL" + "," + userPassword + " TEXT " + "," + userEmail + " TEXT NOT NULL" + ")";

    private static final String CREATE_TABLE_EXPENSE_HISTORY =
            "create table " + tblExpensesHistory + "(" + expenseHistoryID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ","
                    + userID + " INTEGER" + "," + expenseDate + " TEXT NOT NULL" + "," + expenseTotalCost + " INTEGER ,"
                    + " FOREIGN KEY(" + userID + ") REFERENCES " + tblUser + "(" + userID + "))";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("Account", CREATE_TABLE_ACCOUNT);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_PRODUCT_GROUP);
            db.execSQL(CREATE_TABLE_PRODUCT);
            db.execSQL(CREATE_TABLE_PRODUCT_DETAIL);
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(CREATE_TABLE_ACCOUNT);
            db.execSQL(CREATE_TABLE_ACCOUNT_DETAIL);
            db.execSQL(CREATE_TABLE_EXPENSE_HISTORY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS " + tblProduct);
        db.execSQL("DROP TABLE IF EXISTS " + tblProductGroup);
        db.execSQL("DROP TABLE IF EXISTS " + tblProductDetail);
        db.execSQL("DROP TABLE IF EXISTS " + tblAccount);
        db.execSQL("DROP TABLE IF EXISTS " + tblAccountDetail);
        db.execSQL("DROP TABLE IF EXISTS " + tblUser);
        db.execSQL("DROP TABLE IF EXISTS " + tblExpensesHistory);

        // Recreate
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
