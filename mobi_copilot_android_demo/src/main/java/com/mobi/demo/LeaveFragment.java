package com.mobi.demo;

import android.app.DatePickerDialog;
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

public class LeaveFragment extends Fragment {

    private static final String DATE_TIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    static {
        dateTimeFormat.setLenient(false);
        dateFormat.setLenient(false);
    }

    public static boolean isValidDateTime(String dateTimeStr) {
        Pattern pattern = Pattern.compile(DATE_TIME_PATTERN);
        Matcher matcher = pattern.matcher(dateTimeStr);
        if (!matcher.matches()) {
            return false;
        }
        try {
            dateTimeFormat.parse(dateTimeStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private Spinner leaveTypeSpinner;
    private EditText startDateEditText;
    private EditText endDateEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leave, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        Context context = getContext();
        if (mainActivity == null || context == null) {
            return;
        }
        leaveTypeSpinner = view.findViewById(R.id.leave_type_spinner);
        startDateEditText = view.findViewById(R.id.start_date_edit_text);
        endDateEditText = view.findViewById(R.id.end_date_edit_text);
        Button submitButton = view.findViewById(R.id.submit_button);
        Button resetButton = view.findViewById(R.id.reset_button);

        mainActivity.getLeaveData().observe(getViewLifecycleOwner(), new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> data) {
                String leaveType = (String) data.get("leaveType");
                if (leaveType != null) {
                    switch (leaveType) {
                        case "事假":
                            leaveTypeSpinner.setSelection(1);
                            break;
                        case "病假":
                            leaveTypeSpinner.setSelection(2);
                            break;
                        default:
                            leaveTypeSpinner.setSelection(0);
                            break;
                    }
                }
                String startDate = (String) data.get("startDate");
                if (startDate != null && isValidDateTime(startDate)) {
                    try {
                        startDateEditText.setText(dateFormat.format(dateTimeFormat.parse(startDate)));
                    } catch (ParseException e) {
                        startDateEditText.setText("");
                    }
                } else {
                    startDateEditText.setText("");
                }
                String endDate = (String) data.get("endDate");
                if (endDate != null && isValidDateTime(endDate)) {
                    try {
                        endDateEditText.setText(dateFormat.format(dateTimeFormat.parse(endDate)));
                    } catch (ParseException e) {
                        endDateEditText.setText("");
                    }
                } else {
                    endDateEditText.setText("");
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.leave_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaveTypeSpinner.setAdapter(adapter);

        setupDatePicker(startDateEditText, context);
        setupDatePicker(endDateEditText, context);

        submitButton.setOnClickListener(v -> {
            String leaveType = leaveTypeSpinner.getSelectedItem().toString();
            String startDate = startDateEditText.getText().toString();
            String endDate = endDateEditText.getText().toString();
            if (leaveType.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(getContext(), "请完整填写所有字段", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "提交成功！\nleaveType: " + leaveType + "\nstartDate: " + startDate + "\nendDate: " + endDate, Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(v -> {
            leaveTypeSpinner.setSelection(0);
            startDateEditText.setText("");
            endDateEditText.setText("");
            Toast.makeText(getContext(), "表单已重置", Toast.LENGTH_SHORT).show();
        });

    }

    private void setupDatePicker(final EditText editText, Context context) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, year1, month1, dayOfMonth) -> {
                        String date = String.format(Locale.CHINA, "%d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        editText.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }
}