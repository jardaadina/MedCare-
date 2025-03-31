package com.medcare.model;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//clasa ce reprezinta medicii din clinica
@Entity
@Table(name = "doctors")
public class Doctor
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //am date despre nume, specializare si program de lucru
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialization;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_schedule", joinColumns = @JoinColumn(name = "doctor_id"))
    private Set<WorkingHours> workingHours = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Set<WorkingHours> getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(Set<WorkingHours> workingHours) {
        this.workingHours = workingHours;
    }

    //metoda prin care verific daca medicul este disponibil sau nu
    public boolean isAvailable(DayOfWeek day, LocalTime startTime, LocalTime endTime)
    {
        return workingHours.stream()
                .anyMatch(wh -> wh.getDayOfWeek() == day &&
                        !startTime.isBefore(wh.getStartTime()) &&
                        !endTime.isAfter(wh.getEndTime()));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(id, doctor.id) && Objects.equals(name, doctor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}