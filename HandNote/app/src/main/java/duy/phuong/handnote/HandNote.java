package duy.phuong.handnote;

import android.app.Application;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import duy.phuong.handnote.DAO.LocalStorage;
import duy.phuong.handnote.Support.SharedPreferenceUtils;

/**
 * Created by Phuong on 09/05/2016.
 */
public class HandNote extends Application {
    private LocalStorage mStorage;
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferenceUtils.setPreferences(getApplicationContext());
        mStorage = new LocalStorage(getApplicationContext());
    }
    public boolean checkInternetAvailability() {
        HttpURLConnection mConnection;
        URL url;
        try {
            url = new URL("https://www.google.com/");
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.setRequestProperty("User-Agent", "Test");
            mConnection.setRequestProperty("Connection", "close");
            mConnection.setConnectTimeout(5000);
            mConnection.setReadTimeout(5000);
            mConnection.connect();
            return mConnection.getResponseCode() == 200;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteAllNote() {
        mStorage.deleteAllNotes();
    }

    public void loadEV_Dict() {
        LocalStorage localStorage = new LocalStorage(getApplicationContext());
        SQLiteDatabase db = localStorage.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Resources resources = getResources();
        loadDict(R.raw.eng_vi, resources, localStorage, db, contentValues);
        db.close();
    }

    private void loadDict(int raw, Resources resources, LocalStorage storage, SQLiteDatabase db, ContentValues contentValues) {
        InputStream inputStream = resources.openRawResource(raw);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        storage.deleteAllDict(db);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                Log.d("line", line);
                StringTokenizer tokenizer = new StringTokenizer(line, "#");
                if (tokenizer.countTokens() == 2) {
                    String word = tokenizer.nextToken();
                    String definition = tokenizer.nextToken();
                    Log.d("Infor", "w: " + word + ", def: " + definition);
                    Log.d("Insert result", String.valueOf(storage.inertEV_DictLine(word, null, definition, db, contentValues)));
                } else {
                    if (tokenizer.countTokens() == 3) {
                        String word = tokenizer.nextToken();
                        String pronunciation = tokenizer.nextToken();
                        String definition = tokenizer.nextToken();
                        Log.d("Infor", "w: " + word + ", pro: " + pronunciation + ", def: " + definition);
                        Log.d("Insert result", String.valueOf(storage.inertEV_DictLine(word, pronunciation, definition, db, contentValues)));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
