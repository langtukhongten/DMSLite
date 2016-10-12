package com.vietdms.mobile.dmslauncher.Forms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.desmond.squarecamera.CameraActivity;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivitySurveyQaBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocalDB;
import CommonLib.Model;
import CommonLib.Status;
import CommonLib.SurveyCampaign;
import CommonLib.SurveyHeader;
import CommonLib.SurveyLine;
import CommonLib.SurveyResult;
import CommonLib.TrackingItem;
import CommonLib.TransactionLine;
import CommonLib.Utils;

public class SurveyQAActivity extends AppCompatActivity {
    private static final String TAG = "SurveyQAActivity";
    private static final int INTENT_CAMERA = 0;
    private static final int INTENT_LOCATION = 1;
    private ActivitySurveyQaBinding binding;
    private ArrayList<SurveyHeader> arrSurveyHeaders;
    private ArrayList<SurveyLine> arrSurveyLines;
    private SurveyCampaign selectedSurvey;
    private Handler handler;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventType.EventBase event = EventPool.view().deQueue();
            try {
                while (event != null) {
                    processEvent(event);
                    event = EventPool.view().deQueue();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //SystemLog.addLog(context, SystemLog.Type.Exception, ex.toString());
            }

        }
    };
    private boolean flagChange = false;
    private SurveyCampaign campaign;
    private ArrayList<SurveyLine> arrQuestion;
    private int nowID = 0;
    private int nowIdCustomer;
    private int nowRootCustomer;
    private RadioGroup nowRadioGroup;
    private Spinner nowSpinner;
    private ArrayList<Status> arrListId;
    private ArrayAdapter nowAdapter;
    private List<RadioGroup> arrRadioGroups;
    private List<Spinner> arrSpinners;
    private ArrayList<View> arrObjectRequireView;//mảng chứa đối tượng bắt buộc nhập
    private ArrayList<Integer> arrObjectRequireType;
    private ArrayList<CheckBox> arrCheckbox;//mảng câu hỏi checkbox
    private ArrayList<ArrayList<CheckBox>> arrayListArrayListCheckbox;//mảng lưu các mảng câu hỏi checkbox

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case SendSurveyData:
                EventType.EventSendSurveyDataResult eventSendSurveyDataResult = (EventType.EventSendSurveyDataResult) event;
                //Tạo giao dịch

                Home.nowTransactionLine.create_date = Model.getServerTime();
                Home.nowTransactionLine.modified_date = Model.getServerTime();
                Home.nowTransactionLine.id_ExtNo_ = campaign.id;
                Home.nowTransactionLine.id_transaction_define = Const.TransactionType.Survey.getValue();
                if (eventSendSurveyDataResult.success) {
                    MyMethod.showToast(this, getString(R.string.send_survey_success));
                    for (SurveyResult result : getResultData()) {
                        LocalDB.inst().addResult(result, 1);
                    }
                    //tra ve id_customer trong message
                    Home.nowTransactionLine.note = getString(R.string.survey) + "@" + nowRootCustomer + "@" + eventSendSurveyDataResult.message;
                 //Lưu offline đánh dấu là gửi rồi
                    LocalDB.inst().addTransactionLine(Home.nowTransactionLine, 1);
                    setResult(1);//Tra ve 1 de dong luon activity truoc
                    finish();
                } else {
                    //lay du lieu trong local
                    Home.nowTransactionLine.note = getString(R.string.survey) + "@" + nowRootCustomer + "@" + nowIdCustomer;
                    MyMethod.showToast(this, getString(R.string.no_connect_saved_local));
                    for (SurveyResult result : getResultData()) {
                        LocalDB.inst().addResult(result, 0);
                    }
                    //Lưu offline đánh dấu là chưa gửi
                    LocalDB.inst().addTransactionLine(Home.nowTransactionLine, 0);
                    setResult(1);//Tra ve 1 de dong luon activity truoc
                    finish();
                }
                binding.content.llSurvey.getChildAt(binding.content.llSurvey.getChildCount()-1).setEnabled(true);
                LayoutLoadingManager.Show_OffLoading(binding.loading);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_survey_qa);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        handler = new Handler();
        Intent intent = getIntent();
        campaign = intent.getExtras().getParcelable("Campaign");
        arrSurveyHeaders = intent.getExtras().getParcelableArrayList("Headers");
        arrSurveyLines = intent.getExtras().getParcelableArrayList("Lines");
        nowIdCustomer = intent.getIntExtra("IDCustomer", -1);
        nowRootCustomer = intent.getIntExtra("RootIDCustomer", -1);
        arrRadioGroups = new ArrayList<>();
        arrSpinners = new ArrayList<>();
        arrObjectRequireView = new ArrayList<>();
        arrObjectRequireType = new ArrayList<>();
        arrCheckbox = new ArrayList<>();
        arrayListArrayListCheckbox = new ArrayList<>();
        getId();
        drawView();
        event();

    }

    private void event() {
        binding.content.llSurvey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(view);
                return false;
            }
        });
    }

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private void drawView() {
        binding.content.title.setText(campaign.name);
        int i = 0;
        for (SurveyHeader header : arrSurveyHeaders) {//Vẽ giao diện từng câu hỏi
            if(header.requireField!=2)
            addHeaderView(header, i,60);
            arrQuestion = getQuestion(header.id);
            int j = 0;
            for (SurveyLine line : arrQuestion) {//Vẽ giao diện từng đáp án
                if(header.requireField!=2)
                addLineView(line, j, arrQuestion, header.requireField, pxToDp((int) getResources().getDimension(R.dimen.minHeightSurvey)));
                j++;
            }
            i++;
        }
        addFinishView(arrQuestion.get(0).status);//Vẽ giao diện nút gửi khảo sát
    }

    private  int pxToDp(int px)
    {
        return (int) (px / getResources().getSystem().getDisplayMetrics().density);
    }
    private void addFinishView(int status) {
        final Button sendButton = new Button(this);
        sendButton.setText(getString(R.string.send_survey));
        sendButton.setTextColor(Color.WHITE);
        sendButton.setEnabled(status == 1 ? false : true);
        sendButton.setBackgroundResource(R.drawable.button_note_drawable);
        sendButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Xử lí gửi khảo sát
                ArrayList<SurveyResult> results = getResultData();
                if (results != null) {
                    sendButton.setEnabled(false);
                    LayoutLoadingManager.Show_OnLoading(binding.loading, getString(R.string.sending), 30);
                    EventPool.control().enQueue(new EventType.EventSendSurveyDataRequest(results, nowRootCustomer));
                } else {
                    MyMethod.showToast(getApplicationContext(), getString(R.string.result_null));
                }
            }
        });
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        view.setMinimumHeight(100);
        binding.content.llSurvey.addView(sendButton);
        binding.content.llSurvey.addView(view);
    }

    private ArrayList<SurveyResult> getResultData() {

        //Check condition require field
        if (requireFieldOK()) {
            //check radio group size
            if (arrRadioGroups.size() > 0) {
                for (RadioGroup radioGroup : arrRadioGroups) {
                    int id = radioGroup.getCheckedRadioButtonId();
                    saveResult(id, "1");
                }
            }
            //check spinner
            if (arrSpinners.size() > 0) {
                for (Spinner spinner : arrSpinners) {
                    int id = ((Status) spinner.getSelectedItem()).id;
                    if (id != 0)
                        //Loại bỏ trường hợp chưa chọn
                        saveResult(id, "1");
                }
            }
            ArrayList<SurveyResult> results = new ArrayList<>(arrSurveyLines.size());
            for (SurveyLine line : arrSurveyLines)
                if (line.type == 1 && line.result.trim().isEmpty()) {//Nếu là radio và không chọn thì bỏ qua
                } else if (line.type == 0 && line.result.contains("0")) {//Nếu là checkbox và không chọn thì bỏ qua
                } else if (line.type == 8 && line.result.trim().isEmpty()) {
                } else {
                    SurveyResult result = new SurveyResult();
                    result.id_customer = nowIdCustomer;
                    result.answer = line.result;
                    result.id_survey_line = line.id;
                    result.status = 0;
                    if (result.answer.contains(".jpg")) {
                        BitmapFactory.Options options;
                        try {
                            options = new BitmapFactory.Options();
                            options.inSampleSize = 16;// 1/16 of origin image size from width and height
                            result.imageThumb = BitmapFactory.decodeFile(result.answer, options);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    results.add(result);
                }
            return results;
        } else {
            return null;
        }
    }

    private boolean requireFieldOK() {
        if (arrObjectRequireView.size() > 0) {
            boolean flagRequire = false;
            int position = 0;
            for (View view : arrObjectRequireView) {
                switch (arrObjectRequireType.get(position)) {
                    case 0:
                        CheckBox checkBox = (CheckBox) view;
                        if (!getResultCheckbox(checkBox.getId())) {
                            flagRequire = true;
                            checkBox.setError(getString(R.string.require_please_input));
                        } else {
                            checkBox.setError(null);
                        }
                        break;
                    case 1:
                        RadioGroup radioGroup = (RadioGroup) view;
                        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(0);
                        if (radioGroup.getCheckedRadioButtonId() <= 0) {
                            flagRequire = true;
                            radioButton.setError(getString(R.string.require_please_input));
                        } else {
                            radioButton.setError(null);
                        }
                        break;
                    case 2:
                        EditText editText = (EditText) view;
                        if (getResult(view.getId()).trim().isEmpty()) {
                            flagRequire = true;
                            editText.setError(getString(R.string.require_please_input));
                        } else {
                            editText.setError(null);
                        }
                        break;
                    case 3:
                        Button camera = (Button) view;
                        if (getResult(view.getId()).trim().isEmpty()) {
                            flagRequire = true;
                            camera.requestFocus();
                            camera.setError(getString(R.string.require_please_input));
                        } else {
                            camera.setError(null);
                        }
                        break;
                    case 4:
                        Button location = (Button) view;
                        if (getResult(view.getId()).trim().isEmpty()) {
                            flagRequire = true;
                            location.setError(getString(R.string.require_please_input));
                        } else {
                            location.setError(null);
                        }
                        break;
                    case 7:
                        EditText numberText = (EditText) view;
                        if (getResult(view.getId()).trim().isEmpty()) {
                            flagRequire = true;
                            numberText.setError(getString(R.string.require_please_input));
                        } else {
                            numberText.setError(null);
                        }
                        break;
                    case 8:
                        Spinner spinner = (Spinner) view;
                        TextView textSpinner = (TextView) spinner.getSelectedView();
                        if (((Status) spinner.getSelectedItem()).id == 0) {
                            flagRequire = true;
                            textSpinner.setError(getString(R.string.require_please_input));
                        } else {
                            textSpinner.setError(null);
                        }
                        break;
                    case 9:
                        Button dateTime = (Button) view;
                        if (getResult(view.getId()).trim().isEmpty()) {
                            flagRequire = true;
                            dateTime.setError(getString(R.string.require_please_input));
                        } else {
                            dateTime.setError(null);
                        }
                        break;


                }
                position++;
            }
            return !flagRequire;
        } else {
            return true;
        }
    }

    private boolean getResultCheckbox(int id) {//Kiểm tra list checkbox đã check hay chưa
        ArrayList<CheckBox> temp = new ArrayList<>();//Mảng checbox cần kiểm tra
        if (arrayListArrayListCheckbox.size() > 0) {
            for (ArrayList<CheckBox> checkBoxes : arrayListArrayListCheckbox) {
                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.getId() == id) {
                        temp = checkBoxes;
                    }
                }
            }
            boolean flagChecked = false;
            for (CheckBox checkBox : temp) {
                if (checkBox.isChecked()) flagChecked = true;
            }
            return flagChecked;
        } else return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addLineView(final SurveyLine line, final int position, final ArrayList<SurveyLine> arrQuestion, final int requireField, final int minHeight) {
        if (arrCheckbox.size() > 0 && line.type != 0) {//Nếu còn checkbox chưa xử lí
            arrayListArrayListCheckbox.add(arrCheckbox);
            arrCheckbox = new ArrayList<>();
        }
        switch (line.type) {
            //Vẽ giao diện theo từng loại đáp án
            case 0://Checkbox
                View lastviewChecbox = binding.content.llSurvey.getChildAt(binding.content.llSurvey.getChildCount() - 1);
                if (lastviewChecbox instanceof CheckBox) {//neu truoc do la checkbox
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setId(line.id);
                    checkBox.setText(line.question);
                    checkBox.setMinimumHeight(minHeight);
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    checkBox.setChecked(line.result.contains("1") ? true : false);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            arrQuestion.get(position).result = isChecked ? "1" : "0";
                            saveResult(line.id, isChecked ? "1" : "0");
                        }
                    });
                    binding.content.llSurvey.addView(checkBox);
                    if (requireField == 1) {
                        arrCheckbox.add(checkBox);
                        arrObjectRequireView.add(checkBox);
                        arrObjectRequireType.add(line.type);
                    }
                } else {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setId(line.id);
                    checkBox.setText(line.question);
                    checkBox.setMinimumHeight(minHeight);
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    checkBox.setChecked(line.result.contains("1") ? true : false);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            arrQuestion.get(position).result = isChecked ? "1" : "0";
                            saveResult(line.id, isChecked ? "1" : "0");
                        }
                    });
                    arrQuestion.get(position).result = checkBox.isChecked() ? "1" : "0";
                    //saveResult(line.id, checkBox.isChecked() ? "1" : "0");
                    binding.content.llSurvey.addView(checkBox);
                    if (requireField == 1) {
                        arrCheckbox.add(checkBox);
                        arrObjectRequireView.add(checkBox);
                        arrObjectRequireType.add(line.type);
                        arrayListArrayListCheckbox.add(arrCheckbox);
                    }
                }
                break;
            case 1://Radio
                //if before view is a radio
                View lastview = binding.content.llSurvey.getChildAt(binding.content.llSurvey.getChildCount() - 1);
                if (lastview instanceof RadioGroup) {//neu truoc do la radio button
                    final RadioButton radioButton = new RadioButton(this);
                    radioButton.setId(line.id);
                    radioButton.setText(line.question);
                    radioButton.setMinimumHeight(minHeight);
                    radioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setChecked(line.result.contains("1") ? true : false);
                    nowRadioGroup.addView(radioButton);
                } else {
                    //tao radio group
                    RadioGroup radioGroup = new RadioGroup(this);
                    nowRadioGroup = radioGroup;
                    arrRadioGroups.add(nowRadioGroup);
                    radioGroup.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    radioGroup.setMinimumHeight(minHeight);
                    final RadioButton radioButton = new RadioButton(this);
                    radioButton.setId(line.id);
                    radioButton.setText(line.question);
                    radioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setChecked(line.result.contains("1") ? true : false);
                    nowRadioGroup.addView(radioButton);
                    binding.content.llSurvey.addView(nowRadioGroup);
                    if (requireField == 1) {
                        arrObjectRequireView.add(nowRadioGroup);
                        arrObjectRequireType.add(line.type);
                    }
                }
                break;
            case 2://EditText
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                TextView labelEditText = new TextView(this);
                labelEditText.setText(line.question);
                labelEditText.setFocusable(true);
                labelEditText.setMinimumHeight(minHeight);
                labelEditText.setFocusableInTouchMode(true);
                labelEditText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                ll.addView(labelEditText);
                final EditText editText = new EditText(this);
                editText.setId(line.id);
                editText.setMinimumHeight(minHeight);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3));
                editText.setText(line.result);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.toString().trim().isEmpty() && requireField == 1) {
                            editText.setError(getString(R.string.require_please_input));
                        } else {
                            arrQuestion.get(position).result = editable.toString();
                            saveResult(line.id, editable.toString());
                        }
                    }
                });
                ll.addView(editText);
                arrQuestion.get(position).result = editText.getText().toString();
                //saveResult(line.id, editText.getText().toString());
                binding.content.llSurvey.addView(ll);
                if (requireField == 1) {
                    arrObjectRequireView.add(editText);
                    arrObjectRequireType.add(line.type);
                }
                break;
            case 3://camera
                LinearLayout llCamera = new LinearLayout(this);
                llCamera.setOrientation(LinearLayout.VERTICAL);
                llCamera.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ImageView imageCamera = new ImageView(this);
                imageCamera.setMinimumHeight(200);
                imageCamera.setMinimumWidth(200);
                imageCamera.setId(Integer.parseInt(line.id + "999"));
                imageCamera.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Glide.with(this).load(MyMethod.getUrlSurveyImage(line.result)).override(200, 200).error(R.drawable.report_btn).centerCrop().into(imageCamera);
                llCamera.addView(imageCamera);
                Button camera = new Button(this);
                camera.setId(line.id);
                camera.setMinimumHeight(minHeight);
                camera.setText(getString(R.string.take_picture));
                camera.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                        nowID = line.id;
                        startActivityForResult(cameraIntent, INTENT_CAMERA);
                    }
                });
                llCamera.addView(camera);
                binding.content.llSurvey.addView(llCamera);
                if (requireField == 1) {
                    arrObjectRequireView.add(camera);
                    arrObjectRequireType.add(line.type);
                }
                break;
            case 4://location
                LinearLayout llLocation = new LinearLayout(this);
                llLocation.setOrientation(LinearLayout.VERTICAL);
                llLocation.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                TextView textLocation = new TextView(this);
                textLocation.setId(Integer.parseInt(line.id + "999"));
                textLocation.setMinimumHeight(minHeight);
                textLocation.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textLocation.setText(line.result.isEmpty() ? getString(R.string.location_none) : line.result);
                llLocation.addView(textLocation);
                Button location = new Button(this);
                location.setId(line.id);
                location.setMinimumHeight(minHeight);
                location.setText(getString(R.string.get_location));
                location.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent locationIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        nowID = line.id;
                        startActivityForResult(locationIntent, INTENT_LOCATION);
                    }
                });
                llLocation.addView(location);
                binding.content.llSurvey.addView(llLocation);
                if (requireField == 1) {
                    arrObjectRequireView.add(location);
                    arrObjectRequireType.add(line.type);
                }
                break;
            case 5://imageview
                ImageView imageView = new ImageView(this);
                imageView.setMinimumHeight(200);
                imageView.setMinimumWidth(200);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setId(line.id);
                Glide.with(this).load(MyMethod.getUrlSurveyImage(line.result)).override(200, 200).error(R.drawable.image_btn).centerCrop().into(imageView);
                binding.content.llSurvey.addView(imageView);
                break;
            case 6://videoview
                VideoView videoView = new VideoView(this);
                videoView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                videoView.setMinimumHeight(minHeight);
                binding.content.llSurvey.addView(videoView);
                break;
            case 7:
                LinearLayout llNumberText = new LinearLayout(this);
                llNumberText.setOrientation(LinearLayout.HORIZONTAL);
                llNumberText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                TextView labelNunmberText = new TextView(this);
                labelNunmberText.setText(line.question);
                labelNunmberText.setFocusable(true);
                labelNunmberText.setMinimumHeight(minHeight);
                labelNunmberText.setFocusableInTouchMode(true);
                labelNunmberText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                llNumberText.addView(labelNunmberText);
                EditText numberText = new EditText(this);
                numberText.setId(line.id);
                numberText.setText(line.result);
                numberText.setMinimumHeight(minHeight);
                numberText.setInputType(InputType.TYPE_CLASS_NUMBER);
                numberText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3));
                numberText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        arrQuestion.get(position).result = editable.toString();
                        saveResult(line.id, editable.toString());
                    }
                });
                llNumberText.addView(numberText);
                arrQuestion.get(position).result = numberText.getText().toString();
                //saveResult(line.id, numberText.getText().toString());
                binding.content.llSurvey.addView(llNumberText);
                if (requireField == 1) {
                    arrObjectRequireView.add(numberText);
                    arrObjectRequireType.add(line.type);
                }
                break;
            case 8:
                //spinner
                View lastviewSpinner = binding.content.llSurvey.getChildAt(binding.content.llSurvey.getChildCount() - 1);
                if (lastviewSpinner instanceof Spinner) {//neu truoc do la spinner
                    arrListId.add(new Status(line.id, line.question));
                    if (line.result.contains("1"))
                        nowSpinner.setSelection(arrListId.size() - 1);
                    nowAdapter.notifyDataSetChanged();
                } else {
                    //tao radio group
                    Spinner spinner = new Spinner(this);
                    spinner.setMinimumHeight(minHeight);
                    arrListId = new ArrayList<>();
                    arrListId.add(new Status(0, getString(R.string.please_choice)));
                    arrListId.add(new Status(line.id, line.question));
                    nowAdapter = new ArrayAdapter(this,
                            android.R.layout.simple_spinner_item, arrListId
                    );
                    nowAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                    nowSpinner = spinner;
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    param.setMargins(0, 10, 0, 10);
                    spinner.setLayoutParams(param);
                    if (line.result.contains("1"))
                        spinner.setSelection(arrListId.size() - 1);
                    spinner.setPadding(0, 5, 0, 5);
                    spinner.setAdapter(nowAdapter);
                    arrSpinners.add(nowSpinner);
                    binding.content.llSurvey.addView(nowSpinner);
                    if (requireField == 1) {
                        arrObjectRequireView.add(nowSpinner);
                        arrObjectRequireType.add(line.type);
                    }

                }
                break;
            case 9://Date time
                LinearLayout llDateTime = new LinearLayout(this);
                llDateTime.setOrientation(LinearLayout.VERTICAL);
                llDateTime.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                final TextView textDateTime = new TextView(this);
                textDateTime.setId(Integer.parseInt(line.id + "999"));
                textDateTime.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textDateTime.setText(line.question);
                textDateTime.setMinimumHeight(minHeight);
                llDateTime.addView(textDateTime);
                final Calendar myCalendar = Calendar.getInstance();
                final Button dateTime = new Button(this);
                dateTime.setId(line.id);
                dateTime.setMinimumHeight(minHeight);
                dateTime.setText(line.result.trim().isEmpty() ? getString(R.string.please_choice) : line.result);
                dateTime.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                dateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(SurveyQAActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String shFormat = "dd/MM/yyyy"; //định dạng hiển thị
                                SimpleDateFormat showFormat = new SimpleDateFormat(shFormat, Locale.US);
                                dateTime.setText(showFormat.format(myCalendar.getTime()));
                                String saFormat = "yyyy-MM-dd"; //định dạng lưu trữ
                                SimpleDateFormat saveFormat = new SimpleDateFormat(saFormat, Locale.US);
                                saveResult(line.id, saveFormat.format(myCalendar.getTime()));
                            }
                        }, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                llDateTime.addView(dateTime);
                binding.content.llSurvey.addView(llDateTime);
                if (requireField == 1) {
                    arrObjectRequireView.add(dateTime);
                    arrObjectRequireType.add(line.type);
                }
                break;
            default:
                break;
        }
    }

    private void addHeaderView(SurveyHeader header, int position,int minHeight) {
        TextView tv = new TextView(this);
        tv.setText(position + "." + header.name);
        tv.setGravity(Gravity.LEFT);
        tv.setMinimumHeight(minHeight);
        tv.setPadding(0,(int)getResources().getDimension(R.dimen.margin_button_size),0,0);
        tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View v = new View(this);
        v.setBackgroundColor(Color.BLACK);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        binding.content.llSurvey.addView(tv);
        binding.content.llSurvey.addView(v);
    }

    private ArrayList<SurveyLine> getQuestion(int id) {
        ArrayList<SurveyLine> result = new ArrayList<>();
        for (SurveyLine line : arrSurveyLines) {
            if (line.id_survey == id) {
                result.add(line);
            }
        }
        return result;
    }

    private void getId() {
        setSupportActionBar(binding.toolbar);
        //instance object
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            //Xử lí kết quả trả về
            switch (requestCode) {
                case INTENT_CAMERA:
                    Uri photoUri = data.getData();
                    saveResult(nowID, photoUri.getPath());
                    ImageView imageView = ((ImageView) findViewById(Integer.parseInt(nowID + "999")));
                    ((Button) findViewById(nowID)).setError(null);
                    Glide.with(this).load(photoUri).into(imageView);
                    break;
                case INTENT_LOCATION:
                    Location location = data.getParcelableExtra("Location");
                    String address = data.getStringExtra("Address");
                    saveResult(nowID, location.getLatitude() + "," + location.getLongitude());
                    ((TextView) findViewById(Integer.parseInt(nowID + "999"))).setText(location.getLatitude() + "," + location.getLongitude());
                    ((Button) findViewById(nowID)).setError(null);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getResult(int id) {
        //Lưu và cập nhật đáp án
        int i = 0;
        for (SurveyLine line : arrSurveyLines) {

            if (line.id == id) {
                return arrSurveyLines.get(i).result;
            }
            i++;
        }
        return "";
    }

    private void saveResult(int id, String path) {
        //Lưu và cập nhật đáp án
        flagChange = true;
        int i = 0;
        for (SurveyLine line : arrSurveyLines) {

            if (line.id == id) {
                arrSurveyLines.get(i).result = path;
            }
            i++;
        }
    }

    @Override
    public void onBackPressed() {
        //Kiểm tra phiếu khảo sát có thay đổi hay không
        if (flagChange) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.confirm_out))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setNeutralButton(getString(R.string.send_survey_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            ArrayList<SurveyResult> results = getResultData();
                            if (results != null) {
                                LayoutLoadingManager.Show_OnLoading(binding.loading, getString(R.string.sending), 30);
                                EventPool.control().enQueue(new EventType.EventSendSurveyDataRequest(results, nowRootCustomer));
                            }
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        Log.w(TAG, "onResume");
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
    }

    @Override
    protected void onPause() {
        Log.w(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG, "onStop");
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            //unregisterReceiver(receiver);
            Log.w(TAG, "unregisterReceiver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
