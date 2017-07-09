package studio.robert.fuelrecorder.data.source;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import studio.robert.fuelrecorder.data.FuelRecord;

/**
 * Created by robert on 2017/5/26.
 */

public interface IRecordSource {
    interface LoadDataCallback {
        void onDataLoaded(List<FuelRecord> record);
        void onDataNotAvailable();
    }

    interface GetDataCallback {
        void onDataGot(FuelRecord record);
        void onDataNotAvailable();
    }

    void getRecords(@NotNull LoadDataCallback callback);
    void getRecord(@NotNull String recordId, @NotNull GetDataCallback callback);

    void saveRecord(@NotNull FuelRecord record);
    void updateRecord(@NotNull FuelRecord record);
    void deleteRecord(@NotNull String recordId);
    void refreshRecords();
}
