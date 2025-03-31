package com.medcare.ui;
import com.medcare.model.Doctor;
import com.medcare.model.WorkingHours;
import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

//adaugarea si editarea medicilor
public class DoctorDialog extends JDialog
{
    private final JTextField nameField;
    private final JTextField specializationField;
    private final Map<DayOfWeek, JCheckBox> dayCheckboxes = new HashMap<>();
    private final Map<DayOfWeek, JTextField> startTimeFields = new HashMap<>();
    private final Map<DayOfWeek, JTextField> endTimeFields = new HashMap<>();

    private boolean confirmed = false;
    private Doctor doctor;

    public DoctorDialog(JFrame parent)
    {
        this(parent, null);
    }

    public DoctorDialog(JFrame parent, Doctor doctor)
    {
        super(parent, doctor == null ? "Add Doctor" : "Edit Doctor", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);

        this.doctor = doctor;

        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        topPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("Specialization:"), gbc);

        gbc.gridx = 1;
        specializationField = new JTextField(20);
        topPanel.add(specializationField, gbc);

        JPanel schedulePanel = new JPanel(new GridBagLayout());
        schedulePanel.setBorder(BorderFactory.createTitledBorder("Working Hours"));

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        schedulePanel.add(new JLabel("Day"), gbc);

        gbc.gridx = 1;
        schedulePanel.add(new JLabel("Start Time (HH:MM)"), gbc);

        gbc.gridx = 2;
        schedulePanel.add(new JLabel("End Time (HH:MM)"), gbc);

        int row = 1;
        for (DayOfWeek day : DayOfWeek.values())
        {
            gbc.gridx = 0;
            gbc.gridy = row;

            JCheckBox dayCheckbox = new JCheckBox(day.toString());
            schedulePanel.add(dayCheckbox, gbc);
            dayCheckboxes.put(day, dayCheckbox);

            gbc.gridx = 1;
            JTextField startField = new JTextField(8);
            schedulePanel.add(startField, gbc);
            startTimeFields.put(day, startField);

            gbc.gridx = 2;
            JTextField endField = new JTextField(8);
            schedulePanel.add(endField, gbc);
            endTimeFields.put(day, endField);

            row++;
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        if (doctor != null)
        {
            nameField.setText(doctor.getName());
            specializationField.setText(doctor.getSpecialization());

            for (WorkingHours wh : doctor.getWorkingHours())
            {
                DayOfWeek day = wh.getDayOfWeek();
                dayCheckboxes.get(day).setSelected(true);
                startTimeFields.get(day).setText(wh.getStartTime().toString());
                endTimeFields.get(day).setText(wh.getEndTime().toString());
            }
        }

        okButton.addActionListener(e -> {
            if (validateInputs())
            {
                saveDoctor();
                confirmed = true;
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(schedulePanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private boolean validateInputs()
    {
        if (nameField.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter a name",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (specializationField.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter a specialization",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean hasWorkingDay = false;
        for (DayOfWeek day : DayOfWeek.values())
        {
            if (dayCheckboxes.get(day).isSelected())
            {
                hasWorkingDay = true;

                try {
                    if (startTimeFields.get(day).getText().trim().isEmpty() ||
                            endTimeFields.get(day).getText().trim().isEmpty())
                    {
                        JOptionPane.showMessageDialog(this,
                                "Please enter both start and end time for " + day,
                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                    LocalTime startTime = LocalTime.parse(startTimeFields.get(day).getText().trim());
                    LocalTime endTime = LocalTime.parse(endTimeFields.get(day).getText().trim());

                    if (startTime.isAfter(endTime) || startTime.equals(endTime))
                    {
                        JOptionPane.showMessageDialog(this,
                                "End time must be after start time for " + day,
                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
                catch (DateTimeParseException e)
                {
                    JOptionPane.showMessageDialog(this,
                            "Invalid time format for " + day + ". Use HH:MM format.",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        if (!hasWorkingDay)
        {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one working day",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
    private void saveDoctor()
    {
        if (doctor == null)
        {
            doctor = new Doctor();
        }

        doctor.setName(nameField.getText().trim());
        doctor.setSpecialization(specializationField.getText().trim());
        doctor.getWorkingHours().clear();
        for (DayOfWeek day : DayOfWeek.values())
        {
            if (dayCheckboxes.get(day).isSelected())
            {
                LocalTime startTime = LocalTime.parse(startTimeFields.get(day).getText().trim());
                LocalTime endTime = LocalTime.parse(endTimeFields.get(day).getText().trim());

                WorkingHours workingHours = new WorkingHours(day, startTime, endTime);
                doctor.getWorkingHours().add(workingHours);
            }
        }
    }
    public boolean isConfirmed()
    {
        return confirmed;
    }
    public Doctor getDoctor()
    {
        return doctor;
    }
}