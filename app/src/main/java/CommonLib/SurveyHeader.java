package CommonLib;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Admin on 8/23/2016.
 */
public class SurveyHeader implements Parcelable {
    private static final String TAG = "SurveyHeader";
    public int id;
    public int sort;
    public String name;
    public String description;
    public int type;
    public int id_campaign;
    public int status;
    public int requireField;

    public SurveyHeader() {

    }

    protected SurveyHeader(Parcel in) {
        id = in.readInt();
        sort = in.readInt();
        name = in.readString();
        description = in.readString();
        type = in.readInt();
        id_campaign = in.readInt();
        status = in.readInt();
        requireField = in.readInt();
    }

    public static final Creator<SurveyHeader> CREATOR = new Creator<SurveyHeader>() {
        @Override
        public SurveyHeader createFromParcel(Parcel in) {
            return new SurveyHeader(in);
        }

        @Override
        public SurveyHeader[] newArray(int size) {
            return new SurveyHeader[size];
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
        parcel.writeInt(sort);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeInt(type);
        parcel.writeInt(id_campaign);
        parcel.writeInt(status);
        parcel.writeInt(requireField);
    }
}
