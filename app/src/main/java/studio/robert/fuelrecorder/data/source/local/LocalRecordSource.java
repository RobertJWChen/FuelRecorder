package studio.robert.fuelrecorder.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import studio.robert.fuelrecorder.data.source.local.Contract.RecordEntry;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import studio.robert.fuelrecorder.data.FuelRecord;
import studio.robert.fuelrecorder.data.source.IRecordSource;

import static studio.robert.fuelrecorder.utils.Preconditions.checkNotNull;

/**
 * Created by robert on 2017/5/26.
 */

public class LocalRecordSource implements IRecordSource {

    private static LocalRecordSource INSTANCE;
    private DbHelper mDbHelper;

    private LocalRecordSource(@NotNull Context context) {
        checkNotNull(context);
        mDbHelper = new DbHelper(context);
    }

    public static LocalRecordSource getInstance(@NotNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new LocalRecordSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getRecords(@NotNull LoadDataCallback callback) {
        List<FuelRecord> records = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                RecordEntry.COL_NAME_ENTRY_ID,
                RecordEntry.COL_DATE,
                RecordEntry.COL_AMOUNT,
                RecordEntry.COL_PRICE,
                RecordEntry.COL_MILEAGE,
                RecordEntry.COL_NOTE
        };

        Cursor c = db.query(RecordEntry.TABLE_NAME, projection, null, null, null, null,null);

        if (c != null && c.getCount() > 0) {
            while(c.moveToNext()) {
                String entry_id = c.getString(c.getColumnIndexOrThrow(RecordEntry.COL_NAME_ENTRY_ID));
                long date = c.getLong(c.getColumnIndex(RecordEntry.COL_DATE));
                double amount = c.getDouble(c.getColumnIndexOrThrow(RecordEntry.COL_AMOUNT));
                double price = c.getDouble(c.getColumnIndexOrThrow(RecordEntry.COL_PRICE));
                double mileage = c.getDouble(c.getColumnIndexOrThrow(RecordEntry.COL_MILEAGE));
                String note = c.getString(c.getColumnIndexOrThrow(RecordEntry.COL_NOTE));
                records.add(new FuelRecord(entry_id, date, amount, price, mileage, note));
            }
        }
        if (c != null) {
            c.close();
        }
        db.close();
        if (records.isEmpty()) {
            //table is empty or db is new
            callback.onDataNotAvailable();
        } else {
            callback.onDataLoaded(records);
        }
    }

    @Override
    public void getRecord(@NotNull String recordId, @NotNull GetDataCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                RecordEntry.COL_NAME_ENTRY_ID,
                RecordEntry.COL_DATE,
                RecordEntry.COL_AMOUNT,
                RecordEntry.COL_PRICE,
                RecordEntry.COL_MILEAGE,
                RecordEntry.COL_NOTE
        };
        String selection = RecordEntry.COL_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {recordId};
        FuelRecord record = null;
        Cursor c = db.query(RecordEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String entry_id = c.getString(c.getColumnIndexOrThrow(RecordEntry.COL_NAME_ENTRY_ID));
            long date = c.getLong(c.getColumnIndexOrThrow(RecordEntry.COL_DATE));
            double amount = c.getDouble(c.getColumnIndexOrThrow(RecordEntry.COL_AMOUNT));
            double price = c.getDouble(c.getColumnIndexOrThrow(RecordEntry.COL_PRICE));
            double mileage = c.getDouble(c.getColumnIndexOrThrow(RecordEntry.COL_MILEAGE));
            String note = c.getString(c.getColumnIndexOrThrow(RecordEntry.COL_NOTE));
            record = new FuelRecord(entry_id, date, amount, price, mileage, note);
        }
        if (c != null) {
            c.close();
        }

        if (record != null) {
            callback.onDataGot(record);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveRecord(@NotNull FuelRecord record) {
        checkNotNull(record);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(RecordEntry.COL_NAME_ENTRY_ID, record.getId());
        cv.put(RecordEntry.COL_DATE, record.getDate().getTime());
        cv.put(RecordEntry.COL_AMOUNT, record.getAmount());
        cv.put(RecordEntry.COL_PRICE, record.getPrice());
        cv.put(RecordEntry.COL_MILEAGE, record.getMileage());
        cv.put(RecordEntry.COL_NOTE, record.getNote());
        db.insert(RecordEntry.TABLE_NAME, null, cv);
        db.close();
    }

    @Override
    public void updateRecord(@NotNull FuelRecord record) {
        checkNotNull(record);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(RecordEntry.COL_DATE, record.getDateTimeStamp());
        cv.put(RecordEntry.COL_AMOUNT, record.getAmount());
        cv.put(RecordEntry.COL_PRICE, record.getPrice());
        cv.put(RecordEntry.COL_MILEAGE, record.getMileage());
        cv.put(RecordEntry.COL_NOTE, record.getNote());

        String selection = RecordEntry.COL_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { record.getId() };

        db.update(RecordEntry.TABLE_NAME, cv, selection, selectionArgs);
        db.close();
    }

    @Override
    public void deleteRecord(@NotNull String recordId) {
        checkNotNull(recordId);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selection = RecordEntry.COL_NAME_ENTRY_ID+" LIKE ?";
        String[] selectionArgs = {recordId};
        db.delete(RecordEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    @Override
    public void deleteAllRecords() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(RecordEntry.TABLE_NAME, null, null);
        db.close();
    }

    @Override
    public void refreshRecords() {
        //TODO: if need
    }
}
