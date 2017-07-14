package studio.robert.fuelrecorder.records;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import studio.robert.fuelrecorder.data.FuelRecord;
import studio.robert.fuelrecorder.data.source.IRecordSource;
import studio.robert.fuelrecorder.data.source.RecordsRepository;

/**
 * Created by robert on 2017/7/15.
 */

public class RecordsPresenter implements RecordsContract.Presenter {

    private final RecordsRepository mRepository;
    private final RecordsContract.View mMainFrag;
    private final RecordsContract.View mSubFrag;


    public RecordsPresenter(RecordsRepository repository, @NotNull RecordsContract.View mainFrag, @NotNull RecordsContract.View subFrag) {
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
    }

    @Override
    public void updateRecord(FuelRecord record) {
        //TODO: call Editor Dialog w/ record
    }
}
