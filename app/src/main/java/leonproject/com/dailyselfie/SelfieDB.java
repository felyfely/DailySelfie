package leonproject.com.dailyselfie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fudan on 3/18/15.
 */
public class SelfieDB extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "selfie";
    public static final String PHOTOPATH = "photopath";
    public static final String TIME = "time";

    public SelfieDB(Context context) {
        super(context, "selfie", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + TIME
                + " STRING PRIMARY KEY," + PHOTOPATH
                + " TEXT NOT NULL)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

