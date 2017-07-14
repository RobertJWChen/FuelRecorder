package studio.robert.fuelrecorder.records;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;

import studio.robert.fuelrecorder.R;
import studio.robert.fuelrecorder.data.FuelRecord;

import static studio.robert.fuelrecorder.utils.Preconditions.checkNotNull;

/**
 * Created by robert on 2017/7/16.
 */

public class MainFragment extends Fragment implements RecordsContract.View {

    private RecordsContract.Presenter mPresenter;
    private MyAdaptor mAdaptor;
    private RecyclerView mRecyclerView;

    public MainFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_main, container, false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NotNull RecordsContract.Presenter presenter) {
        checkNotNull(presenter);
        mPresenter = presenter;
    }

    @Override
    public void showResult(List<FuelRecord> records) {
        //TODO: recycle view
        mAdaptor = new MyAdaptor(records);
        mRecyclerView = (RecyclerView) (this.getActivity().findViewById(R.id.main_frame)).findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdaptor);
    }

    @Override
    public void showNoRecords() {
        //TODO:no records text
        Toast.makeText(getContext(), "No Data!", Toast.LENGTH_SHORT).show();
    }

    public void confirmDelete(@NotNull final FuelRecord record) {
        new AlertDialog.Builder(getContext()).
                setTitle(R.string.delete_confirm).
                setMessage(record.getDate()+"|"+record.getAmount()+"|"+record.getNote()).
                setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), getString(R.string.delete_msg)+">"+record.getId(),Toast.LENGTH_LONG).show();
                        mPresenter.deleteRecord(record);
                    }
                }).
                setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing;
                    }
                }).show();
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
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(record.getDateTimeStamp());
            holder.mTVDate.setText(c.get(Calendar.YEAR)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH));
            holder.mTVPrice.setText(String.valueOf(record.getPrice()));
            holder.mTVAmount.setText(String.valueOf(record.getAmount()));
            holder.mTVMileage.setText(String.valueOf(record.getMileage()));
            holder.mTVNote.setText(record.getNote());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(), "Item "+position+" | "+record, Toast.LENGTH_LONG).show();
                    mPresenter.updateRecord(record);
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
                mTVDate = (TextView) itemView.findViewById(R.id.text_date);
                mTVAmount = (TextView) itemView.findViewById(R.id.text_amount);
                mTVPrice = (TextView) itemView.findViewById(R.id.text_price);
                mTVMileage = (TextView) itemView.findViewById(R.id.text_mileage);
                mTVNote = (TextView) itemView.findViewById(R.id.text_note);
            }
        }
    }

}
