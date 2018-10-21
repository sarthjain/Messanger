package com.example.risha.first.ui.SignUp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;


import com.example.risha.first.R;
import com.example.risha.first.model.User;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GenderBdayInputFragment extends Fragment {

    Button next;
    DatePicker picker;
    FragmentListener listener;

    public GenderBdayInputFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gender_bday_input, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentListener){
            listener =(FragmentListener)context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        next = (Button) view.findViewById(R.id.Next);
        picker =  ((DatePicker)getView().findViewById(R.id.DOBPick));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.moveToNext();
            }
        });

        String caption;
        if(listener!=null && listener.getUserDetailsModel().name!=null)
            caption="Hi "+listener.getUserDetailsModel().name + ", When is your birthday?";
        else caption="When is your birthday?";

        ((TextView)view.findViewById(R.id.BdayText)).setText(caption);

        view.findViewById(R.id.Next).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(listener!=null)
                {
                    User userDetailsModel = listener.getUserDetailsModel();

                    if(((RadioButton)getView().findViewById(R.id.RadioMale)).isChecked())
                        userDetailsModel.gender="Male";
                    else if(((RadioButton)getView().findViewById(R.id.RadioFemale)).isChecked())
                        userDetailsModel.gender="Female";
                    else
                        userDetailsModel.gender="TransGender";

                    Date date=new GregorianCalendar(picker.getYear(),picker.getMonth(),picker.getDayOfMonth()).getTime();
                    SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
                    userDetailsModel.dob=dt.format(date);

                    listener.moveToNext();
                }
            }
        });

        picker.init(1999, 2, 7, new DatePicker.OnDateChangedListener()
        {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2)
            {
                try
                {
                    if(isValidAge(i,i1,i2))
                        next.setEnabled(true);
                    else
                        next.setEnabled(false);

                    Date date=new GregorianCalendar(i,i1,i2).getTime();
                    SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
                    ((TextView) getView().findViewById(R.id.DOB)).setText(dt.format(date));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        colorizeDatePicker(((DatePicker)view.findViewById(R.id.DOBPick)));
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.YEAR,-10);
        long maxDate = c.getTime().getTime();
        picker.setMaxDate(maxDate);
    }


    private boolean isValidAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
            age--;
        return age>=10;
    }

    public static void colorizeDatePicker(DatePicker datePicker) {
        Resources system = Resources.getSystem();
        int dayId = system.getIdentifier("day", "id", "android");
        int monthId = system.getIdentifier("month", "id", "android");
        int yearId = system.getIdentifier("year", "id", "android");

        NumberPicker dayPicker = (NumberPicker) datePicker.findViewById(dayId);
        NumberPicker monthPicker = (NumberPicker) datePicker.findViewById(monthId);
        NumberPicker yearPicker = (NumberPicker) datePicker.findViewById(yearId);

        setDividerColor(dayPicker);
        setDividerColor(monthPicker);
        setDividerColor(yearPicker);
    }

    private static void setDividerColor(NumberPicker picker) {
        if (picker == null)
            return;

        final int count = picker.getChildCount();
        for (int i = 0; i < count; i++) {
            try {
                Field dividerField = picker.getClass().getDeclaredField("mSelectionDivider");
                dividerField.setAccessible(true);
                ColorDrawable colorDrawable = new ColorDrawable(picker.getResources().getColor(R.color.colorPrimary));
                dividerField.set(picker, colorDrawable);
                picker.invalidate();
            } catch (Exception e) {
                Log.w("setDividerColor", e);
            }
        }
    }

}
