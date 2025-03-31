package com.medcare.model;
import jakarta.persistence.Embeddable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

//clasa in care pastrez orele de lucru ale medicilor
@Embeddable
public class WorkingHours
{
    //am ziua saptamnii, ora la care incepe sa lucreze medicul si la care termina
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public WorkingHours() {}

    public WorkingHours(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime)
    {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek)
    {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WorkingHours that = (WorkingHours) o;
        return dayOfWeek == that.dayOfWeek &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }
    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime);
    }
}