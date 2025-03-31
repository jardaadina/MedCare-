package com.medcare.ui;
import com.medcare.model.Appointment;
import com.medcare.model.Receptionist;
import com.medcare.service.AppointmentService;
import com.medcare.service.DoctorService;
import com.medcare.service.MedicalServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

//interfata pentru receptionisti care permite si gestionarea programarilor
@Component
public class ReceptionistDashboardFrame extends JFrame
{
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final MedicalServiceService medicalServiceService;

    private Receptionist currentReceptionist;
    private LoginFrame loginFrame;

    private JTabbedPane tabbedPane;
    private JTable appointmentsTable;
    private DefaultTableModel appointmentsTableModel;

    private JTextField searchPatientField;
    private JButton searchButton;
    private JButton refreshButton;

    private JComboBox<String> statusFilterComboBox;

    private final Color LIGHT_BLUE_BG = new Color(240, 248, 255);
    private final Color MEDIUM_BLUE = new Color(66, 133, 244);
    private final Color LIGHT_BLUE = new Color(178, 209, 255);
    private final Color DARK_BLUE = new Color(41, 98, 255);
    private final Color DARK_TEXT = new Color(50, 50, 50);
    private final Color ADD_COLOR = new Color(76, 175, 80);
    private final Color EDIT_COLOR = new Color(255, 152, 0);
    private final Color UPDATE_COLOR = new Color(103, 183, 220);

    @Autowired
    public ReceptionistDashboardFrame(AppointmentService appointmentService,
                                      DoctorService doctorService,
                                      MedicalServiceService medicalServiceService)
    {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.medicalServiceService = medicalServiceService;

        initializeUI();
    }

    public void setReceptionist(Receptionist receptionist)
    {
        this.currentReceptionist = receptionist;
        setTitle("MedCare - Receptionist Dashboard - " + receptionist.getName());
        loadAppointments();
    }
    public void setLoginFrame(LoginFrame loginFrame)
    {
        this.loginFrame = loginFrame;
    }

    private void initializeUI()
    {
        setTitle("MedCare - Receptionist Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(LIGHT_BLUE_BG);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BLUE_BG);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(MEDIUM_BLUE);
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Receptionist Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_BLUE_BG);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        tabbedPane.setBackground(LIGHT_BLUE_BG);
        tabbedPane.setForeground(DARK_TEXT);

        UIManager.put("TabbedPane.selected", LIGHT_BLUE);
        UIManager.put("TabbedPane.background", LIGHT_BLUE_BG);
        UIManager.put("TabbedPane.contentAreaColor", LIGHT_BLUE_BG);
        UIManager.put("TabbedPane.focus", MEDIUM_BLUE);
        UIManager.put("TabbedPane.highlight", LIGHT_BLUE_BG);
        UIManager.put("TabbedPane.light", LIGHT_BLUE_BG);
        UIManager.put("TabbedPane.tabInsets", new Insets(8, 15, 8, 15));
        UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(2, 2, 2, 2));

        JPanel appointmentsPanel = createAppointmentsPanel();
        tabbedPane.addTab("Appointments", appointmentsPanel);

        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createAppointmentsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LIGHT_BLUE_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(LIGHT_BLUE_BG);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BLUE, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(LIGHT_BLUE_BG);

        JLabel searchLabel = new JLabel("Patient Name:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(DARK_TEXT);

        searchPatientField = new JTextField(20);
        searchPatientField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPatientField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BLUE, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchPatientField.setPreferredSize(new Dimension(200, 30));

        searchButton = createStyledButton("Search Patient", MEDIUM_BLUE);
        refreshButton = createStyledButton("Refresh", LIGHT_BLUE.darker());

        searchPanel.add(searchLabel);
        searchPanel.add(searchPatientField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        filterPanel.setBackground(LIGHT_BLUE_BG);

        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterLabel.setForeground(DARK_TEXT);

        statusFilterComboBox = new JComboBox<>(new String[]{"All Statuses", "NEW", "IN_PROGRESS", "COMPLETED"});
        statusFilterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        statusFilterComboBox.setPreferredSize(new Dimension(150, 30));
        statusFilterComboBox.setBackground(Color.WHITE);

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilterComboBox);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);

