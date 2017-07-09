package studio.robert.fuelrecorder.data.source;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import studio.robert.fuelrecorder.data.FuelRecord;

/**
 * Created by robert on 2017/5/27.
 * Unit Test  for the implementation of the data repository
 */
public class RecordRepositoryTest {
    //Test Data

    private List<FuelRecord> mTestRecords = initTestDatas();
    private RecordRepository mRecordRepository;

    @Mock
    private Context mContext;

    @Mock
    private IRecordSource mLocalDataSource;

    @Mock
    private IRecordSource.GetDataCallback mGetDataCallback;

    @Mock
    private IRecordSource.LoadDataCallback mLoadDataCallback;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<IRecordSource.LoadDataCallback> mLoadDataCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<IRecordSource.GetDataCallback> mGetDataCallbackArgumentCaptor;

    @Before
    public void setupEnv() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mRecordRepository = RecordRepository.getInstance(mLocalDataSource);
    }

    @After
    public void destroyRepository() {
        mRecordRepository.destroyInstance();
    }

    @Test
    public void getRecords_notAvailable() {
        //TEST1: data not available
        mRecordRepository.getRecords(mLoadDataCallback);
        verify(mLocalDataSource).getRecords(mLoadDataCallbackArgumentCaptor.capture());
        mLoadDataCallbackArgumentCaptor.getValue().onDataNotAvailable();
    }

    @Test
    public void getRecords_dataLoaded() {
        //TEST2: dataLoaded
        mRecordRepository.getRecords(mLoadDataCallback);
        verify(mLocalDataSource).getRecords(mLoadDataCallbackArgumentCaptor.capture());
        mLoadDataCallbackArgumentCaptor.getValue().onDataLoaded(mTestRecords);
        //TEST3: check cache
        mRecordRepository.getRecords(mLoadDataCallback);//2nd to call the getRecords to check chache mechanism
        verify(mLocalDataSource).getRecords(any(IRecordSource.LoadDataCallback.class));
    }
    //TODO: actually, above 2 cases will be meaningful iff we implement the remote repository mechanism

    @Test
    public void getRecord_notAvailable() {
        mRecordRepository.getRecord("0", mGetDataCallback);
        verify(mLocalDataSource).getRecord(eq("0"), mGetDataCallbackArgumentCaptor.capture());
        mGetDataCallbackArgumentCaptor.getValue().onDataNotAvailable();
    }

    @Test
    public void getRecord_onDataGot() {
        FuelRecord testRecord = mTestRecords.get(0);
        mRecordRepository.getRecord(testRecord.getId(), mGetDataCallback);
        verify(mLocalDataSource).getRecord(eq(testRecord.getId()), any(IRecordSource.GetDataCallback.class));
    }

    @Test
    public void saveRecord() {
        //TEST Record
        DateFormat df = new SimpleDateFormat("yyy-MM-dd");
        FuelRecord record = null;
        try {
            record = new FuelRecord(df.parse("2017-07-01").getTime(), 4.0, 100, 15000, "saveRecordTest");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //save new record
        mRecordRepository.saveRecord(record);
        //check
        verify(mLocalDataSource).saveRecord(record);
        assertThat(mRecordRepository.mCachedRecords.size(), is(1));
    }

    @Test
    public void updateRecord() {
        //TEST Record
        DateFormat df = new SimpleDateFormat("yyy-MM-dd");
        FuelRecord record = null;
        try {
            record = new FuelRecord(df.parse("2017-07-02").getTime(), 4.0, 100, 15000, "saveRecordTest");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //save new record
        mRecordRepository.saveRecord(record);

        //update test, test FuelRecord.setXXX functions in the mean time
        long newTime = record.getDateTimeStamp()-1000;
        record.setTimeStamp(newTime);
        double newAmount = record.getAmount()+1;
        record.setAmount(newAmount);
        double newPrice = record.getPrice()+1;
        record.setPrice(newPrice);
        double newMileage = record.getMileage()+1;
        record.setMileAge(newMileage);
        record.setNote("updateRecordTest");
        mRecordRepository.updateRecord(record);

        //verifying
        verify(mLocalDataSource).updateRecord(record);
        assertThat(mRecordRepository.mCachedRecords.size(), is(1));
        assertThat(mRecordRepository.mCachedRecords.get(record.getId()).getDateTimeStamp(), is(newTime));
        assertThat(mRecordRepository.mCachedRecords.get(record.getId()).getAmount(), is(newAmount));
        assertThat(mRecordRepository.mCachedRecords.get(record.getId()).getPrice(), is(newPrice));
        assertThat(mRecordRepository.mCachedRecords.get(record.getId()).getMileage(), is(newMileage));
        assertThat(mRecordRepository.mCachedRecords.get(record.getId()).getNote(), is("updateRecordTest"));
    }

    @Test
    public void deleteRecord() {
        //TODO: test deleteRecord funciton
        for (FuelRecord record:mTestRecords) {
            mRecordRepository.saveRecord(record);
        }
        FuelRecord delRecord = mTestRecords.get(0);
        mRecordRepository.deleteRecord(delRecord.getId());
        verify(mLocalDataSource).deleteRecord(eq(delRecord.getId()));
        assertThat(mRecordRepository.mCachedRecords.size(), is(mTestRecords.size()-1));
    }

    @Test
    public void refresh() {
        mRecordRepository.refreshRecords();
        assertThat(mRecordRepository.mCacheIsDirty, is(true));
    }

    private List<FuelRecord> initTestDatas() {
        List<FuelRecord> records = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyy-MM-dd");
        try {
            records.add(new FuelRecord(df.parse("2017-06-01").getTime(), 3.5, 100, 10000, "teset1"));
            records.add(new FuelRecord(df.parse("2017-06-02").getTime(), 3.0, 110, 10150, "teset2"));
            records.add(new FuelRecord(df.parse("2017-06-03").getTime(), 3.1, 120, 10300, "teset3"));
            records.add(new FuelRecord(df.parse("2017-06-04").getTime(), 3.2, 130, 10450, "teset4"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return records;
    }
}
