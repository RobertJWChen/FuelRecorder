package studio.robert.fuelrecorder.data.source.local;

import android.provider.BaseColumns;

/**
 * Created by robert on 2017/5/23.
 */

public final class Contract {

    //To prevent sb. from accidentally instantiating the contracts class,
    // give it an empty constrructor.
    private Contract(){};

    //Contracts
    public static abstract class RecordEntry implements BaseColumns {
        public static final String TABLE_NAME ="history";

        public static final String COL_NAME_ENTRY_ID = "entryid";
        public static final String COL_DATE = "date";
        public static final String COL_AMOUNT = "amount";
        public static final String COL_PRICE = "price";
        public static final String COL_MILEAGE = "mileage";
        public static final String COL_NOTE = "note";
    }
}
