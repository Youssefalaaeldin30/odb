package hospital_odb;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class HospitalApp {

    // Method to fetch all patients
    public static void fetchAllPatients(EntityManager em) {
        TypedQuery<Patient> query = em.createQuery("SELECT p FROM Patient p", Patient.class);
        List<Patient> patients = query.getResultList();

        for (Patient patient : patients) {
            System.out.println("Patient ID: " + patient.getId());
            System.out.println("Name: " + patient.getName());
            System.out.println("Age: " + patient.getAge());
            System.out.println("Address: " + patient.getAddress());
            System.out.println("----------------------------");
        }
    }

    // Method to fetch doctors by specialization
    public static void fetchDoctorsBySpecialization(EntityManager em, String specialization) {
        TypedQuery<Doctor> query = em.createQuery("SELECT d FROM Doctor d WHERE d.specialization = :specialization", Doctor.class);
        query.setParameter("specialization", specialization);
        List<Doctor> doctors = query.getResultList();

        for (Doctor doctor : doctors) {
            System.out.println("Doctor ID: " + doctor.getId());
            System.out.println("Name: " + doctor.getName());
            System.out.println("Specialization: " + doctor.getSpecialization());
            System.out.println("----------------------------");
        }
    }

    // Method to fetch appointments by patient name
    public static void fetchAppointmentsByPatientName(EntityManager em, String patientName) {
        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a WHERE a.patient.name = :name", Appointment.class
        );
        query.setParameter("name", patientName);
        List<Appointment> appointments = query.getResultList();

        for (Appointment appointment : appointments) {
            System.out.println("Appointment ID: " + appointment.getId());
            System.out.println("Date: " + appointment.getDate());
            System.out.println("Doctor: " + appointment.getDoctor().getName());
            System.out.println("Patient: " + appointment.getPatient().getName());
            System.out.println("----------------------------");
        }
    }

    // Method to add a new patient
    public static void addPatient(EntityManager em, String name, int age, String address) {
        try {
            em.getTransaction().begin();
            Patient patient = new Patient(name, age, address);
            em.persist(patient);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }


    // Method to add a new doctor
    public static void addDoctor(EntityManager em, String name, String specialization) {
        em.getTransaction().begin();
        Doctor doctor = new Doctor(name, specialization);
        em.persist(doctor);
        em.getTransaction().commit();
    }

    // Method to add a new appointment
    public static void addAppointment(EntityManager em, Date date, String patientName, String doctorName) {
        em.getTransaction().begin();
        TypedQuery<Patient> patientQuery = em.createQuery("SELECT p FROM Patient p WHERE p.name = :name", Patient.class);
        patientQuery.setParameter("name", patientName);
        Patient patient = patientQuery.getSingleResult();

        TypedQuery<Doctor> doctorQuery = em.createQuery("SELECT d FROM Doctor d WHERE d.name = :name", Doctor.class);
        doctorQuery.setParameter("name", doctorName);
        Doctor doctor = doctorQuery.getSingleResult();

        Appointment appointment = new Appointment(date, patient, doctor);
        em.persist(appointment);
        em.getTransaction().commit();
    }

    // Method to delete a patient by ID
    public static void deletePatient(EntityManager em, Long patientId) {
        em.getTransaction().begin();
        Patient patient = em.find(Patient.class, patientId);
        if (patient != null) {
            em.remove(patient);
        }
        em.getTransaction().commit();
    }

    // Method to delete a doctor by ID
    public static void deleteDoctor(EntityManager em, Long doctorId) {
        em.getTransaction().begin();
        Doctor doctor = em.find(Doctor.class, doctorId);
        if (doctor != null) {
            em.remove(doctor);
        }
        em.getTransaction().commit();
    }

    // Method to delete an appointment by ID
    public static void deleteAppointment(EntityManager em, Long appointmentId) {
        em.getTransaction().begin();
        Appointment appointment = em.find(Appointment.class, appointmentId);
        if (appointment != null) {
            em.remove(appointment);
        }
        em.getTransaction().commit();
    }

    // Main method
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hospital");
        EntityManager em = emf.createEntityManager();

        try {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("1. Add Patient");
                System.out.println("2. Add Doctor");
                System.out.println("3. Add Appointment");
                System.out.println("4. Fetch All Patients");
                System.out.println("5. Fetch Doctors by Specialization");
                System.out.println("6. Fetch Appointments by Patient Name");
                System.out.println("7. Delete Patient");
                System.out.println("8. Delete Doctor");
                System.out.println("9. Delete Appointment");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.print("Enter Name: ");
                        String name = scanner.next();
                        System.out.print("Enter Age: ");
                        int age = scanner.nextInt();
                        System.out.print("Enter Address: ");
                        String address = scanner.next();
                        addPatient(em, name, age, address);
                        break;
                    case 2:
                        System.out.print("Enter Name: ");
                        String doctorName = scanner.next();
                        System.out.print("Enter Specialization: ");
                        String specialization = scanner.next();
                        addDoctor(em, doctorName, specialization);
                        break;
                    case 3:
                        // Similar changes can be made to other operations
                        break;
                    case 0:
                        em.getTransaction().commit();
                        em.close();
                        emf.close();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }
}
