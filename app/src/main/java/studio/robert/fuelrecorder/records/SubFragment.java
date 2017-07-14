package studio.robert.fuelrecorder.records;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import studio.robert.fuelrecorder.R;
import studio.robert.fuelrecorder.data.FuelRecord;

import static studio.robert.fuelrecorder.utils.Preconditions.checkNotNull;

/**
 * Created by robert on 2017/7/16.
 */

public class SubFragment extends Fragment implements RecordsContract.View {

    private static final String TAG = "Records.SubFragment";
    private RecordsContract.Presenter mPresenter;

    public SubFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(RecordsContract.Presenter presenter) {
        checkNotNull(presenter);
        mPresenter = presenter;
    }

    @Override
    public void showResult(List<FuelRecord> records) {
        if (records.size() == 1) {
            return;
        }
        Collections.sort(records, new Comparator<FuelRecord>(){
            @Override
            public int compare(FuelRecord o1, FuelRecord o2) {
                return (int) (o1.getDateTimeStamp() - o2.getDateTimeStamp());
            }
        });
        double currentMileage = records.get(records.size()-1).getMileage();
        double startMileage = records.get(0).getMileage();
        double totalFuel = -(records.get(records.size()-1).getAmount());
        for (FuelRecord record:records) {
            totalFuel+=record.getAmount();
        }
        double avg = (currentMileage-startMileage)/totalFuel;
        NumberFormat formater = NumberFormat.getInstance();
        formater.setMaximumFractionDigits(2);
        ((TextView)getActivity().findViewById(R.id.avg_result)).setText(String.valueOf(formater.format(avg)));
    }

    @Override
    public void showNoRecords() {
        TextView tvAvg = (TextView)getActivity().findViewById(R.id.avg_result);
        tvAvg.setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordEditor(final FuelRecord record, final boolean isNewRecord) {
        final View editor = LayoutInflater.from(getContext()).inflate(R.layout.editor, null);
        final Calendar current = Calendar.getInstance();
        if (!isNewRecord){
            long timestamp = record.getDateTimeStamp();
            current.setTimeInMillis(timestamp);
        }

        //defaul date
        ((EditText) editor.findViewById(R.id.edit_year)).setText(String.valueOf(current.get(Calendar.YEAR)));
        ((EditText) editor.findViewById(R.id.edit_month)).setText(String.valueOf(current.get(Calendar.MONTH)+1));
        ((EditText) editor.findViewById(R.id.edit_date)).setText(String.valueOf(current.get(Calendar.DAY_OF_MONTH)));
        editor.findViewById(R.id.btn_set_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, current.get(Calendar.YEAR)+"|"+current.get(Calendar.MONTH)+"|"+current.get(Calendar.DAY_OF_MONTH)+"|"+current.toString());
                new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        EditText etYr = (EditText) editor.findViewById(R.id.edit_year);
                        etYr.setText(String.valueOf(year));
                        EditText etMn = (EditText) editor.findViewById(R.id.edit_month);
                        etMn.setText(String.valueOf(month+1));
                        EditText etDm = (EditText) editor.findViewById(R.id.edit_date);
                        etDm.setText(String.valueOf(dayOfMonth));
                    }
                }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        if (!isNewRecord) {
            ((EditText) editor.findViewById(R.id.edit_price)).setText(String.valueOf(record.getPrice()));
            ((EditText) editor.findViewById(R.id.edit_amount)).setText(String.valueOf(record.getAmount()));
            ((EditText) editor.findViewById(R.id.edit_mileage)).setText(String.valueOf(record.getMileage()));
            ((EditText) editor.findViewById(R.id.edit_note)).setText(String.valueOf(record.getNote()));
        }
        AlertDialog.Builder AdBuilder = new AlertDialog.Builder(this.getContext())
                .setTitle("Editor")
                //.setMessage("test2")
                .setView(editor)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = Integer.valueOf(((EditText) editor.findViewById(R.id.edit_year)).getText().toString());
                        int month = Integer.valueOf(((EditText) editor.findViewById(R.id.edit_month)).getText().toString())-1;//month display issue
                        int day = Integer.valueOf(((EditText) editor.findViewById(R.id.edit_date)).getText().toString());
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        long timestamp = c.getTimeInMillis();
                        String etPrice = ((EditText)editor.findViewById(R.id.edit_price)).getText().toString();
                        String etAmount = ((EditText)editor.findViewById(R.id.edit_amount)).getText().toString();
                        String etMileage = ((EditText)editor.findViewById(R.id.edit_mileage)).getText().toString();
                        if (("").matches(etPrice) || ("").matches(etAmount) || ("").matches(etMileage)) {
                            Toast.makeText(getContext(), getString(R.string.failed_to_record_empty_field), Toast.LENGTH_LONG).show();
                            return;
                        };
                        double price = Double.valueOf(etPrice);
                        double amount = Double.valueOf(etAmount);
                        double mileage = Double.valueOf(etMileage);
                        String note = ((EditText)editor.findViewById(R.id.edit_note)).getText().toString();
                        record.setTimeStamp(timestamp);
                        record.setPrice(price);
                        record.setAmount(amount);
                        record.setMileAge(mileage);
                        record.setNote(note);
                        Log.d(TAG, record.toString());
                        if (isNewRecord)
                            mPresenter.saveNewRecord(record);
                        else
                            mPresenter.saveExistRecord(record);
                        //refresh the list
                        mPresenter.loadRecords();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });
        if (!isNewRecord) {
            AdBuilder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPresenter.deleteConfrim(record);
                }
            });
        }
        AdBuilder.show();
    }
}
