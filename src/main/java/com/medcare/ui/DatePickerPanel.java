package com.medcare.ui;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

//selectarea datelor
public class DatePickerPanel extends JPanel
{
    private final JSpinner dateSpinner;
    private final JFormattedTextField textField;

    public DatePickerPanel()
    {
        super(new FlowLayout(FlowLayout.LEFT));

        SpinnerDateModel model = new SpinnerDateModel();

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.YEAR, -1);
        Date start = calendar.getTime();
        calendar.add(Calendar.YEAR, 2);
        Date end = calendar.getTime();

        model.setValue(now);
        dateSpinner = new JSpinner(model);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        textField = ((JSpinner.DefaultEditor) dateSpinner.getEditor()).getTextField();

        this.add(dateSpinner);
    }

    public LocalDate getDate()
    {
        Date date = (Date) dateSpinner.getValue();
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public void setDate(LocalDate date)
    {
        Date utilDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        dateSpinner.setValue(utilDate);
    }
}