        String[] columns = {"ID", "Patient Name", "Doctor", "Service", "Date & Time", "End Time", "Status"};
        appointmentsTableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        appointmentsTable = new JTable(appointmentsTableModel);
        styleTable(appointmentsTable);

        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(LIGHT_BLUE_BG);
        buttonsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BLUE, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JButton addButton = createStyledButton("New Appointment", ADD_COLOR);
        JButton editButton = createStyledButton("Edit Appointment", EDIT_COLOR);
        JButton updateStatusButton = createStyledButton("Update Status", UPDATE_COLOR);

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(updateStatusButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchAppointmentsByPatient());
        refreshButton.addActionListener(e -> loadAppointments());
        statusFilterComboBox.addActionListener(e -> filterAppointmentsByStatus());

        addButton.addActionListener(e -> openAddAppointmentDialog());
        editButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow != -1)
            {
                Long id = (Long) appointmentsTable.getValueAt(selectedRow, 0);
                openEditAppointmentDialog(id);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select an appointment to edit",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        updateStatusButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow != -1)
            {
                Long id = (Long) appointmentsTable.getValueAt(selectedRow, 0);
                updateAppointmentStatus(id);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select an appointment to update",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private void styleTable(JTable table)
    {
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionBackground(LIGHT_BLUE);
        table.setSelectionForeground(DARK_TEXT);
        table.setGridColor(new Color(230, 235, 245));
        table.setShowGrid(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setBackground(MEDIUM_BLUE);
        header.setForeground(Color.BLACK);  // Use black text for better visibility
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 2));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setOpaque(true);
    }

    private JButton createStyledButton(String text, Color bgColor)
    {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        int width = Math.max(text.length() * 10, 120);
        button.setPreferredSize(new Dimension(width, 35));

        button.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(MouseEvent evt)
            {
                button.setBackground(darken(bgColor));
            }

            public void mouseExited(MouseEvent evt)
            {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private Color darken(Color color)
    {
        return new Color(
                Math.max(0, (int)(color.getRed() * 0.9)),
                Math.max(0, (int)(color.getGreen() * 0.9)),
                Math.max(0, (int)(color.getBlue() * 0.9))
        );
    }

    private void loadAppointments()
    {
        List<Appointment> appointments = appointmentService.findAllAppointments();
        updateAppointmentsTable(appointments);
    }

    private void updateAppointmentsTable(List<Appointment> appointments)
    {
        appointmentsTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Appointment appointment : appointments) {
            appointmentsTableModel.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getPatientName(),
                    appointment.getDoctor().getName(),
                    appointment.getMedicalService().getName(),
                    appointment.getStartDateTime().format(formatter),
                    appointment.getEndDateTime().format(formatter),
                    appointment.getStatus()
            });
        }
    }

    private void searchAppointmentsByPatient()
    {
        String patientName = searchPatientField.getText().trim();
        if (patientName.isEmpty())
        {
            loadAppointments();
            return;
        }

        List<Appointment> appointments = appointmentService.findAppointmentsByPatientName(patientName);
        updateAppointmentsTable(appointments);
    }

    private void filterAppointmentsByStatus()
    {
        String selectedStatus = (String) statusFilterComboBox.getSelectedItem();
        if ("All Statuses".equals(selectedStatus))
        {
            loadAppointments();
            return;
        }

        List<Appointment> allAppointments = appointmentService.findAllAppointments();

        List<Appointment> filteredAppointments = allAppointments.stream()
                .filter(appointment -> selectedStatus.equals(appointment.getStatus().toString()))
                .collect(java.util.stream.Collectors.toList());

        updateAppointmentsTable(filteredAppointments);
    }

    private void openAddAppointmentDialog()
    {
        AppointmentDialog dialog = new AppointmentDialog(this, doctorService, medicalServiceService);
        // Center the dialog on this frame
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Appointment appointment = dialog.getAppointment();

            try
            {
                appointmentService.createAppointment(appointment);
                loadAppointments();
                showSuccessMessage("Appointment created successfully!");
            }
            catch (IllegalArgumentException e)
            {
                showErrorMessage(e.getMessage(), "Error Creating Appointment");
            }
        }
    }

    private void openEditAppointmentDialog(Long id)
    {
        Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(id);

        if (appointmentOpt.isPresent())
        {
            Appointment appointment = appointmentOpt.get();
            AppointmentDialog dialog = new AppointmentDialog(this, doctorService, medicalServiceService, appointment);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            if (dialog.isConfirmed())
            {
                Appointment updatedAppointment = dialog.getAppointment();

                try
                {
                    updatedAppointment.setId(appointment.getId());
                    appointmentService.createAppointment(updatedAppointment);
                    loadAppointments();
                    showSuccessMessage("Appointment updated successfully!");
                }
                catch (IllegalArgumentException e)
                {
                    showErrorMessage(e.getMessage(), "Error Updating Appointment");
                }
            }
        }
    }

    private void updateAppointmentStatus(Long id)
    {
        Optional<Appointment> appointmentOpt = appointmentService.findAppointmentById(id);

        if (appointmentOpt.isPresent())
        {
            Appointment appointment = appointmentOpt.get();

            String[] options = {"NEW", "IN_PROGRESS", "COMPLETED"};
            int currentStatusIndex = 0;

            for (int i = 0; i < options.length; i++)
            {
                if (options[i].equals(appointment.getStatus().toString()))
                {
                    currentStatusIndex = i;
                    break;
                }
            }

            JDialog statusDialog = new JDialog(this, "Update Appointment Status", true);
            statusDialog.setSize(350, 200);
            statusDialog.setLocationRelativeTo(this);
            statusDialog.setLayout(new BorderLayout());
            statusDialog.getContentPane().setBackground(LIGHT_BLUE_BG);

            JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
            contentPanel.setBackground(LIGHT_BLUE_BG);
            contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

            JLabel promptLabel = new JLabel("Select new status:");
            promptLabel.setFont(new Font("Arial", Font.BOLD, 14));
            promptLabel.setForeground(DARK_TEXT);

            JComboBox<String> statusComboBox = new JComboBox<>(options);
            statusComboBox.setSelectedIndex(currentStatusIndex);
            statusComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
            statusComboBox.setBackground(Color.WHITE);

            contentPanel.add(promptLabel, BorderLayout.NORTH);
            contentPanel.add(statusComboBox, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.setBackground(LIGHT_BLUE_BG);

            JButton updateButton = createStyledButton("Update", MEDIUM_BLUE);
            JButton cancelButton = createStyledButton("Cancel", new Color(158, 158, 158));

            buttonPanel.add(updateButton);
            buttonPanel.add(cancelButton);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);

            statusDialog.add(contentPanel);

            final String[] selectedStatus = {null};

            updateButton.addActionListener(e -> {
                selectedStatus[0] = (String) statusComboBox.getSelectedItem();
                statusDialog.dispose();
            });

            cancelButton.addActionListener(e -> statusDialog.dispose());

            statusDialog.setVisible(true);

            if (selectedStatus[0] != null)
            {
                try
                {
                    Appointment.AppointmentStatus newStatus = Appointment.AppointmentStatus.valueOf(selectedStatus[0]);
                    appointmentService.updateAppointmentStatus(id, newStatus);
                    loadAppointments();
                    showSuccessMessage("Status updated successfully!");
                }
                catch (Exception e)
                {
                    showErrorMessage("Error updating status: " + e.getMessage(), "Error");
                }
            }
        }
    }
    private void showSuccessMessage(String message)
    {
        JOptionPane.showMessageDialog(this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
    private void showErrorMessage(String message, String title)
    {
        JOptionPane.showMessageDialog(this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
}