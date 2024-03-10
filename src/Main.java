import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            try {
                System.out.println("");
                System.out.println("Choose an option:");
                System.out.println("+----+-------------------------+");
                System.out.println("| 1. | Create new event        |");
                System.out.println("| 2. | View all events         |");
                System.out.println("| 3. | Update an event         |");
                System.out.println("| 4. | Delete an event         |");
                System.out.println("| 5. | Edit Participants       |");
                System.out.println("| 6. | Register a participant  |");
                System.out.println("| 7. | Remove a participant    |");
                System.out.println("| 8. | Search for an event     |");
                System.out.println("| 9. | Search for a Participant|");
                System.out.println("| 10.| Exit                    |");
                System.out.println("+----+-------------------------+");
                System.out.print("Enter your choice: ");


                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        createEvent(scanner);
                        break;
                    case 2:
                        viewAllEvents();
                        break;
                    case 3:
                        updateEvent(scanner);
                        break;
                    case 4:
                        deleteEvent(scanner);
                        break;
                    case 5:
                        editEventParticipants(scanner);
                        break;
                    case 6:
                        registerParticipant(scanner);
                        break;
                    case 7:
                        removeParticipant(scanner);
                        break;
                    case 8:
                        searchEvent(scanner);
                        break;
                    case 9:
                        findParticipants(scanner);
                        break;
                    case 10:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 10.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input from the scanner
            }
        }

        scanner.close();
    }

    // Method to create a new event
    private static void createEvent(Scanner scanner) {
        try {
            System.out.print("Enter Event Title: ");
            String eventTitle = scanner.nextLine();
            if (eventTitle.isEmpty()) {
                throw new IllegalArgumentException("Event title cannot be empty.");
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

            Event event = new Event(eventTitle, eventDate, eventLocation, eventDescription);

            boolean addingParticipants = true;
            while (addingParticipants) {
                System.out.print("Add Participant (y/n)? ");
                String addParticipantChoice = scanner.nextLine().toLowerCase();
                if (addParticipantChoice.equals("n")) {
                    addingParticipants = false;
                } else if (addParticipantChoice.equals("y")) {
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

                    Participant participant = new Participant(0, participantName, participantEmail);
                    event.addParticipant(participant);
                } else {
                    throw new IllegalArgumentException("Invalid input. Please enter 'y' or 'n'.");
                }
            }

            Database.addEventData(event);
            System.out.println("Event successfully saved!");
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to update an event
    private static void updateEvent(Scanner scanner) {
        try {
            System.out.print("Enter the ID of the event you want to update: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Event event = Database.getEventByID(eventID);
            if (event != null) {
                System.out.println("+----+------------------------+");
                System.out.println("| ID |         Event          |");
                System.out.println("+----+------------------------+");
                System.out.println("| " + eventID + "  | " + event.getEventTitle() + " |");
                System.out.println("+----+------------------------+");

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
                // Always update eventDescription
                event.setEventDescription(eventDescription);

                if (!eventLocation.isEmpty()) {
                    event.setEventLocation(eventLocation);
                }

                Database.updateEvent(event);
                System.out.println("Event updated successfully!");
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
            System.out.print("Enter the ID of the event you want to delete: ");
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
            // Display all events (ID and title)
            System.out.println("Available Events:");
            System.out.println("+----+---------------------+");
            System.out.println("| ID |      Event Title    |");
            System.out.println("+----+---------------------+");
            List<Event> events = Database.getEvents();
            for (Event event : events) {
                System.out.printf("| %-3d| %-20s|%n", event.getEventID(), event.getEventTitle());
            }
            System.out.println("+----+---------------------+");

            System.out.print("Enter the ID of the event you want to edit participants for: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Event event = Database.getEventByID(eventID);
            if (event != null) {
                // Display participants table for the specified event
                System.out.println("Participants for Event ID " + eventID + ":");
                System.out.println("+----+----------------------+----------------------+");
                System.out.println("| ID |     Participant      |    Participant Email |");
                System.out.println("+----+----------------------+----------------------+");
                List<Participant> participants = event.getParticipants();
                for (Participant participant : participants) {
                    System.out.printf("| %-3d| %-20s| %-20s|%n",
                            participant.getParticipantID(), participant.getParticipantName(), participant.getParticipantEmail());
                }
                System.out.println("+----+----------------------+----------------------+");

                System.out.print("Enter the ID of the participant you want to edit: ");
                int participantID = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                // Find the participant to edit
                Participant participantToEdit = null;
                for (Participant participant : participants) {
                    if (participant.getParticipantID() == participantID) {
                        participantToEdit = participant;
                        break;
                    }
                }

                if (participantToEdit != null) {
                    System.out.println("Editing Participant:");
                    System.out.println("+----+----------------------+----------------------+");
                    System.out.println("| ID |   Participant Name   |    Participant Email |");
                    System.out.println("+----+----------------------+----------------------+");
                    System.out.printf("| %-3d| %-20s| %-20s|%n",
                            participantToEdit.getParticipantID(), participantToEdit.getParticipantName(), participantToEdit.getParticipantEmail());
                    System.out.println("+----+----------------------+----------------------+");

                    System.out.print("Enter updated Participant Name (Press Enter to keep current value): ");
                    String participantName = scanner.nextLine();

                    System.out.print("Enter updated Participant Email (Press Enter to keep current value): ");
                    String participantEmail = scanner.nextLine();

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
        } catch (InputMismatchException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Method to register a participant for an event
    private static void registerParticipant(Scanner scanner) {
        try {
            // Display all events (ID and title)
            System.out.println("Available Events:");
            System.out.println("+----+---------------------+");
            System.out.println("| ID |      Event Title    |");
            System.out.println("+----+---------------------+");
            List<Event> events = Database.getEvents();
            for (Event event : events) {
                System.out.printf("| %-3d| %-20s|%n", event.getEventID(), event.getEventTitle());
            }
            System.out.println("+----+---------------------+");

            System.out.print("Enter the ID of the event you want to register a participant for: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Event event = Database.getEventByID(eventID);
            if (event != null) {
                // Proceed with participant registration
                System.out.print("Enter Participant Name: ");
                String participantName = scanner.nextLine();
                if (participantName.isEmpty()) {
                    throw new IllegalArgumentException("Participant name cannot be empty.");
                }

                System.out.print("Enter Participant Email (optional): ");
                String participantEmail = scanner.nextLine();

                // Check if the participant is already registered for the event
                List<Participant> existingParticipants = event.getParticipants();
                for (Participant participant : existingParticipants) {
                    if (participant.getParticipantName().equalsIgnoreCase(participantName)) {
                        System.out.println("Participant with the same name already registered for this event.");
                        return;
                    }
                }

                // Create Participant object
                Participant participant = new Participant(0, participantName, participantEmail);

                // Call addParticipant function
                int participantID = Database.addParticipant(participant); // Update to get the participantID

                // Add participant to event and update event participants
                participant.setParticipantID(participantID); // Set the participantID
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
            System.out.print("Enter the ID of the event you want to remove a participant from: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Event event = Database.getEventByID(eventID);
            if (event != null) {
                // Display event details (only ID and title)
                System.out.println("Event Details:");
                System.out.println("+----+---------------------+");
                System.out.println("| ID |      Event Title    |");
                System.out.println("+----+---------------------+");
                System.out.printf("| %-3d| %-20s|%n", event.getEventID(), event.getEventTitle());
                System.out.println("+----+---------------------+");

                // Display participants table for the specified event
                System.out.println("Participants for Event ID " + eventID + ":");
                System.out.println("+----+----------------------+----------------------+");
                System.out.println("| ID |     Participant      |    Participant Email |");
                System.out.println("+----+----------------------+----------------------+");
                List<Participant> participants = event.getParticipants();
                for (Participant participant : participants) {
                    System.out.printf("| %-3d| %-20s| %-20s|%n",
                            participant.getParticipantID(), participant.getParticipantName(), participant.getParticipantEmail());
                }
                System.out.println("+----+----------------------+----------------------+");

                // Prompt for participant ID to remove
                System.out.print("Enter the ID of the participant you want to remove: ");
                int participantID = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                // Call deleteParticipant function
                if (Database.deleteParticipant(participantID)) {
                    // Remove participant from the event and update event participants
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
                System.out.println("+----------------------+------------------+------------------+");
                System.out.println("| Event Title          | Event Date       | Event Location   |");
                System.out.println("+----------------------+------------------+------------------+");
                for (Event result : searchResults) {
                    System.out.printf("| %-20s| %-16s| %-16s|%n",
                            result.getEventTitle(), result.getEventDate(), result.getEventLocation());
                }
                System.out.println("+----------------------+------------------+------------------+");

                // Display participants for each event in the search results
                for (Event result : searchResults) {
                    System.out.println("Participants:");
                    System.out.println("+----------------------+");
                    System.out.println("|     Participant      |");
                    System.out.println("+----------------------+");
                    List<Participant> participants = result.getParticipants();
                    for (Participant participant : participants) {
                        System.out.printf("| %-20s|%n", participant.getParticipantName());
                    }
                    System.out.println("+----------------------+");
                }
            } else {
                System.out.println("No events found matching the search criteria.");
            }
        } catch (InputMismatchException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void printEventInfoWithParticipants(Event event) {
        System.out.println("+----------------------+------------------+------------------+");
        System.out.println("| Event Title          | Event Date       | Event Location   |");
        System.out.println("+----------------------+------------------+------------------+");
        System.out.printf("| %-20s | %-16s | %-16s |%n",
                event.getEventTitle(), event.getEventDate(), event.getEventLocation());
        System.out.println("+----------------------+------------------+------------------+");
        System.out.println("| Participants                                          |");
        System.out.println("+-------------------------------------------------------+");
        for (Participant participant : event.getParticipants()) {
            System.out.printf("| %-53s |%n", participant.getParticipantName());
        }
        System.out.println("+-------------------------------------------------------+");
    }


    // Method to validate email format
    private static boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            return true;
        }
        return email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    }
    // Method to view all events
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
            System.out.print("Enter the ID of the event to find participants for: ");
            int eventID = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Event event = Database.getEventByID(eventID);
            if (event != null) {
                List<Participant> participants = Database.getParticipantsByEventID(eventID);
                System.out.println("+----+----------------------+-------------------+");
                System.out.println("| ID |    Participant Name  |  Participant Email|");
                System.out.println("+----+----------------------+-------------------+");
                for (Participant participant : participants) {
                    System.out.printf("| %-3d| %-20s| %-18s|%n",
                            participant.getParticipantID(), participant.getParticipantName(),
                            participant.getParticipantEmail());
                }
                System.out.println("+----+----------------------+-------------------+");
            } else {
                System.out.println("Event not found!");
            }
        } catch (InputMismatchException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Method to check if a participant belongs to a specific event
    private static boolean participantBelongsToEvent(Participant participant, int eventID) throws SQLException {
        List<Event> events = Database.getEvents();
        for (Event event : events) {
            if (event.getEventID() == eventID) {
                List<Participant> eventParticipants = event.getParticipants();
                for (Participant eventParticipant : eventParticipants) {
                    if (eventParticipant.getParticipantID() == participant.getParticipantID()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
