package com.medcare.ui;
import com.medcare.model.Appointment;
import com.medcare.model.Doctor;
import com.medcare.model.MedicalService;
import com.medcare.service.DoctorService;
import com.medcare.service.MedicalServiceService;
import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//adaugare/editare de programari
public class AppointmentDialog extends JDialog
{
    private final JTextField patientNameField;
    private final JComboBox<Doctor> doctorComboBox;
    private final JComboBox<MedicalService> serviceComboBox;
    private final JSpinner dateSpinner;
    private final JSpinner timeSpinner;
    private final JComboBox<Appointment.AppointmentStatus> statusComboBox;

    private final DoctorService doctorService;
    private final MedicalServiceService medicalServiceService;

    private boolean confirmed = false;
    private Appointment appointment;

    public AppointmentDialog(JFrame parent, DoctorService doctorService, MedicalServiceService medicalServiceService)
    {
        this(parent, doctorService, medicalServiceService, null);
    }

    public AppointmentDialog(JFrame parent, DoctorService doctorService, MedicalServiceService medicalServiceService, Appointment appointment)
    {
        super(parent, appointment == null ? "Add Appointment" : "Edit Appointment", true);
        setSize(500, 350);
        setLocationRelativeTo(parent);

        this.doctorService = doctorService;
        this.medicalServiceService = medicalServiceService;
        this.appointment = appointment;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Patient Name:"), gbc);

        gbc.gridx = 1;
        patientNameField = new JTextField(20);
        panel.add(patientNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Doctor:"), gbc);

        gbc.gridx = 1;
        java.util.List<Doctor> doctors = doctorService.findAllDoctors();
        Doctor[] doctorArray = doctors.toArray(new Doctor[0]);
        doctorComboBox = new JComboBox<>(doctorArray);
        doctorComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Doctor)
                {
                    value = ((Doctor) value).getName() + " (" + ((Doctor) value).getSpecialization() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        panel.add(doctorComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Service:"), gbc);

        gbc.gridx = 1;
        List<MedicalService> services = medicalServiceService.findAllMedicalServices();
        MedicalService[] serviceArray = services.toArray(new MedicalService[0]);
        serviceComboBox = new JComboBox<>(serviceArray);
        serviceComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                if (value instanceof MedicalService)
                {
                    value = ((MedicalService) value).getName() + " (" + ((MedicalService) value).getPrice() + " lei, " +
                            ((MedicalService) value).getDuration().toMinutes() + " min)";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        panel.add(serviceComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Date:"), gbc);

        gbc.gridx = 1;
        Calendar calendar = Calendar.getInstance();
        Date initDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();

        SpinnerDateModel dateModel = new SpinnerDateModel(initDate, null, endDate, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        panel.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Time:"), gbc);

        gbc.gridx = 1;
        Date initTime = calendar.getTime();
        SpinnerDateModel timeModel = new SpinnerDateModel(initTime, null, null, Calendar.MINUTE);
        timeSpinner = new JSpinner(timeModel);
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        panel.add(timeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(Appointment.AppointmentStatus.values());
        panel.add(statusComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        if (appointment != null)
        {
            patientNameField.setText(appointment.getPatientName());

            for (int i = 0; i < doctorComboBox.getItemCount(); i++)
            {
                Doctor doc = doctorComboBox.getItemAt(i);
                if (doc.getId().equals(appointment.getDoctor().getId()))
                {
                    doctorComboBox.setSelectedIndex(i);
                    break;
                }
            }

            for (int i = 0; i < serviceComboBox.getItemCount(); i++)
            {
                MedicalService svc = serviceComboBox.getItemAt(i);
                if (svc.getId().equals(appointment.getMedicalService().getId()))
                {
                    serviceComboBox.setSelectedIndex(i);
                    break;
                }
            }

            Calendar cal = Calendar.getInstance();
            cal.set(
                    appointment.getStartDateTime().getYear(),
                    appointment.getStartDateTime().getMonthValue() - 1,
                    appointment.getStartDateTime().getDayOfMonth()
            );
            dateSpinner.setValue(cal.getTime());

            cal.set(Calendar.HOUR_OF_DAY, appointment.getStartDateTime().getHour());
            cal.set(Calendar.MINUTE, appointment.getStartDateTime().getMinute());
            timeSpinner.setValue(cal.getTime());

            statusComboBox.setSelectedItem(appointment.getStatus());
        }
        else
        {
            statusComboBox.setSelectedItem(Appointment.AppointmentStatus.NEW);
        }

        okButton.addActionListener(e -> {
            if (validateInputs())
            {
                saveAppointment();
                confirmed = true;
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());
        add(panel);
    }

    private boolean validateInputs()
    {
        if (patientNameField.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter a patient name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (doctorComboBox.getSelectedItem() == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a doctor", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (serviceComboBox.getSelectedItem() == null)
        {
            JOptionPane.showMessageDialog(this, "Please select a service", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveAppointment()
    {
        if (appointment == null)
        {
            appointment = new Appointment();
        }

        appointment.setPatientName(patientNameField.getText().trim());
        appointment.setDoctor((Doctor) doctorComboBox.getSelectedItem());
        appointment.setMedicalService((MedicalService) serviceComboBox.getSelectedItem());

        Date dateValue = (Date) dateSpinner.getValue();
        LocalDate date = dateValue.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        Date timeValue = (Date) timeSpinner.getValue();
        LocalTime time = timeValue.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        LocalDateTime startDateTime = LocalDateTime.of(date, time);
        appointment.setStartDateTime(startDateTime);

        Duration serviceDuration = appointment.getMedicalService().getDuration();
        LocalDateTime endDateTime = startDateTime.plus(serviceDuration);
        appointment.setEndDateTime(endDateTime);

        appointment.setStatus((Appointment.AppointmentStatus) statusComboBox.getSelectedItem());
    }
    public boolean isConfirmed() {
        return confirmed;
    }
    public Appointment getAppointment() {
        return appointment;
    }
}