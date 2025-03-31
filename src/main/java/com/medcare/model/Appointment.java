package com.medcare.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

//clasa cu programrile clinicii
@Entity
@Table(name = "appointments")
public class Appointment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //pastrez informatii despre pacient, doctor, serviciu, ora de inceput/sfarsit a programarii
    @Column(nullable = false)
    private String patientName;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private MedicalService medicalService;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    //ennumerare interna pentru a stii statusul programrii
    public enum AppointmentStatus {
        NEW, IN_PROGRESS, COMPLETED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public MedicalService getMedicalService() {
        return medicalService;
    }

    public void setMedicalService(MedicalService medicalService) {
        this.medicalService = medicalService;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(patientName, that.patientName) &&
                Objects.equals(startDateTime, that.startDateTime) &&
                Objects.equals(doctor, that.doctor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, patientName, startDateTime, doctor);
    }
}