package com.medcare.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

//clasa pentru serviciile medicale ale clinicii
@Entity
@Table(name = "medical_services")
public class MedicalService
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //informatii despre numele serviciului, pret si durata
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Duration duration;

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MedicalService that = (MedicalService) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}