package studio.robert.fuelrecorder.records;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import studio.robert.fuelrecorder.R;
import studio.robert.fuelrecorder.data.source.RecordsRepository;
import studio.robert.fuelrecorder.data.source.local.LocalRecordSource;

public class RecordsActivity extends AppCompatActivity {

    private RecordsPresenter mPresenter;
    private RecordsRepository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Main Fragment
        MainFragment mainFragment =
                (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_frame);
        if (mainFragment == null) {
            ///create it!
            mainFragment = new MainFragment();
            //Add Fragment to Activity
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.main_frame, mainFragment);
            transaction.commit();
        }
        //Sub Fragment
        SubFragment subFragment =
                (SubFragment) getSupportFragmentManager().findFragmentById(R.id.sub_frame);
        if(subFragment == null) {
            //create new one
            subFragment = new SubFragment();
            //Add Fragment to Activity
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.sub_frame, subFragment);
            transaction.commit();
        }
        //init repository
        LocalRecordSource localResource = LocalRecordSource.getInstance(this);
        mRepository = RecordsRepository.getInstance(localResource);
        //new Presenter
        mPresenter = new RecordsPresenter(mRepository, mainFragment, subFragment);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "TODO: Add New Record", Snackbar.LENGTH_SHORT)
                //        .setAction("Action", null).show();
                mPresenter.newRecord();
            }
        });
    }

}
