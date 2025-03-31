package com.medcare.model;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

//clasa care reprezinta utilizatorii cu rol de administrator in sistem
@Entity
@DiscriminatorValue("ADMIN")
public class Administrator extends User {}