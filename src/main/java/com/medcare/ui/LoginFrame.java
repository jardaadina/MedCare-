package com.medcare.ui;
import com.medcare.model.Administrator;
import com.medcare.model.Receptionist;
import com.medcare.model.User;
import com.medcare.service.AdministratorService;
import com.medcare.service.DoctorService;
import com.medcare.service.MedicalServiceService;
import com.medcare.service.ReportService;
import com.medcare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

//autentificarea utilizatorilor si in functie de ce esti admin sau receptionist ti se decshide dashboard ul corespunzator
@Component
public class LoginFrame extends JFrame
{
    private final UserService userService;
    private final ApplicationContext applicationContext;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    @Autowired
    public LoginFrame(UserService userService, ApplicationContext applicationContext)
    {
        this.userService = userService;
        this.applicationContext = applicationContext;
        initializeUI();
    }

    private void initializeUI()
    {
        Color lightBlue = new Color(240, 248, 255);
        Color mediumBlue = new Color(66, 133, 244);
        Color darkBlue = new Color(25, 80, 180);

        setTitle("MedCare Clinic - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, lightBlue, 0, getHeight(), new Color(220, 240, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(15, 0, 5, 0));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("MedCare Clinic", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(darkBlue);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Medical Management System", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(mediumBlue);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(darkBlue);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 35));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mediumBlue, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(darkBlue);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mediumBlue, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(mediumBlue);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                loginButton.setBackground(darkBlue);
            }

            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                loginButton.setBackground(mediumBlue);
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(loginButton, gbc);

        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 MedCare Clinic", JLabel.CENTER);
        footerLabel.setForeground(mediumBlue);
        footerPanel.add(footerLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        add(mainPanel);

        loginButton.addActionListener(e -> attemptLogin());

        getRootPane().setDefaultButton(loginButton);
    }

    private void attemptLogin()
    {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter both username and password",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userService.checkCredentials(username, password))
        {
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent())
            {
                User user = userOpt.get();

                if (user instanceof Administrator)
                {
                    openAdminDashboard((Administrator) user);
                }
                else if (user instanceof Receptionist)
                {
                    openReceptionistDashboard((Receptionist) user);
                }
                dispose();
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Invalid username or password",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAdminDashboard(Administrator admin)
    {
        try
        {
            AdministratorService administratorService = applicationContext.getBean(AdministratorService.class);
            DoctorService doctorService = applicationContext.getBean(DoctorService.class);
            MedicalServiceService medicalServiceService = applicationContext.getBean(MedicalServiceService.class);
            ReportService reportService = applicationContext.getBean(ReportService.class);

            AdminDashboardFrame dashboard = new AdminDashboardFrame(
                    administratorService, doctorService, medicalServiceService, reportService
            );

            dashboard.setVisible(true);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error opening Admin Dashboard: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openReceptionistDashboard(Receptionist receptionist)
    {
        try
        {
            DoctorService doctorService = applicationContext.getBean(DoctorService.class);
            MedicalServiceService medicalServiceService = applicationContext.getBean(MedicalServiceService.class);

            ReceptionistDashboardFrame dashboard = applicationContext.getBean(ReceptionistDashboardFrame.class);
            dashboard.setReceptionist(receptionist);
            dashboard.setVisible(true);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error opening Receptionist Dashboard: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}