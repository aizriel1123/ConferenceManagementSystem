import java.sql.SQLException;
import java.util.*;

public class EventManager {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("Welcome to Event Manager!");
            System.out.println("+---------------------------------------------+");
            System.out.println("|                  Main Menu                  |");
            System.out.println("+---------------------------------------------+");
            System.out.println("| Option |             Description            |");
            System.out.println("+--------+------------------------------------+");
            System.out.println("|   1    |          Create Event              |");
            System.out.println("|   2    |          Update Event              |");
            System.out.println("|   3    |          Delete Event              |");
            System.out.println("|   4    |    Edit Event Participants         |");
            System.out.println("|   5    |      Register Participant          |");
            System.out.println("|   6    |       Remove Participant           |");
            System.out.println("|   7    |          Search Event              |");
            System.out.println("|   8    |        View All Events             |");
            System.out.println("|   9    |    Find Participants for Event     |");
            System.out.println("|   10   |               Exit                 |");
            System.out.println("+--------+------------------------------------+");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (option) {
                case 1:
                    createEvent(scanner);
                    break;
                case 2:
                    updateEvent(scanner);
                    break;
                case 3:
                    deleteEvent(scanner);
                    break;
                case 4:
                    editEventParticipants(scanner);
                    break;
                case 5:
                    registerParticipant(scanner);
                    break;
                case 6:
                    removeParticipant(scanner);
                    break;
                case 7:
                    searchEvent(scanner);
                    break;
                case 8:
                    viewAllEvents();
                    break;
                case 9:
                    findParticipants(scanner);
                    break;
                case 10:
                    exit = true;
                    System.out.println("Exiting Event Manager. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    // Method to create a new event
    private static void createEvent(Scanner scanner) {
        try {
            System.out.println("+-----------------------------------------+");
            System.out.println("|            Create New Event             |");
            System.out.println("+-----------------------------------------+");
            System.out.print("Enter Event Title: ");
            String eventTitle = scanner.nextLine();
            if (eventTitle.isEmpty()) {
                throw new IllegalArgumentException("Event title cannot be empty.");
            }

            List<Event> existingEvents = Database.getEvents();
            for (Event existingEvent : existingEvents) {
                if (existingEvent.getEventTitle().equalsIgnoreCase(eventTitle)) {
                    throw new IllegalArgumentException("An event with the same title already exists.");
                }
            }

            System.out.print("Enter Event Date (YYYY-MM-DD): ");
            String eventDate = scanner.nextLine();
            if (!eventDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD format.");
            }

            System.out.print("Enter Event Location: ");
            String eventLocation = scanner.nextLine();
            if (eventLocation.isEmpty()) {
                throw new IllegalArgumentException("Event location cannot be empty.");
            }

            System.out.print("Enter Event Description (optional): ");
            String eventDescription = scanner.nextLine();
            // If event description is blank, set it to 'none'
            if (eventDescription == null || eventDescription.trim().isEmpty()) {
                eventDescription = "none";
            }

            Event event = new Event(eventTitle, eventDate, eventLocation, eventDescription);

            boolean addingParticipants = true;
            while (addingParticipants) {
                System.out.print("Add Participant (y/n)? ");
                String addParticipantChoice = scanner.nextLine().toLowerCase();
                if (addParticipantChoice.equals("n")) {
                    addingParticipants = false;
                } else if (addParticipantChoice.equals("y")) {
                    System.out.println("+-------------------------------------+");
                    System.out.println("|         Add New Participant         |");
                    System.out.println("+-------------------------------------+");
                    System.out.print("Enter Participant Name: ");
                    String participantName = scanner.nextLine();
                    if (participantName.isEmpty()) {
                        throw new IllegalArgumentException("Participant name cannot be empty.");
                    }

                    String participantEmail;
                    do {
                        System.out.print("Enter Participant Email (optional): ");
                        participantEmail = scanner.nextLine();
                    } while (!isValidEmail(participantEmail) && !participantEmail.isEmpty());

                    for (Participant existingParticipant : event.getParticipants()) {
                        if (existingParticipant.getParticipantName().equalsIgnoreCase(participantName)) {
                            throw new IllegalArgumentException("Participant with the same name already registered for this event.");
                        }
                        if (participantEmail != null && !participantEmail.isEmpty() && participantEmail.equals(existingParticipant.getParticipantEmail())) {
                            throw new IllegalArgumentException("Participant with the same email already registered for this event.");
                        }
                    }

                    Participant participant = new Participant(0, participantName, participantEmail);
                    event.addParticipant(participant);
                } else {
                    throw new IllegalArgumentException("Invalid input. Please enter 'y' or 'n'.");
                }
            }

            int eventId = Database.addEventData(event); // Capture the generated event ID
            System.out.println("Event successfully saved!");

            // Display created event details
            System.out.println("Created Event Details:");
            System.out.println("+----+---------------------+------------+---------------+-------------------+");
            System.out.println("| ID |      Event Title    |    Date    |    Location   |    Description    |");
            System.out.println("+----+---------------------+------------+---------------+-------------------+");
            System.out.printf("| %-3d| %-20s| %-11s| %-14s| %-18s|%n",
                    eventId, event.getEventTitle(), event.getEventDate(),
                    event.getEventLocation(), event.getEventDescription());
            System.out.println("+----+---------------------+------------+---------------+-------------------+");

        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // Method to update an event
    private static void updateEvent(Scanner scanner) {
        try {
            System.out.println("+-----------------------------------------+");
            System.out.println("|             Update Event                |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Enter the ID of the event you want to update:");
            displayEventTable(Database.getEvents());
            System.out.print("Choose an option: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Event event = Database.getEventByID(eventID);
            if (event != null) {
                // Display event details before update
                System.out.println("Event Details (Before Update):");
                System.out.println("+----+---------------------+------------+---------------+-------------------+");
                System.out.println("| ID |      Event Title    |    Date    |    Location   |    Description    |");
                System.out.println("+----+---------------------+------------+---------------+-------------------+");
                System.out.printf("| %-3d| %-20s| %-11s| %-14s| %-18s|%n",
                        event.getEventID(), event.getEventTitle(), event.getEventDate(),
                        event.getEventLocation(), event.getEventDescription());
                System.out.println("+----+---------------------+------------+---------------+-------------------+");

                // Prompt user for updated event details
                System.out.print("Enter updated Event Title (Press Enter to keep current value): ");
                String eventTitle = scanner.nextLine();

                System.out.print("Enter updated Event Date (YYYY-MM-DD) (Press Enter to keep current value): ");
                String eventDate = scanner.nextLine();
                if (!eventDate.isEmpty() && !eventDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD format.");
                }

                System.out.print("Enter updated Event Location (Press Enter to keep current value): ");
                String eventLocation = scanner.nextLine();

                System.out.print("Enter updated Event Description (Press Enter to keep current value): ");
                String eventDescription = scanner.nextLine();

                // Update event fields
                if (!eventTitle.isEmpty()) {
                    event.setEventTitle(eventTitle);
                }
                if (!eventDate.isEmpty()) {
                    event.setEventDate(eventDate);
                }
                if (!eventLocation.isEmpty()) {
                    event.setEventLocation(eventLocation);
                }
                // Update description only if provided
                if (!eventDescription.isEmpty()) {
                    event.setEventDescription(eventDescription);
                }

                // Update event in the database
                Database.updateEvent(event);
                System.out.println("Event updated successfully!");

                // Display updated event details
                System.out.println("Updated Event Details:");
                System.out.println("+----+---------------------+------------+---------------+-------------------+");
                System.out.println("| ID |      Event Title    |    Date    |    Location   |    Description    |");
                System.out.println("+----+---------------------+------------+---------------+-------------------+");
                System.out.printf("| %-3d| %-20s| %-11s| %-14s| %-18s|%n",
                        event.getEventID(), event.getEventTitle(), event.getEventDate(),
                        event.getEventLocation(), event.getEventDescription());
                System.out.println("+----+---------------------+------------+---------------+-------------------+");
            } else {
                System.out.println("Event not found!");
            }
        } catch (InputMismatchException | IllegalArgumentException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // Method to delete an event
    private static void deleteEvent(Scanner scanner) {
        try {
            System.out.println("+-----------------------------------------+");
            System.out.println("|             Delete Event                |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Enter the ID of the event you want to delete (type 'exit' to cancel):");
            String userInput = scanner.nextLine().trim();
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Delete event operation canceled.");
                return; // Exit the method
            }
            displayEventTable(Database.getEvents());
            System.out.print("Choose an option: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Are you sure you want to delete this event? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                if (Database.deleteEvent(eventID)) {
                    System.out.println("Event deleted successfully!");
                } else {
                    System.out.println("Event not found!");
                }
            } else {
                System.out.println("Deletion canceled.");
            }
        } catch (InputMismatchException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Method to edit participants of an event
    private static void editEventParticipants(Scanner scanner) {
        try {
            System.out.println("+-----------------------------------------+");
            System.out.println("|      Edit Event Participants            |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Enter the ID of the event you want to edit participants for:");
            displayEventTable(Database.getEvents());
            System.out.print("Choose an option: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Event event = Database.getEventByID(eventID);
            if (event != null) {
                displayParticipantsTable(event.getParticipants());
                System.out.print("Enter the ID of the participant you want to edit: ");
                int participantID = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                // Find the participant to edit
                Participant participantToEdit = null;
                for (Participant participant : event.getParticipants()) {
                    if (participant.getParticipantID() == participantID) {
                        participantToEdit = participant;
                        break;
                    }
                }

                if (participantToEdit != null) {
                    System.out.println("Editing Participant:");
                    displayParticipantTable(participantToEdit);
                    System.out.print("Enter updated Participant Name (Press Enter to keep current value): ");
                    String participantName = scanner.nextLine();

                    String participantEmail;
                    do {
                        System.out.print("Enter updated Participant Email (Press Enter to keep current value): ");
                        participantEmail = scanner.nextLine();
                        if (!participantEmail.isEmpty() && !isValidEmail(participantEmail)) {
                            throw new IllegalArgumentException("Invalid email format. Please enter a valid email or leave it empty.");
                        }
                    } while (!participantEmail.isEmpty() && !isValidEmail(participantEmail));

                    // Update participant in the event's participant list and the database
                    String updatedName = participantName.isEmpty() ? participantToEdit.getParticipantName() : participantName;
                    String updatedEmail = participantEmail.isEmpty() ? participantToEdit.getParticipantEmail() : participantEmail;
                    participantToEdit.setParticipantName(updatedName);
                    participantToEdit.setParticipantEmail(updatedEmail);
                    event.updateParticipant(participantToEdit);
                    Database.updateParticipant(participantToEdit);
                    System.out.println("Participant updated successfully!");
                } else {
                    System.out.println("Participant not found!");
                }
            } else {
                System.out.println("Event not found!");
            }
        } catch (InputMismatchException | IllegalArgumentException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // Method to register a participant for an event
    private static void registerParticipant(Scanner scanner) {
        try {
            System.out.println("+-----------------------------------------+");
            System.out.println("|        Register New Participant         |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Available Events:");
            List<Event> events = Database.getEvents();
            displayEventTable(events);
            System.out.print("Enter the ID of the event you want to register a participant for: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Event event = Database.getEventByID(eventID);
            if (event != null) {
                System.out.print("Enter Participant Name: ");
                String participantName = scanner.nextLine();
                if (participantName.isEmpty()) {
                    throw new IllegalArgumentException("Participant name cannot be empty.");
                }

                // Check if participant name already exists for the event
                for (Participant existingParticipant : event.getParticipants()) {
                    if (existingParticipant.getParticipantName().equalsIgnoreCase(participantName)) {
                        throw new IllegalArgumentException("Participant with the same name already registered for this event.");
                    }
                }

                System.out.print("Enter Participant Email (optional): ");
                String participantEmail = scanner.nextLine();
                if (!participantEmail.isEmpty() && !isValidEmail(participantEmail)) {
                    throw new IllegalArgumentException("Invalid email format. Please enter a valid email or leave it empty.");
                }

                // Check if participant email already exists for the event
                if (participantEmail != null && !participantEmail.isEmpty()) {
                    for (Participant existingParticipant : event.getParticipants()) {
                        if (participantEmail.equals(existingParticipant.getParticipantEmail())) {
                            throw new IllegalArgumentException("Participant with the same email already registered for this event.");
                        }
                    }
                }

                Participant participant = new Participant(0, participantName, participantEmail);

                int participantID = Database.addParticipant(participant);
                participant.setParticipantID(participantID);
                event.addParticipant(participant);
                Database.updateEventParticipants(eventID, event.getParticipants());
                System.out.println("Participant registered successfully!");
            } else {
                System.out.println("Event not found!");
            }
        } catch (InputMismatchException | IllegalArgumentException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Method to remove a participant from an event
    private static void removeParticipant(Scanner scanner) {
        try {
            System.out.println("+-----------------------------------------+");
            System.out.println("|      Remove Participant From Event      |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Available Events:");
            List<Event> events = Database.getEvents();
            displayEventTable(events);
            System.out.print("Enter the ID of the event you want to remove a participant from: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Event event = Database.getEventByID(eventID);
            if (event != null) {
                displayParticipantsTable(event.getParticipants());
                System.out.print("Enter the ID of the participant you want to remove: ");
                int participantID = scanner.nextInt();
                scanner.nextLine(); // Consume newline


                if (Database.deleteParticipant(participantID)) {
                    event.removeParticipant(participantID);
                    Database.updateEventParticipants(eventID, event.getParticipants());
                    System.out.println("Participant removed successfully!");
                } else {
                    System.out.println("Participant not found in the event!");
                }
            } else {
                System.out.println("Event not found!");
            }
        } catch (InputMismatchException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Method to search for an event
    private static void searchEvent(Scanner scanner) {
        try {
            System.out.println("+-----------------------------------------+");
            System.out.println("|             Search Event                |");
            System.out.println("+-----------------------------------------+");
            System.out.println("Enter search criteria:");
            System.out.println("1. Search by title");
            System.out.println("2. Search by date");
            System.out.println("3. Search by location");
            System.out.println("4. Search by ID");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            List<Event> searchResults = new ArrayList<>();

            switch (choice) {
                case 1: // Search by title
                    System.out.print("Enter event title to search: ");
                    String title = scanner.nextLine();
                    searchResults = Database.searchEventByTitle(title);
                    break;
                case 2: // Search by date
                    System.out.print("Enter event date to search (YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    searchResults = Database.searchEventByDate(date);
                    break;
                case 3: // Search by location
                    System.out.print("Enter event location to search: ");
                    String location = scanner.nextLine();
                    searchResults = Database.searchEventByLocation(location);
                    break;
                case 4: // Search by ID
                    System.out.print("Enter event ID to search: ");
                    int eventID = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    Event event = Database.getEventByID(eventID);
                    if (event != null) {
                        searchResults.add(event);
                    }
                    break;
                default:
                    System.out.println("Invalid input. Please enter a valid number.");
                    return;
            }

            if (!searchResults.isEmpty()) {
                System.out.println("Search Results:");
                displaySearchResults(searchResults);
                displayParticipantsForSearchResults(searchResults);
            } else {
                System.out.println("No events found matching the search criteria.");
            }
        } catch (InputMismatchException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void viewAllEvents() {
        try {
            List<Event> events = Database.getEvents();
            System.out.println("+----+---------------------+------------+---------------+-------------------+");
            System.out.println("| ID |      Event Title    |    Date    |    Location   |    Description    |");
            System.out.println("+----+---------------------+------------+---------------+-------------------+");
            for (Event event : events) {
                System.out.printf("| %-3d| %-20s| %-11s| %-14s| %-18s|%n",
                        event.getEventID(), event.getEventTitle(), event.getEventDate(),
                        event.getEventLocation(), event.getEventDescription());
            }
            System.out.println("+----+---------------------+------------+---------------+-------------------+");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to find participants for an event
    private static void findParticipants(Scanner scanner) {
        try {
            System.out.println("+-----------------------------------------+");
            System.out.println("|      Find Participants for Event        |");
            System.out.println("+-----------------------------------------+");
            List<Event> events = Database.getEvents();
            displayEventTable(events);
            System.out.print("Enter the ID of the event to find participants for: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Event event = Database.getEventByID(eventID);
            if (event != null) {
                List<Participant> participants = Database.getParticipantsByEventID(eventID);
                displayParticipantsTable(participants);
            } else {
                System.out.println("Event not found!");
            }
        } catch (InputMismatchException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to display event details
    private static void displayEventDetails(Event event) {
        System.out.println("Event Details:");
        System.out.println("+----+---------------------+");
        System.out.println("| ID |      Event Title    |");
        System.out.println("+----+---------------------+");
        System.out.printf("| %-3d| %-20s|%n", event.getEventID(), event.getEventTitle());
        System.out.println("+----+---------------------+");
    }

    // Method to display a table of events
    private static void displayEventTable(List<Event> events) {
        System.out.println("+----+---------------------+");
        System.out.println("| ID |      Event Title    |");
        System.out.println("+----+---------------------+");
        for (Event event : events) {
            System.out.printf("| %-3d| %-20s|%n", event.getEventID(), event.getEventTitle());
        }
        System.out.println("+----+---------------------+");
    }

    // Method to display participants table for an event
    private static void displayParticipantsTable(List<Participant> participants) {
        System.out.println("Participants:");
        System.out.println("+----+----------------------+----------------------+");
        System.out.println("| ID |     Participant      |    Participant Email |");
        System.out.println("+----+----------------------+----------------------+");
        for (Participant participant : participants) {
            System.out.printf("| %-3d| %-20s| %-20s|%n",
                    participant.getParticipantID(), participant.getParticipantName(), participant.getParticipantEmail());
        }
        System.out.println("+----+----------------------+----------------------+");
    }

    // Method to display a participant table
    private static void displayParticipantTable(Participant participant) {
        System.out.println("Editing Participant:");
        System.out.println("+----+----------------------+----------------------+");
        System.out.println("| ID |   Participant Name   |    Participant Email |");
        System.out.println("+----+----------------------+----------------------+");
        System.out.printf("| %-3d| %-20s| %-20s|%n",
                participant.getParticipantID(), participant.getParticipantName(), participant.getParticipantEmail());
        System.out.println("+----+----------------------+----------------------+");
    }

    // Method to display search results
    private static void displaySearchResults(List<Event> searchResults) {
        System.out.println("+----------------------+------------------+------------------+");
        System.out.println("| Event Title          | Event Date       | Event Location   |");
        System.out.println("+----------------------+------------------+------------------+");
        for (Event result : searchResults) {
            System.out.printf("| %-20s| %-16s| %-16s|%n",
                    result.getEventTitle(), result.getEventDate(), result.getEventLocation());
        }
        System.out.println("+----------------------+------------------+------------------+");
    }

    // Method to display participants for search results
    private static void displayParticipantsForSearchResults(List<Event> searchResults) {
        for (Event result : searchResults) {
            System.out.println("Participants:");
            displayParticipantsTable(result.getParticipants());
        }
    }

    private static boolean isValidEmail(String email) throws IllegalArgumentException {
        if (email.isEmpty()) {
            return true;
        }
        if (email.matches("^[a-zA-Z0-9_+&*-]+@(gmail|yahoo|outlook|hotmail)\\.com$")) {
            return true;
        } else {
            throw new IllegalArgumentException("Invalid email format. Only domains like @gmail.com, @yahoo.com, @outlook.com, and @hotmail.com are allowed.");
        }
    }


}




