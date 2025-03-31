package com.medcare.ui;
import com.medcare.model.Appointment;
import com.medcare.model.Doctor;
import com.medcare.model.MedicalService;
import com.medcare.model.Receptionist;
import com.medcare.service.AdministratorService;
import com.medcare.service.DoctorService;
import com.medcare.service.MedicalServiceService;
import com.medcare.service.ReportService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

//interfata pentru administratori
//permite gestionearea receptionistilor, medicilor, serviciilor medicale
//la final am facut partea cu generarea de rapoarte
@org.springframework.stereotype.Component
public class AdminDashboardFrame extends JFrame
{
    private final AdministratorService administratorService;
    private final DoctorService doctorService;
    private final MedicalServiceService medicalServiceService;
    private final ReportService reportService;

    private JTabbedPane tabbedPane;
    private JTable receptionistsTable;
    private JTable doctorsTable;
    private JTable servicesTable;
    private DefaultTableModel receptionistsTableModel;
    private DefaultTableModel doctorsTableModel;
    private DefaultTableModel servicesTableModel;

    private JPanel reportsPanel;
    private DatePickerPanel startDatePicker;
    private DatePickerPanel endDatePicker;
    private JButton generateReportButton;
    private JButton exportCsvButton;
    private JTable reportTable;
    private DefaultTableModel reportTableModel;

    private final Color LIGHT_BLUE_BG = new Color(240, 248, 255);
    private final Color MEDIUM_BLUE = new Color(66, 133, 244);
    private final Color LIGHT_BLUE = new Color(178, 209, 255);
    private final Color DARK_BLUE = new Color(41, 98, 255);
    private final Color DARK_TEXT = new Color(50, 50, 50);
    private final Color ADD_COLOR = new Color(76, 175, 80);
    private final Color EDIT_COLOR = new Color(255, 152, 0);
    private final Color DELETE_COLOR = new Color(244, 67, 54);

    public AdminDashboardFrame(AdministratorService administratorService,
                               DoctorService doctorService,
                               MedicalServiceService medicalServiceService,
                               ReportService reportService)
    {
        this.administratorService = administratorService;
        this.doctorService = doctorService;
        this.medicalServiceService = medicalServiceService;
        this.reportService = reportService;

        initializeUI();
        loadData();
    }

