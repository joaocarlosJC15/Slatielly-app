package com.example.slatielly.app.calendar.CalendarDateFinish;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.slatielly.R;

import java.sql.Timestamp;
import java.util.Calendar;

public class CalendarDateFinishActivity extends AppCompatActivity implements CalendarDateFinishContract.View
{
    public Calendar calendar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.calendar_date_finish);

        CalendarView calendarViewFinish = (CalendarView) findViewById(R.id.MaterialCalendarView_calendar_date_finish);


        calendar = Calendar.getInstance();

        calendarViewFinish.setMinimumDate(calendar);

        try //releases dates after a minimum date
        {
            calendarViewFinish.setDate(calendar);
        }
        catch (OutOfDateRangeException e)
        {
            e.printStackTrace();
        }

        calendarViewFinish.setOnDayClickListener(new OnDayClickListener()
        {
            @Override
            public void onDayClick(EventDay eventDay)
            {
                Calendar clickedDayCalendar = eventDay.getCalendar();

                Timestamp dateFinish = formDate(clickedDayCalendar);
            }
        });
    }

    @Override
    public Timestamp formDate(Calendar clickedDayCalendar)
    {
        int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
        int month = clickedDayCalendar.get(Calendar.MONTH);
        int year = clickedDayCalendar.get(Calendar.YEAR);

        Calendar aux = Calendar.getInstance();
        aux.set(year,month,day,0,0,0);

        Timestamp dateStart = new Timestamp(aux.getTimeInMillis());
        dateStart.setNanos(0);
        return dateStart;
    }

    @Override
    public void setLoadingStatus(boolean isLoading)
    {

    }

    @Override
    public void setErrorMessage(String errorMessage)
    {

    }
}