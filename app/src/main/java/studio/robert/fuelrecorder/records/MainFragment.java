package studio.robert.fuelrecorder.records;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

import studio.robert.fuelrecorder.R;
import studio.robert.fuelrecorder.data.FuelRecord;

/**
 * Created by robert on 2017/7/16.
 */

public class MainFragment extends Fragment implements RecordsContract.View {

    private RecordsContract.Presenter mPresenter;
    private MyAdaptor mAdaptor;

    public MainFragment() {
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

    private class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.ViewHolder> {
        private List<FuelRecord> mRecords;
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final FuelRecord record = mRecords.get(position);
            holder.mTVDate.setText(record.getDate().toString());
            holder.mTVPrice.setText(String.valueOf(record.getPrice()));
            holder.mTVAmount.setText(String.valueOf(record.getAmount()));
            holder.mTVMileage.setText(String.valueOf(record.getMileage()));
            holder.mTVNote.setText(record.getNote());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Item "+position+" | "+record, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRecords.size();
        }

        public MyAdaptor(@NotNull List<FuelRecord> records) {
            mRecords = records;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView mTVDate;
            public TextView mTVAmount;
            public TextView mTVPrice;
            public TextView mTVNote;
            public TextView mTVMileage;
            public ViewHolder(View itemView) {
                super(itemView);
                mTVDate = itemView.findViewById(R.id.text_date);
                mTVAmount = itemView.findViewById(R.id.text_amount);
                mTVPrice = itemView.findViewById(R.id.text_price);
                mTVMileage = itemView.findViewById(R.id.text_mileage);
                mTVNote = itemView.findViewById(R.id.text_note);
            }
        }
    }

}
