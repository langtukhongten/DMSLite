package CommonLib;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Admin on 8/23/2016.
 */
public class SurveyCampaign implements Parcelable {
    private static final String TAG = "SurveyCampaign";
    public int id;
    public String no_;
    public String name;
    public long documentDate;
    public long beginDate;
    public long endDate;
    public String description;

    public SurveyCampaign() {
    }

    protected SurveyCampaign(Parcel in) {
        id = in.readInt();
        no_ = in.readString();
        name = in.readString();
        documentDate = in.readLong();
        beginDate = in.readLong();
        endDate = in.readLong();
        description = in.readString();
    }

    public static final Creator<SurveyCampaign> CREATOR = new Creator<SurveyCampaign>() {
        @Override
        public SurveyCampaign createFromParcel(Parcel in) {
            return new SurveyCampaign(in);
        }

        @Override
        public SurveyCampaign[] newArray(int size) {
            return new SurveyCampaign[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        Log.v(TAG, "writeToParcel..." + flags);
        parcel.writeInt(id);
        parcel.writeString(no_);
        parcel.writeString(name);
        parcel.writeLong(documentDate);
        parcel.writeLong(beginDate);
        parcel.writeLong(endDate);
        parcel.writeString(description);
    }
}
