//package com.vietdms.mobile.dmslauncher.CustomAdapter;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.databinding.DataBindingUtil;
//import android.text.Editable;
//import android.text.InputType;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.CompoundButton;
//
//import com.bumptech.glide.Glide;
//import com.desmond.squarecamera.CameraActivity;
//import com.vietdms.mobile.dmslauncher.Forms.MapsActivity;
//import com.vietdms.mobile.dmslauncher.Forms.SurveyQAActivity;
//import com.vietdms.mobile.dmslauncher.MyMethod;
//import com.vietdms.mobile.dmslauncher.R;
//import com.vietdms.mobile.dmslauncher.databinding.RowSurveyBinding;
//
//import java.util.ArrayList;
//
//import CommonLib.SurveyLine;
//
///**
// * Created by Admin on 8/24/2016.
// */
//public class SurveyAdapter extends ArrayAdapter<SurveyLine> {
//    private Context mContext;
//    private ArrayList<SurveyLine> surveyLines;
//    private LayoutInflater mLayoutInflater;
//    private RowSurveyBinding binding;
//
//    public SurveyAdapter(Context context, ArrayList<SurveyLine> surveyLines) {
//        super(context, 0, surveyLines);
//        mContext = context;
//        this.surveyLines = surveyLines;
//        mLayoutInflater = LayoutInflater.from(context);
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        //get song object at position
//        final SurveyLine line = surveyLines.get(position);
//        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_survey, parent, false);
//        convertView = binding.getRoot();
//        //inflat view from row_song.xml
//        //set data
//        switch (line.type) {
//            case 0:
//                MyMethod.setVisible(binding.checkBox);
//                binding.checkBox.setText(line.question);
//                binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
//                        surveyLines.get(position).result = ischecked ? "1" : "0";
//                    }
//                });
//                if (!line.result.isEmpty()) {//Xử lí nếu có kết quả trước
//                    binding.checkBox.setChecked(line.result.contains("1") ? true : false);
//                }
//                surveyLines.get(position).result = binding.checkBox.isChecked() ? "1" : "0";
//                break;
//            case 1:
//                MyMethod.setVisible(binding.radio);
//                binding.radio.setText(line.question);
//
//                if (!line.result.isEmpty()) {
//                    binding.radio.setChecked(line.result.contains("1") ? true : false);
//                }
//                binding.radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                        line.result = isChecked ? "1" : "0";
//                        for (int j = 0; j < surveyLines.size(); j++) {
//                            if (position != j) {
//                                surveyLines.get(j).result = "0";
//                            }
//                        }
//                        notifyDataSetChanged();
//                    }
//                });
//                binding.radio.setChecked(line.result.contains("1") ? true : false);
//                break;
//            case 2:
//                MyMethod.setVisible(binding.formEditText);
//                binding.labelEditText.setText(line.question);
//                binding.editText.setInputType(InputType.TYPE_CLASS_TEXT);
//                binding.editText.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//                        surveyLines.get(position).result = editable.toString();
//                        // saveToList(surveyLines.get(i));
//                    }
//                });
//                binding.editText.setText(line.result);
//                surveyLines.get(position).result = binding.editText.getText().toString();
//                //saveToList(surveyLines.get(i));
//                break;
//            case 3:
//                MyMethod.setVisible(binding.formCamera);
//                binding.camera.setText(getContext().getString(R.string.take_picture));
//                binding.camera.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        SurveyQAActivity.nowPosition = position;
////                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                        File fileDir = new File(Environment.getExternalStorageDirectory()
////                                + "/" + Const.FOLDERDATA);
////                        if (!fileDir.exists()) {
////                            fileDir.mkdirs();
////                        }
////                        line.result = Environment.getExternalStorageDirectory() + "/" + Const.SURVEYIMAGE + "/" + Utils.unAccent(line.question)
////                                + System.currentTimeMillis() + ".jpg";
////                        File cameraFile = new File(line.result);
////                        Uri imageCarmeraUri = Uri.fromFile(cameraFile);
////                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
////                                imageCarmeraUri);
////                        try {
////                            intent.putExtra("return-data", true);
////                            ((Activity) getContext()).startActivityForResult(intent, 2);
////                        } catch (ActivityNotFoundException e) {
////                        }
//
//                        Intent startCustomCameraIntent = new Intent(getContext(), CameraActivity.class);
//                        ((Activity) getContext()).startActivityForResult(startCustomCameraIntent, 2);
//                    }
//                });
//                break;
//            case 4:
//                MyMethod.setVisible(binding.formLocation);
//                binding.address.setText(line.result.isEmpty() ? getContext().getString(R.string.location_none) : line.result);
//                binding.location.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        SurveyQAActivity.nowPosition = position;
//                        ((Activity) getContext()).startActivityForResult(new Intent(getContext(), MapsActivity.class), 1);
//                    }
//                });
//                break;
//            case 5:
//                MyMethod.setVisible(binding.imageView);
//                Glide.with(getContext()).load(line.result).centerCrop().into(binding.imageView);
//                break;
//            case 6:
//                MyMethod.setVisible(binding.videoView);
//                //load video
//                break;
//            case 7:
//                MyMethod.setVisible(binding.formEditText);
//                binding.labelEditText.setText(line.question);
//                binding.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                binding.editText.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//                        surveyLines.get(position).result = editable.toString();
//                        // saveToList(surveyLines.get(i));
//                    }
//                });
//                binding.editText.setText(line.result);
//                surveyLines.get(position).result = binding.editText.getText().toString();
//                break;
//            default:
//                break;
//
//        }
//
//        return convertView;
//    }
//
//    @Override
//    public int getCount() {
//        return surveyLines.size();
//    }
//
//    public void setItems(ArrayList<SurveyLine> list) {
//        this.surveyLines = list;
//    }
//
//    public ArrayList<SurveyLine> getModifyList() {
//        return this.surveyLines;
//    }
//
//    public String getResult() {
//        return this.surveyLines.get(SurveyQAActivity.nowPosition).result;
//    }
//
//    public void setResult(String result) {
//        this.surveyLines.get(SurveyQAActivity.nowPosition).result = result;
//    }
//
//    public RowSurveyBinding getBinding() {
//        return binding;
//    }
//
//    public void setFinishView() {
//        this.surveyLines = new ArrayList<>();
//        MyMethod.setVisible(binding.formThank);
//        notifyDataSetChanged();
//    }
//}