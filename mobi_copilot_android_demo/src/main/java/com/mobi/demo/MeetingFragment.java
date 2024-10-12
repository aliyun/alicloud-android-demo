package com.mobi.demo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeetingFragment extends Fragment {

    private static final String DATE_TIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    private static final SimpleDateFormat dateTimeWithSecondsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final SimpleDateFormat dateTimeWithoutSecondsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    static {
        dateTimeWithSecondsFormat.setLenient(false); // 设置为严格模式进行日期验证
        dateTimeWithoutSecondsFormat.setLenient(false);
    }

    private boolean isValidDateTime(String dateTimeStr) {
        Pattern pattern = Pattern.compile(DATE_TIME_PATTERN);
        Matcher matcher = pattern.matcher(dateTimeStr);
        if (!matcher.matches()) {
            return false;
        }
        try {
            dateTimeWithSecondsFormat.parse(dateTimeStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private Spinner meetingRoomSpinner;
    private EditText startTimeEditText;
    private EditText endTimeEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meeting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        Context context = getContext();
        if (mainActivity == null || context == null) {
            return;
        }
        meetingRoomSpinner = view.findViewById(R.id.meeting_room_spinner);
        startTimeEditText = view.findViewById(R.id.start_time_edit_text);
        endTimeEditText = view.findViewById(R.id.end_time_edit_text);
        Button submitButton = view.findViewById(R.id.meeting_submit_button);
        Button resetButton = view.findViewById(R.id.meeting_reset_button);

        mainActivity.getMeetingData().observe(getViewLifecycleOwner(), new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> data) {
                String meetingRoom = (String) data.get("meetingRoom");
                if (meetingRoom != null) {
                    switch (meetingRoom) {
                        case "梅花岭":
                            meetingRoomSpinner.setSelection(1);
                            break;
                        case "云林峰":
                            meetingRoomSpinner.setSelection(2);
                            break;
                        default:
                            meetingRoomSpinner.setSelection(0);
                            break;
                    }
                }
                String startTime = (String) data.get("startTime");
                if (startTime != null && isValidDateTime(startTime)) {
                    try {
                        startTimeEditText.setText(dateTimeWithoutSecondsFormat.format(dateTimeWithSecondsFormat.parse(startTime)));
                    } catch (ParseException e) {
                        startTimeEditText.setText("");
                    }
                } else {
                    startTimeEditText.setText("");
                }
                String endTime = (String) data.get("endTime");
                if (endTime != null && isValidDateTime(endTime)) {
                    try {
                        endTimeEditText.setText(dateTimeWithoutSecondsFormat.format(dateTimeWithSecondsFormat.parse(endTime)));
                    } catch (ParseException e) {
                        endTimeEditText.setText("");
                    }
                } else {
                    endTimeEditText.setText("");
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.meeting_rooms, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        meetingRoomSpinner.setAdapter(adapter);

        setupDateTimePicker(startTimeEditText, context);
        setupDateTimePicker(endTimeEditText, context);

        submitButton.setOnClickListener(v -> {
            String meetingRoom = meetingRoomSpinner.getSelectedItem().toString();
            String startTime = startTimeEditText.getText().toString();
            String endTime = endTimeEditText.getText().toString();
            if (meetingRoom.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(getContext(), "请完整填写所有字段", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "提交成功！\nmeetingRoom: " + meetingRoom + "\nstartTime: " + startTime + "\nendTime: " + endTime, Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(v -> {
            meetingRoomSpinner.setSelection(0);
            startTimeEditText.setText("");
            endTimeEditText.setText("");
            Toast.makeText(getContext(), "表单已重置", Toast.LENGTH_SHORT).show();
        });

    }

    private void setupDateTimePicker(final EditText editText, Context context) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, year1, month1, dayOfMonth) -> {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                (view1, hourOfDay, minute1) -> {
                                    String dateTime = String.format(Locale.CHINA, "%d-%02d-%02d %02d:%02d", year1, month1 + 1, dayOfMonth, hourOfDay, minute1);
                                    editText.setText(dateTime);
                                }, hour, minute, true);
                        timePickerDialog.show();
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

}