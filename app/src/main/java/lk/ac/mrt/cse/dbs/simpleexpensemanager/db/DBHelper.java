package lk.ac.mrt.cse.dbs.simpleexpensemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "170676P";
    private final static int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS " + DBConstants.ACCOUNT_TABLE + "(" +
                DBConstants.ACCOUNT_ACCOUNTNUM + " VARCHAR PRIMARY KEY," +
                DBConstants.ACCOUNT_BANKNAME + " VARCHAR," +
                DBConstants.ACCOUNT_HOLDERNAME + " VARCHAR," +
                DBConstants.ACCOUNT_BALANCE + " NUMERIC" +
                ");");

        database.execSQL("CREATE TABLE IF NOT EXISTS " + DBConstants.EXPENSETYPE_TABLE +
                "(" + DBConstants.EXPENSE_TYPE_TYPE + " VARCHAR(31) PRIMARY KEY);");
        database.execSQL("INSERT INTO " + DBConstants.EXPENSETYPE_TABLE +
                "(" + DBConstants.EXPENSE_TYPE_TYPE + ") VALUES (?);", new String[]{DBConstants.TYPE_EXPENSE});
        database.execSQL("INSERT INTO " + DBConstants.EXPENSETYPE_TABLE +
                "(" + DBConstants.EXPENSE_TYPE_TYPE + ") VALUES (?);", new String[]{DBConstants.TYPE_INCOME});

        database.execSQL("CREATE TABLE IF NOT EXISTS " + DBConstants.TRANSACTION_TABLE + "(" +
                DBConstants.TRANSACTION_ID + " INTEGER PRIMARY KEY," +
                DBConstants.TRANSACTION_ACCOUNTNUM + " VARCHAR NOT NULL," +
                DBConstants.TRANSACTION_TYPE + " VARCHAR NOT NULL," +
                DBConstants.TRANSACTION_DATE + " TIMESTAMP NOT NULL," +
                DBConstants.TRANSACTION_AMOUNT + " NUMERIC NOT NULL," +
                "FOREIGN KEY (" + DBConstants.TRANSACTION_ACCOUNTNUM + ") REFERENCES "
                + DBConstants.ACCOUNT_TABLE + "(" + DBConstants.ACCOUNT_ACCOUNTNUM + ")," +
                "FOREIGN KEY (" + DBConstants.TRANSACTION_TYPE + ") REFERENCES "
                + DBConstants.EXPENSETYPE_TABLE + "(" + DBConstants.EXPENSE_TYPE_TYPE + ")" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String message = String.format("DB upgrade from %s to %s", Integer.toString(oldVersion), Integer.toString(newVersion));
        Log.w(this.getClass().getName(), message);
        database.execSQL("DROP TABLE IF EXISTS " + DBConstants.TRANSACTION_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + DBConstants.EXPENSETYPE_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + DBConstants.ACCOUNT_TABLE);
        onCreate(database);
    }
}