    private void initializeUI()
    {
        setTitle("MedCare - Administrator Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(LIGHT_BLUE_BG);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(MEDIUM_BLUE);
        headerPanel.setPreferredSize(new Dimension(900, 60));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Administrator Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

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

        JPanel receptionistsPanel = createReceptionistsPanel();
        JPanel doctorsPanel = createDoctorsPanel();
        JPanel servicesPanel = createServicesPanel();
        reportsPanel = createReportsPanel();

        tabbedPane.addTab("Receptionists", receptionistsPanel);
        tabbedPane.addTab("Doctors", doctorsPanel);
        tabbedPane.addTab("Medical Services", servicesPanel);
        tabbedPane.addTab("Reports", reportsPanel);

        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createReceptionistsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LIGHT_BLUE_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Name", "Username"};
        receptionistsTableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        receptionistsTable = new JTable(receptionistsTableModel);
        styleTable(receptionistsTable);

        JScrollPane scrollPane = new JScrollPane(receptionistsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(LIGHT_BLUE_BG);

        JButton addButton = createStyledButton("Add Receptionist", ADD_COLOR);
        JButton editButton = createStyledButton("Edit Receptionist", EDIT_COLOR);
        JButton deleteButton = createStyledButton("Delete Receptionist", DELETE_COLOR);

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> openAddReceptionistDialog());
        editButton.addActionListener(e -> {
            int selectedRow = receptionistsTable.getSelectedRow();
            if (selectedRow != -1)
            {
                Long id = (Long) receptionistsTable.getValueAt(selectedRow, 0);
                openEditReceptionistDialog(id);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select a receptionist to edit",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = receptionistsTable.getSelectedRow();
            if (selectedRow != -1)
            {
                Long id = (Long) receptionistsTable.getValueAt(selectedRow, 0);
                deleteReceptionist(id);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select a receptionist to delete",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createDoctorsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LIGHT_BLUE_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Name", "Specialization", "Working Hours"};
        doctorsTableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        doctorsTable = new JTable(doctorsTableModel);
        styleTable(doctorsTable);

        JScrollPane scrollPane = new JScrollPane(doctorsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(LIGHT_BLUE_BG);

        JButton addButton = createStyledButton("Add Doctor", ADD_COLOR);
        JButton editButton = createStyledButton("Edit Doctor", EDIT_COLOR);
        JButton deleteButton = createStyledButton("Delete Doctor", DELETE_COLOR);

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> openAddDoctorDialog());
        editButton.addActionListener(e -> {
            int selectedRow = doctorsTable.getSelectedRow();
            if (selectedRow != -1)
            {
                Long id = (Long) doctorsTable.getValueAt(selectedRow, 0);
                openEditDoctorDialog(id);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select a doctor to edit",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = doctorsTable.getSelectedRow();
            if (selectedRow != -1)
            {
                Long id = (Long) doctorsTable.getValueAt(selectedRow, 0);
                deleteDoctor(id);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select a doctor to delete",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createServicesPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LIGHT_BLUE_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Name", "Price", "Duration (min)"};
        servicesTableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        servicesTable = new JTable(servicesTableModel);
        styleTable(servicesTable);

        JScrollPane scrollPane = new JScrollPane(servicesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(LIGHT_BLUE_BG);

        JButton addButton = createStyledButton("Add Service", ADD_COLOR);
        JButton editButton = createStyledButton("Edit Service", EDIT_COLOR);
        JButton deleteButton = createStyledButton("Delete Service", DELETE_COLOR);

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> openAddServiceDialog());
        editButton.addActionListener(e -> {
            int selectedRow = servicesTable.getSelectedRow();
            if (selectedRow != -1)
            {
                Long id = (Long) servicesTable.getValueAt(selectedRow, 0);
                openEditServiceDialog(id);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select a service to edit",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = servicesTable.getSelectedRow();
            if (selectedRow != -1)
            {
                Long id = (Long) servicesTable.getValueAt(selectedRow, 0);
                deleteMedicalService(id);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Please select a service to delete",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createReportsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LIGHT_BLUE_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel dateSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        dateSelectionPanel.setBackground(LIGHT_BLUE_BG);
        dateSelectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BLUE, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        startDateLabel.setForeground(DARK_TEXT);
        JLabel endDateLabel = new JLabel("End Date:");
        endDateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        endDateLabel.setForeground(DARK_TEXT);

        startDatePicker = new DatePickerPanel();
        endDatePicker = new DatePickerPanel();

        generateReportButton = createStyledButton("Generate Report", MEDIUM_BLUE);

        dateSelectionPanel.add(startDateLabel);
        dateSelectionPanel.add(startDatePicker);
        dateSelectionPanel.add(endDateLabel);
        dateSelectionPanel.add(endDatePicker);
        dateSelectionPanel.add(generateReportButton);

        String[] columns = {"ID", "Patient Name", "Doctor", "Service", "Date", "Status"};
        reportTableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        reportTable = new JTable(reportTableModel);
        styleTable(reportTable);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        exportPanel.setBackground(LIGHT_BLUE_BG);
        exportPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BLUE, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        exportCsvButton = createStyledButton("Export to CSV", new Color(103, 183, 220));
        JButton viewMostRequestedDoctorsButton = createStyledButton("Most Requested Doctors", MEDIUM_BLUE);
        JButton viewMostRequestedServicesButton = createStyledButton("Most Requested Services", MEDIUM_BLUE);

        exportPanel.add(exportCsvButton);
        exportPanel.add(viewMostRequestedDoctorsButton);
        exportPanel.add(viewMostRequestedServicesButton);

        generateReportButton.addActionListener(e -> generateReport());
        exportCsvButton.addActionListener(e -> exportReportToCsv());
        viewMostRequestedDoctorsButton.addActionListener(e -> viewMostRequestedDoctors());
        viewMostRequestedServicesButton.addActionListener(e -> viewMostRequestedServices());

        panel.add(dateSelectionPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(exportPanel, BorderLayout.SOUTH);

        return panel;
    }
    private void viewMostRequestedDoctors()
    {
        List<Object[]> mostRequestedDoctors = reportService.getMostRequestedDoctorsReport();

        if (mostRequestedDoctors == null || mostRequestedDoctors.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "No data available for most requested doctors",
                    "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Most Requested Doctors", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(LIGHT_BLUE_BG);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(MEDIUM_BLUE);
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Most Requested Doctors", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(LIGHT_BLUE_BG);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"Doctor ID", "Doctor Name", "Appointment Count"};
        DefaultTableModel model = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        for (Object[] doctorData : mostRequestedDoctors)
        {
            model.addRow(new Object[]{
                    doctorData[0], //id doctor
                    doctorData[1], //nume doctor
                    doctorData[2]  //numara programarile
            });
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(LIGHT_BLUE_BG);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton closeButton = createStyledButton("Close", MEDIUM_BLUE);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        dialog.add(titlePanel, BorderLayout.NORTH);
        dialog.add(tablePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void viewMostRequestedServices()
    {
        List<Object[]> mostRequestedServices = reportService.getMostRequestedServicesReport();

        if (mostRequestedServices == null || mostRequestedServices.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "No data available for most requested services",
                    "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Most Requested Services", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(LIGHT_BLUE_BG);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(MEDIUM_BLUE);
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Most Requested Services", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(LIGHT_BLUE_BG);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"Service ID", "Service Name", "Request Count"};
        DefaultTableModel model = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        for (Object[] serviceData : mostRequestedServices)
        {
            model.addRow(new Object[]{
                    serviceData[0], //id serviciu
                    serviceData[1], //nume serviciu
                    serviceData[2]  //numara de cate ori este solicitat un serviciu
            });
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(LIGHT_BLUE_BG);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton closeButton = createStyledButton("Close", MEDIUM_BLUE);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        dialog.add(titlePanel, BorderLayout.NORTH);
        dialog.add(tablePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void styleTable(JTable table)
    {
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionBackground(LIGHT_BLUE);
        table.setSelectionForeground(DARK_TEXT);
        table.setGridColor(new Color(255, 255, 255));
        table.setShowGrid(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setBackground(MEDIUM_BLUE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(DARK_BLUE));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
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
        button.setPreferredSize(new Dimension(160, 35));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darken(bgColor));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private Color darken(Color color)
    {
        return new Color(
                Math.max(0, (int) (color.getRed() * 0.9)),
                Math.max(0, (int) (color.getGreen() * 0.9)),
                Math.max(0, (int) (color.getBlue() * 0.9))
        );
    }

    private void loadData()
    {
        List<Receptionist> receptionists = administratorService.findAllReceptionists();
        receptionistsTableModel.setRowCount(0);
        for (Receptionist receptionist : receptionists)
        {
            receptionistsTableModel.addRow(new Object[]
                    {
                    receptionist.getId(),
                    receptionist.getName(),
                    receptionist.getUsername()
            });
        }

        List<Doctor> doctors = doctorService.findAllDoctors();
        doctorsTableModel.setRowCount(0);
        for (Doctor doctor : doctors)
        {
            doctorsTableModel.addRow(new Object[]
                    {
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getSpecialization(),
                    formatWorkingHours(doctor)
            });
        }

        List<MedicalService> services = medicalServiceService.findAllMedicalServices();
        servicesTableModel.setRowCount(0);
        for (MedicalService service : services)
        {
            servicesTableModel.addRow(new Object[]
                    {
                    service.getId(),
                    service.getName(),
                    service.getPrice(),
                    service.getDuration().toMinutes()
            });
        }
    }

    private String formatWorkingHours(Doctor doctor)
    {
        StringBuilder sb = new StringBuilder();
        doctor.getWorkingHours().forEach(wh ->
                sb.append(wh.getDayOfWeek())
                        .append(": ")
                        .append(wh.getStartTime())
                        .append("-")
                        .append(wh.getEndTime())
                        .append(", ")
        );
        if (sb.length() > 2)
        {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }
    private void openAddReceptionistDialog()
    {
        ReceptionistDialog dialog = new ReceptionistDialog(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed())
        {
            Receptionist receptionist = dialog.getReceptionist();
            administratorService.createReceptionist(receptionist);
            loadData();
        }
    }

    private void openEditReceptionistDialog(Long id)
    {
        List<Receptionist> receptionists = administratorService.findAllReceptionists();
        Receptionist selectedReceptionist = null;
        for (Receptionist receptionist : receptionists)
        {
            if (receptionist.getId().equals(id))
            {
                selectedReceptionist = receptionist;
                break;
            }
        }

        if (selectedReceptionist != null)
        {
            ReceptionistDialog dialog = new ReceptionistDialog(this, selectedReceptionist);
            dialog.setVisible(true);
            if (dialog.isConfirmed())
            {
                Receptionist updatedReceptionist = dialog.getReceptionist();
                updatedReceptionist.setId(selectedReceptionist.getId());
                administratorService.updateReceptionist(updatedReceptionist);
                loadData();
            }
        }
    }

    private void deleteReceptionist(Long id) {
        int confirmed = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this receptionist?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmed == JOptionPane.YES_OPTION) {
            administratorService.deleteReceptionist(id);
            loadData();
        }
    }
    private void openAddDoctorDialog()
    {
        DoctorDialog dialog = new DoctorDialog(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed())
        {
            Doctor doctor = dialog.getDoctor();
            doctorService.saveDoctor(doctor);
            loadData();
        }
    }
    private void openEditDoctorDialog(Long id)
    {
        Doctor selectedDoctor = doctorService.findDoctorById(id).orElse(null);

        if (selectedDoctor != null)
        {
            DoctorDialog dialog = new DoctorDialog(this, selectedDoctor);
            dialog.setVisible(true);

            if (dialog.isConfirmed())
            {
                Doctor updatedDoctor = dialog.getDoctor();
                doctorService.saveDoctor(updatedDoctor);
                loadData();
            }
        }
    }
    private void deleteDoctor(Long id)
    {
        int confirmed = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this doctor?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmed == JOptionPane.YES_OPTION)
        {
            doctorService.deleteDoctor(id);
            loadData();
        }
    }
    private void openAddServiceDialog()
    {
        MedicalServiceDialog dialog = new MedicalServiceDialog(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed())
        {
            MedicalService service = dialog.getMedicalService();
            medicalServiceService.saveMedicalService(service);
            loadData();
        }
    }

    private void openEditServiceDialog(Long id)
    {
        MedicalService selectedService = medicalServiceService.findMedicalServiceById(id).orElse(null);

        if (selectedService != null)
        {
            MedicalServiceDialog dialog = new MedicalServiceDialog(this, selectedService);
            dialog.setVisible(true);

            if (dialog.isConfirmed())
            {
                MedicalService updatedService = dialog.getMedicalService();
                medicalServiceService.saveMedicalService(updatedService);
                loadData();
            }
        }
    }
    private void deleteMedicalService(Long id)
    {
        int confirmed = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this service?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmed == JOptionPane.YES_OPTION)
        {
            medicalServiceService.deleteMedicalService(id);
            loadData();
        }
    }
    private void generateReport()
    {
        LocalDate startDate = startDatePicker.getDate();
        LocalDate endDate = endDatePicker.getDate();

        if (startDate == null || endDate == null)
        {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates",
                    "Missing Dates", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Appointment> appointments = reportService.getAppointmentsReport(startDateTime, endDateTime);

        reportTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Appointment appointment : appointments)
        {
            reportTableModel.addRow(new Object[]
                    {
                    appointment.getId(),
                    appointment.getPatientName(),
                    appointment.getDoctor().getName(),
                    appointment.getMedicalService().getName(),
                    appointment.getStartDateTime().format(formatter),
                    appointment.getStatus()
            });
        }
    }
    private void exportReportToCsv()
    {
        if (reportTableModel.getRowCount() == 0)
        {
            JOptionPane.showMessageDialog(this, "No data to export",
                    "Export Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.setSelectedFile(new File("appointments.csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv"))
            {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try {
                LocalDate startDate = startDatePicker.getDate();
                LocalDate endDate = endDatePicker.getDate();
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                List<Appointment> appointments = reportService.getAppointmentsReport(startDateTime, endDateTime);

                reportService.exportAppointmentsToCSV(appointments, fileToSave.getAbsolutePath());

                JOptionPane.showMessageDialog(this, "Export to CSV completed successfully!",
                        "Export Success", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(this, "Error exporting to CSV: " + e.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}