package studio.robert.fuelrecorder.records;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import studio.robert.fuelrecorder.BasePresenter;
import studio.robert.fuelrecorder.BaseView;
import studio.robert.fuelrecorder.data.FuelRecord;

/**
 *  This specifies the contract between the view and the presenter.
 * Created by robert on 2017/7/15.
 */

public class RecordsContract {
    interface View extends BaseView<Presenter> {
        void showResult(List<FuelRecord> records);
        void showNoRecords();
    }

    interface Presenter extends BasePresenter {
        void loadRecords();
        void newRecord();
        void updateRecord(FuelRecord record);
        void saveNewRecord(FuelRecord record);
        void saveExistRecord(FuelRecord record);
        void deleteConfrim(FuelRecord record);
        void deleteRecord(FuelRecord record);
    }
}
