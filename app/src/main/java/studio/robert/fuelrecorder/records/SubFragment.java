package studio.robert.fuelrecorder.records;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import studio.robert.fuelrecorder.data.FuelRecord;

/**
 * Created by robert on 2017/7/16.
 */

public class SubFragment extends Fragment implements RecordsContract.View {

    private RecordsContract.Presenter mPresenter;

    public SubFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setPresenter(RecordsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showResult(List<FuelRecord> records) {

    }

    @Override
    public void showNoRecords() {

    }
}
