package studio.robert.fuelrecorder.data.source.local;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import studio.robert.fuelrecorder.data.FuelRecord;
import studio.robert.fuelrecorder.data.source.IRecordSource;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 *
 * Created by RobertJW_Chen on 2017/7/12.
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LocalRecordSourceTest {

    private List<FuelRecord> mTestRecords = initTestData();
    private LocalRecordSource mLocalRecordSource;

    @Before
    public void setup() {
        //mLocalRecordSource =
        //        LocalRecordSource.getInstance(InstrumentationRegistry.getContext());
        mLocalRecordSource =
                LocalRecordSource.getInstance(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void cleanup() {
        mLocalRecordSource.deleteAllRecords();
    }

    @Test
    public void preConditionTest() {
        assertNotNull(mLocalRecordSource);
    }

    @Test
    public void saveRecord_getRecord() {
        final FuelRecord newRecord = mTestRecords.get(0);
        //save record
        mLocalRecordSource.saveRecord(newRecord);

        mLocalRecordSource.getRecord(newRecord.getId(), new IRecordSource.GetDataCallback() {
            @Override
            public void onDataGot(FuelRecord record) {
                assertThat(record, is(newRecord));
            }

            @Override
            public void onDataNotAvailable() {
                fail("Wrong Callback!");
            }
        });
    }

    @Test
    public void saveRecords_getRecords() {
        //save records
        for(FuelRecord record:mTestRecords) {
            mLocalRecordSource.saveRecord(record);
        }
        mLocalRecordSource.getRecords(new LocalRecordSource.LoadDataCallback(){
            @Override
            public void onDataLoaded(List<FuelRecord> records) {
                assertNotNull(records);
                assertThat(records.size(), is(mTestRecords.size()));
                for (FuelRecord record:records) {
                    assertTrue(mTestRecords.contains(record));
                }
            }

            @Override
            public void onDataNotAvailable() {
                fail("Wrong Callback!");
            }
        });
    }

    @Test
    public void saveRecord_updateRecord() {
        //saveRecord
        final FuelRecord testRecord = mTestRecords.get(0);
        mLocalRecordSource.saveRecord(testRecord);

        testRecord.setNote("updated Note");
        mLocalRecordSource.updateRecord(testRecord);
        mLocalRecordSource.getRecord(testRecord.getId(), new IRecordSource.GetDataCallback() {
            @Override
            public void onDataGot(FuelRecord record) {
                assertThat(record.getId(), is(testRecord.getId()));
                assertThat(record.getAmount(), is(testRecord.getAmount()));
                assertThat(record.getDateTimeStamp(), is(testRecord.getDateTimeStamp()));
                assertThat(record.getPrice(), is(testRecord.getPrice()));
                assertThat(record.getNote(), is(testRecord.getNote()));
                assertThat(record, is(testRecord));
            }

            @Override
            public void onDataNotAvailable() {
                fail("missing updated record!");
            }
        });
    }

    @Test
    public void saveRecords_delteRecord() {
        //saveRecords
        for(FuelRecord record: mTestRecords) {
            mLocalRecordSource.saveRecord(record);
        }
        //delete  first record
        mLocalRecordSource.deleteRecord(mTestRecords.get(0).getId());

        //load records and check
        mLocalRecordSource.getRecords(new IRecordSource.LoadDataCallback() {
            @Override
            public void onDataLoaded(List<FuelRecord> records) {
                assertTrue(records.size() == mTestRecords.size()-1);
                assertTrue(mTestRecords.containsAll(records));
            }

            @Override
            public void onDataNotAvailable() {
                fail("Wrong Callback!");
            }
        });
    }

    @Test
    public void saveRecord_delteAllRecords() {
        //save record
        mLocalRecordSource.saveRecord(mTestRecords.get(0));
        //deleteAll
        mLocalRecordSource.deleteAllRecords();
        //check
        //TODO: use mock callback function to check the callback status

        mLocalRecordSource.getRecords(new IRecordSource.LoadDataCallback() {

            @Override
            public void onDataLoaded(List<FuelRecord> records) {
                fail("contains unexpected record list size:"+records.size());
            }

            @Override
            public void onDataNotAvailable() {
                return;//pass the test
            }
        });
    }

    //helper function to generate test Data
    private List<FuelRecord> initTestData() {
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
