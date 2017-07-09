package studio.robert.fuelrecorder.data;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

import static studio.robert.fuelrecorder.utils.Preconditions.checkNotNull;

/**
 * Created by robert on 2017/5/26.
 */

public class FuelRecord {
    @NotNull
    private final String mId;

    @NotNull
    private long mDateTimeStamp;

    @NotNull
    private double mAmount;

    @NotNull
    private double mPrice;

    @NotNull
    private double mMileAge;

    private String mNote;

    public FuelRecord(@NotNull long timestamp, @NotNull double amount, @NotNull double price, @NotNull double mileage, String note) {
        this(UUID.randomUUID().toString(), timestamp, amount, price, mileage, note);
    }

    public FuelRecord(@NotNull String id, @NotNull long timestamp, @NotNull double amount, @NotNull double price, @NotNull double mileAge, String note) {
        this.mId = id;
        this.mDateTimeStamp = timestamp;
        this.mAmount = amount;
        this.mPrice = price;
        this.mMileAge = mileAge;
        this.mNote = note;
    }

    @NotNull
    public String getId() {return mId;}

    @NotNull
    public Date getDate() {return new Date(mDateTimeStamp);}

    @NotNull
    public long getDateTimeStamp() {return mDateTimeStamp;}

    @NotNull
    public double getAmount() {return mAmount;}

    @NotNull
    public double getPrice() {return mPrice;}

    @NotNull
    public double getMileage() {return mMileAge;}

    public String getNote() {return mNote;}

    public void setTimeStamp(@NotNull long timestamp) {
        checkNotNull(timestamp);
        mDateTimeStamp = timestamp;
    }

    public void setAmount(@NotNull double amount) {
        checkNotNull(amount);
        mAmount = amount;
    }

    public void setPrice(@NotNull double price) {
        checkNotNull(price);
        mPrice = price;
    }

    public void setMileAge(@NotNull double mileage) {
        checkNotNull(mileage);
        mMileAge = mileage;
    }

    public void setNote(String note) {
        mNote = note;
    }

    @Override
    public boolean equals(Object obj) {
        //return super.equals(obj);
        if (!(obj instanceof FuelRecord)) return false;
        FuelRecord record = (FuelRecord) obj;
        return (record.getId() == mId) &&
                (record.getDateTimeStamp() == mDateTimeStamp) &&
                (record.getAmount() == mAmount) &&
                (record.getMileage() == mMileAge) &&
                (record.getPrice() == mPrice) &&
                (record.getNote().equals(mNote));
    }
}
