package com.medcare.service;
import com.medcare.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

//gestioneaza rapoartele despre programari
@Service
public class ReportService
{
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final MedicalServiceService medicalServiceService;

    @Autowired
    public ReportService(
            AppointmentService appointmentService,
            DoctorService doctorService,
            MedicalServiceService medicalServiceService)
    {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.medicalServiceService = medicalServiceService;
    }

    public List<Appointment> getAppointmentsReport(LocalDateTime startDate, LocalDateTime endDate)
    {
        return appointmentService.findAppointmentsByDateRange(startDate, endDate);
    }

    public List<Object[]> getMostRequestedDoctorsReport()
    {
        return doctorService.findMostRequestedDoctors();
    }

    public List<Object[]> getMostRequestedServicesReport()
    {
        return medicalServiceService.findMostRequestedServices();
    }

    //exportare raport in format cvs
    public void exportAppointmentsToCSV(List<Appointment> appointments, String filePath) throws IOException
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (FileWriter writer = new FileWriter(filePath))
        {
            writer.append("ID,Patient Name,Doctor,Medical Service,Start Date/Time,End Date/Time,Status\n");

            for (Appointment appointment : appointments)
            {
                writer.append(String.valueOf(appointment.getId())).append(",")
                        .append(appointment.getPatientName()).append(",")
                        .append(appointment.getDoctor().getName()).append(",")
                        .append(appointment.getMedicalService().getName()).append(",")
                        .append(appointment.getStartDateTime().format(formatter)).append(",")
                        .append(appointment.getEndDateTime().format(formatter)).append(",")
                        .append(appointment.getStatus().toString()).append("\n");
            }
        }
    }
}