package com.example.inf0251atrabalho3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.inf0251atrabalho3.model.Moeda;

import java.util.ArrayList;
import java.util.List;

public class DBSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MoedasDB";
    private static final String TABLE_MOEDAS = "moedas";
    private static final String ID = "id"; // id
    private static final String CURRENCY = "currency"; // moeda
    private static final String BID_VALUE = "bid_value"; // valor de compra
    private static final String ASK_VALUE = "ask_value"; // valor de venda
    private static final String DATE_HOUR_INCLUSION = "date_hour_inclusion"; // data e hora de inclusao no banco
    private static final String DATE_MILLIS = "date_millis"; // data e hora de inclusao no banco em milisegundos
    private static final String[] COLUNAS = {ID, CURRENCY, BID_VALUE, ASK_VALUE, DATE_HOUR_INCLUSION, DATE_MILLIS};

    public DBSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_MOEDAS + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CURRENCY + " TEXT," +
                BID_VALUE + " REAL," +
                ASK_VALUE + " REAL," +
                DATE_HOUR_INCLUSION + " TEXT," +
                DATE_MILLIS + " INTEGER)" ;
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOEDAS);
        this.onCreate(db);
    }

    public int add(Moeda moeda) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CURRENCY, moeda.getCurrency());
        values.put(BID_VALUE, moeda.getBidValue());
        values.put(ASK_VALUE, moeda.getAskValue());
        values.put(DATE_HOUR_INCLUSION, moeda.getDateHourInclusion());
        values.put(DATE_MILLIS, moeda.getDateMillis());
        long id = db.insert(TABLE_MOEDAS, null, values);
        db.close();
        return (int) id; // id do objeto inserido
    }

    public Moeda getByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MOEDAS,       // a. tabela
                COLUNAS,                             // b. colunas
                " id = ?",                  // c. colunas para comparar
                new String[] { String.valueOf(id) }, // d. parâmetros
                null,                       // e. group by
                null,                        // f. having
                null,                       // g. order by
                null);                        // h. limit
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        } else {
            cursor.moveToFirst();
            return cursorToMoeda(cursor);
        }
    }

    private Moeda cursorToMoeda(Cursor cursor) {
        Moeda moeda = new Moeda();
        moeda.setId(cursor.getInt(0));
        moeda.setCurrency(cursor.getString(1));
        moeda.setBidValue(cursor.getFloat(2));
        moeda.setAskValue(cursor.getFloat(3));
        moeda.setDateHourInclusion(cursor.getString(4));
        moeda.setDateMillis(cursor.getLong(5));
        return moeda;
    }

    public List<Moeda> getAllByCurrency(String currency) {
        List<Moeda> listMoedas = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_MOEDAS + " WHERE " + CURRENCY + " = '" + currency + "' ORDER BY " + ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Moeda moeda = cursorToMoeda(cursor);
                listMoedas.add(moeda);
            } while (cursor.moveToNext());
        }
        return listMoedas;
    }

    public Moeda getLastIncludedByCurrency(String currency) {
        String query = "SELECT * FROM " + TABLE_MOEDAS + " WHERE " + CURRENCY + " = '" + currency + "' GROUP BY " + CURRENCY + " HAVING " + ID + " = MAX(" + ID + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        } else {
            cursor.moveToFirst();
            return cursorToMoeda(cursor);
        }
    }

    public List<Moeda> getHistoricByCurrencyAndDate(String currency, Long dateMillis) {
        List<Moeda> listMoedas = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_MOEDAS + " WHERE " + CURRENCY + " = '" + currency + "' AND " + DATE_MILLIS + " >= " + dateMillis + " ORDER BY " + DATE_MILLIS + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Moeda moeda = cursorToMoeda(cursor);
                listMoedas.add(moeda);
            } while (cursor.moveToNext());
        }
        return listMoedas;
    }
}
