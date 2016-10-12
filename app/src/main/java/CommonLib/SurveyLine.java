package CommonLib;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Admin on 8/23/2016.
 */
public class SurveyLine implements Parcelable {
    private static final String TAG = "SurveyLine";
    public int id;
    public int id_survey;
    public String refNo_;
    public int sort;
    public String question;
    public int type;
    public String result;
    public int status;

    public SurveyLine() {
    }

    protected SurveyLine(Parcel in) {
        id = in.readInt();
        id_survey = in.readInt();
        refNo_ = in.readString();
        sort = in.readInt();
        question = in.readString();
        type = in.readInt();
        result = in.readString();
        status = in.readInt();
    }

    public static final Creator<SurveyLine> CREATOR = new Creator<SurveyLine>() {
        @Override
        public SurveyLine createFromParcel(Parcel in) {
            return new SurveyLine(in);
        }

        @Override
        public SurveyLine[] newArray(int size) {
            return new SurveyLine[size];
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
        parcel.writeInt(id_survey);
        parcel.writeString(refNo_);
        parcel.writeInt(sort);
        parcel.writeString(question);
        parcel.writeInt(type);
        parcel.writeString(result);
        parcel.writeInt(status);
    }
}
