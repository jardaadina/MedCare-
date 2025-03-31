package com.medcare.model;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

//clasa care extinde user si reprezinta utilizatorii cu rol de receptionist
@Entity
@DiscriminatorValue("RECEPTIONIST")
public class Receptionist extends User {}