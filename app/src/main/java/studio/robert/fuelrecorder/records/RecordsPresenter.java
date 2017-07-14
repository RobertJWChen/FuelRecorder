package studio.robert.fuelrecorder.records;

import android.app.AlertDialog;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import studio.robert.fuelrecorder.data.FuelRecord;
import studio.robert.fuelrecorder.data.source.IRecordSource;
import studio.robert.fuelrecorder.data.source.RecordsRepository;

/**
 * Created by robert on 2017/7/15.
 */

public class RecordsPresenter implements RecordsContract.Presenter {

    private final RecordsRepository mRepository;
    private final MainFragment mMainFrag;
    private final SubFragment mSubFrag;


    public RecordsPresenter(RecordsRepository repository, @NotNull MainFragment mainFrag, @NotNull SubFragment subFrag) {
        this.mRepository = repository;
        mMainFrag = mainFrag;
        mSubFrag = subFrag;

        mainFrag.setPresenter(this);
        mSubFrag.setPresenter(this);
    }

    @Override
    public void start() {
        loadRecords();
    }

    @Override
    public void loadRecords() {
        mRepository.getRecords(new IRecordSource.LoadDataCallback() {
            @Override
            public void onDataLoaded(List<FuelRecord> records) {
                mMainFrag.showResult(records);
                mSubFrag.showResult(records);
            }

            @Override
            public void onDataNotAvailable() {
                mMainFrag.showNoRecords();
                mSubFrag.showNoRecords();
            }
        });
    }

    @Override
    public void newRecord() {
        //TODO: call Editor Dialog
        mSubFrag.recordEditor(new FuelRecord(System.currentTimeMillis(), 0.0, 0.0, 0.0, ""), true);
    }

    @Override
    public void updateRecord(FuelRecord record) {
        //TODO: call Editor Dialog w/ record
        mSubFrag.recordEditor(record, false);
    }

    @Override
    public void saveNewRecord(FuelRecord record) {
        mRepository.saveRecord(record);
    }

    @Override
    public void saveExistRecord(FuelRecord record) {
        mRepository.updateRecord(record);
    }

    @Override
    public void deleteConfrim(FuelRecord record) {
        mMainFrag.confirmDelete(record);
    }

    @Override
    public void deleteRecord(FuelRecord record) {
        mRepository.deleteRecord(record.getId());
        loadRecords();
    }
}
