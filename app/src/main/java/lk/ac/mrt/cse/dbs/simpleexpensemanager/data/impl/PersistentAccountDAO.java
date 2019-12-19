package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DBConstants;

public class PersistentAccountDAO implements AccountDAO {
    private SQLiteOpenHelper sqlhelper;

    public PersistentAccountDAO(SQLiteOpenHelper helper) {
        this.sqlhelper = helper;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase database = sqlhelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBConstants.ACCOUNT_ACCOUNTNUM, account.getAccountNo());
        values.put(DBConstants.ACCOUNT_BANKNAME, account.getBankName());
        values.put(DBConstants.ACCOUNT_HOLDERNAME, account.getAccountHolderName());
        values.put(DBConstants.ACCOUNT_BALANCE, account.getBalance());

        database.insert(DBConstants.ACCOUNT_TABLE, null, values);
    }


    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase database = sqlhelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT " + DBConstants.ACCOUNT_ACCOUNTNUM + " from "
                + DBConstants.ACCOUNT_TABLE, null);
        ArrayList<String> accountNumbers = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            accountNumbers.add(cursor.getString(0));
        }
        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase database = sqlhelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * from " + DBConstants.ACCOUNT_TABLE, null);
        ArrayList<Account> accounts = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Account account = new Account(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getDouble(3));
            accounts.add(account);
        }
        cursor.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = sqlhelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * from " + DBConstants.ACCOUNT_TABLE + " WHERE " + DBConstants.ACCOUNT_ACCOUNTNUM + "=?;", new String[]{accountNo});
        Account account;
        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getDouble(3));
        } else {
            throw new InvalidAccountException("Invalid account ID");
        }
        cursor.close();
        return account;
    }


    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase database = sqlhelper.getWritableDatabase();

        if (accountNo == null) throw new InvalidAccountException("Account was not selected");

        database.beginTransaction();
        Account account = getAccount(accountNo);

        if (account != null) {
            double newAmount;
            if (expenseType == ExpenseType.EXPENSE) {
                newAmount = account.getBalance() - amount;
            } else if (expenseType == ExpenseType.INCOME) {
                newAmount = account.getBalance() + amount;
            } else {
                throw new InvalidAccountException("Unknown Expense Type");
            }

            if (newAmount < 0){
                throw  new InvalidAccountException("Insufficient balance. (" + account.getBalance() + " in the account)");
            }

            database.execSQL("UPDATE " + DBConstants.ACCOUNT_TABLE + " SET "
                            + DBConstants.ACCOUNT_BALANCE + " = ? WHERE " +
                            DBConstants.ACCOUNT_ACCOUNTNUM + " = ?",
                    new String[]{Double.toString(newAmount), accountNo});
            database.endTransaction();
        } else {
            throw new InvalidAccountException("Account ID");
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = sqlhelper.getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * from " + DBConstants.ACCOUNT_TABLE + " WHERE " + DBConstants.ACCOUNT_ACCOUNTNUM + "=?;", new String[]{accountNo});
        if (cursor.moveToFirst()) {
            database.delete(DBConstants.ACCOUNT_TABLE, DBConstants.ACCOUNT_ACCOUNTNUM + " = ?", new String[]{accountNo});
        } else {
            throw new InvalidAccountException("Invalid account ID");
        }
        cursor.close();
    }


}
