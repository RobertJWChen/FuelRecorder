package studio.robert.fuelrecorder.data.source;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import studio.robert.fuelrecorder.data.FuelRecord;

import static studio.robert.fuelrecorder.utils.Preconditions.checkNotNull;

/**
 * Created by robert on 2017/5/27.
 */

public class RecordsRepository implements IRecordSource {

    private static RecordsRepository INSTANCE = null;
    private final IRecordSource mLocalDataCtrl;
    //TODO: remote repository... TBD
    //private final IRecordSource mRemoteDataCtrl;
    //Cache
    Map<String, FuelRecord> mCachedRecords;
    boolean mCacheIsDirty = false;//Variable to trigger refresh, prepare for handling the remote repository implementation

    //Constructor: set private to prevent directly access
    private RecordsRepository(@NotNull IRecordSource localCtrl) {
        mLocalDataCtrl = checkNotNull(localCtrl);
        //TODO: remote data repository
        //mRemoteDataCtrl = remoteCtrl;
    }

    public static RecordsRepository getInstance(IRecordSource localCtrl) {
        if (INSTANCE == null) {
            INSTANCE = new RecordsRepository(localCtrl);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getRecords(@NotNull final LoadDataCallback callback) {
        checkNotNull(callback);
        //if cache available, response immediately
        if (mCachedRecords != null && !mCacheIsDirty) {
            callback.onDataLoaded(new ArrayList<>(mCachedRecords.values()));
            return;
        }
        if (mCacheIsDirty) {
            //TODO: remote repository
            //if cached is dirty, need to fetch new from the network
        } else {
            //query the local storage if available. If not, query the network
            mLocalDataCtrl.getRecords(new LoadDataCallback() {
                @Override
                public void onDataLoaded(List<FuelRecord> records) {
                    refreshCache(records);
                    callback.onDataLoaded(new ArrayList<FuelRecord>(mCachedRecords.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }
    }

    private void refreshCache(List<FuelRecord> records) {
        if (mCachedRecords == null) {
            mCachedRecords = new LinkedHashMap<>();
        }
        mCachedRecords.clear();
        for (FuelRecord record:records) {
            mCachedRecords.put(record.getId(), record);
        }
        mCacheIsDirty = false;
    }

    @Override
    public void getRecord(@NotNull String recordId, @NotNull final GetDataCallback callback) {
        checkNotNull(recordId);
        checkNotNull(callback);
        FuelRecord cachedRecord;
        if (mCachedRecords == null || mCachedRecords.isEmpty()) {
            cachedRecord =  null;
        } else {
            cachedRecord = mCachedRecords.get(recordId);
        }
        if (cachedRecord != null) {
            callback.onDataGot(cachedRecord);
            return;
        }

        //load froam db
        mLocalDataCtrl.getRecord(recordId, new IRecordSource.GetDataCallback() {

            @Override
            public void onDataGot(FuelRecord record) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedRecords == null) {
                    mCachedRecords = new LinkedHashMap<>();
                }
                mCachedRecords.put(record.getId(), record);
                callback.onDataGot(record);
            }

            @Override
            public void onDataNotAvailable() {
                //TODO: if local db has no data, remote one
                //by now, just return not available
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveRecord(@NotNull FuelRecord record) {
        checkNotNull(record);
        mLocalDataCtrl.saveRecord(record);
        //TODO:remote repository
        if (mCachedRecords == null) {
            mCachedRecords = new LinkedHashMap<>();
        }
        mCachedRecords.put(record.getId(), record);
    }

    @Override
    public void updateRecord(@NotNull FuelRecord record) {
        checkNotNull(record);
        mLocalDataCtrl.updateRecord(record);
        //TODO:remote repository
        if (mCachedRecords == null) {
            mCachedRecords = new LinkedHashMap<>();
        }
        mCachedRecords.put(record.getId(), record);
    }

    @Override
    public void deleteRecord(@NotNull String recordId) {
        checkNotNull(recordId);
        mLocalDataCtrl.deleteRecord(recordId);
        //TODO:remote repository
        if (mCachedRecords == null) {
            mCachedRecords = new LinkedHashMap<>();
        }
        mCachedRecords.remove(recordId);
    }

    @Override
    public void deleteAllRecords() {
        mLocalDataCtrl.deleteAllRecords();
        if (mCachedRecords == null) {
            mCachedRecords = new LinkedHashMap<>();
        }
        mCachedRecords.clear();
    }

    @Override
    public void refreshRecords() {
        mCacheIsDirty = true;
    }
}
