package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DBHelper;

public class PersistentExpenseManager extends ExpenseManager {
    private DBHelper DBHelper;

    public PersistentExpenseManager(Context context) {
        DBHelper = new DBHelper(context);
        setup();
    }

    @Override
    public void setup() {
        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(DBHelper);
        setTransactionsDAO(persistentTransactionDAO);
        AccountDAO persistentAccountDAO = new PersistentAccountDAO(DBHelper);
        setAccountsDAO(persistentAccountDAO);
    }
}
