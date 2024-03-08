import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Participant {
    private int participantID;
    private String participantName;
    private String participantEmail;

    public Participant(int participantID, String participantName, String participantEmail) {
        this.participantID = participantID;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
    }

    public void printInfo() {
        System.out.println("  Participant ID: " + participantID);
        System.out.println("  Participant Name: " + participantName);
        if (participantEmail != null && !participantEmail.isEmpty()) {
            System.out.println("  Participant Email: " + participantEmail);
        }
    }

    // Method to validate participant name
    private String validateName(String name) throws Exception {
        if (!name.matches("[a-zA-Z\\s]+")) {
            throw new Exception("Invalid participant name. Only alphabets and spaces are allowed.");
        }
        return name;
    }

    // Method to validate participant email
    private String validateEmail(String email) throws Exception {
        if (email != null && !email.isEmpty()) {
            if (!email.matches("\\b[A-Za-z0-9._%+-]+@(?:gmail|yahoo|rocketmail|hotmail|outlook)\\.com\\b")) {
                throw new Exception("Invalid email format. Please use valid email domain (gmail.com, yahoo.com, rocketmail.com, hotmail.com, outlook.com).");
            }
        }
        return email;
    }
}

class Event {
    private int eventID;
    private String eventTitle;
    private String eventDate;
    private String eventLocation;
    private ArrayList<Participant> participants;
    private String eventDescription;

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public Event(String eventTitle, String eventDate, String eventLocation, String eventDescription) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.participants = new ArrayList<Participant>();
        this.eventDescription = eventDescription;
    }

    public Event(int eventID, String eventTitle, String eventDate, String eventLocation, String eventDescription) {
        this.eventID = eventID;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.participants = new ArrayList<Participant>();
        this.eventDescription = eventDescription;
    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    public void printInfo() {
        System.out.println("Event Title: " + eventTitle);
        System.out.println("Event Date: " + eventDate);
        System.out.println("Event Location: " + eventLocation);
        if (eventDescription != null && !eventDescription.isEmpty()) {
            System.out.println("Event Description: " + eventDescription);
        }
        System.out.println("Participants:");
        for (Participant participant : participants) {
            participant.printInfo();
        }
    }

    // Method to validate date format (unchanged)
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Event Title: ");
        String eventTitle = scanner.nextLine();
        System.out.print("Enter Event Date (YYYY-MM-DD): ");
        String eventDate = scanner.nextLine();
        System.out.print("Enter Event Location: ");
        String eventLocation = scanner.nextLine();
        System.out.print("Enter Event Description (optional): ");
        String eventDescription = scanner.nextLine();

        Event event = new Event(eventTitle, eventDate, eventLocation, eventDescription);

        boolean addingParticipants = true;
        while (addingParticipants) {
            System.out.print("Add Participant (y/n)? ");
            String addParticipantChoice = scanner.nextLine().toLowerCase();
            if (addParticipantChoice.equals("n")) {
                addingParticipants = false;
            } else {
                System.out.print("Enter Participant Name: ");
                String participantName = scanner.nextLine();
                System.out.print("Enter Participant Email (optional): ");
                String participantEmail = scanner.nextLine();
                Participant participant = new Participant(0, participantName, participantEmail);
                event.addParticipant(participant);
            }
        }

        event.printInfo();

        // Option to save the event to the database
        System.out.print("Save Event to Database (y/n)? ");
        String saveEventChoice = scanner.nextLine().toLowerCase();
        if (saveEventChoice.equals("y")) {
            try {
                Database.addEventData(event);
                System.out.println("Event successfully saved!");
            } catch (SQLException e) {
                System.out.println("Error: Failed to save event to database.");
                e.printStackTrace();
            }
        }

        try {
            List<Event> events = Database.getEvents();
            for (Event dbEvent : events) {
                dbEvent.printInfo();
            }
        } catch (SQLException e) {
            System.out.println("Error: Failed to retrieve events from the database.");
            e.printStackTrace();
        }

    }
}



