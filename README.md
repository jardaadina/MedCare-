# MedCare - Sistem de Management pentru Clinici Medicale

##  Descriere

MedCare este o aplicație desktop dezvoltată pentru gestionarea eficientă a programărilor pacienților într-o clinică medicală. Sistemul permite înregistrarea și gestionarea consultațiilor realizate de medici, oferind o experiență fluidă atât pentru personalul administrativ cât și pentru recepționiști.

## Tehnologii Utilizate

- **Backend**: Java 17, Spring Boot 3.4.4
- **Frontend**: Java Swing
- **Persistența datelor**: Spring Data JPA, Hibernate, MySQL
- **Securitate**: Spring Security, BCrypt pentru criptarea parolelor

## Funcționalități

### Autentificare și Autorizare
- Acces securizat bazat pe login
- Două tipuri de utilizatori: administratori și recepționiști
- Parole stocate criptat în baza de date

### Administratori
- Gestionarea conturilor pentru recepționiști
- Administrarea listei de medici (adăugare, editare, ștergere)
- Gestionarea serviciilor medicale oferite de clinică
- Generarea și vizualizarea rapoartelor și statisticilor
- Exportarea rapoartelor în format CSV sau XML

### Recepționiști
- Gestionarea programărilor pacienților
- Verificarea disponibilității medicilor
- Actualizarea statusului programărilor
- Căutarea și filtrarea programărilor

### Gestionarea Medicilor
- Înregistrarea informațiilor despre medici
- Definirea programului de lucru pentru fiecare medic
- Verificarea disponibilității pentru programări

### Servicii Medicale
- Înregistrarea serviciilor oferite de clinică
- Definirea prețului și duratei fiecărui serviciu

### Programări
- Înregistrarea și gestionarea programărilor
- Evitarea suprapunerilor în programări
- Actualizarea statusului programărilor

### Rapoarte și Statistici
- Generarea rapoartelor cu programări între date calendaristice
- Statistici privind cei mai solicitați medici
- Statistici privind cele mai solicitate servicii medicale

##  Arhitectură

Aplicația este structurată pe arhitectură stratificată (Layers):
- **Presentation Layer**: Interfața utilizator dezvoltată în Java Swing
- **Business Logic Layer**: Servicii care implementează logica de business
- **Data Access Layer**: Repository-uri care facilitează accesul la date
- **Data Layer**: Entitățile de bază și modelul de date

##  Structura Proiectului

```
com.medcare
├── model        # Entitățile de business
├── repository   # Interfețe pentru accesul la date
├── service      # Servicii care implementează logica de business
├── ui           # Componente de interfață utilizator
└── config       # Configurări Spring
```

##  Dezvoltări Viitoare

- Implementarea unui modul pentru gestionarea pacienților
- Adăugarea unui modul de facturare
- Implementarea unui sistem de notificări pentru programări
