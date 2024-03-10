import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class Event {
    private int eventID;
    private String eventTitle;
    private String eventDate;
    private String eventLocation;
    private String eventDescription;
    private List<Participant> participants;

    // Constructor
    public Event(String eventTitle, String eventDate, String eventLocation, String eventDescription) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.participants = new ArrayList<>();
        this.eventDescription = eventDescription;
    }

    // Constructor
    public Event(int eventID, String eventTitle, String eventDate, String eventLocation, String eventDescription) {
        this.eventID = eventID;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.participants = new ArrayList<>();
    }

    // Getter methods
    public int getEventID() {
        return eventID;
    }

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

    // Method to add participant to event
    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    // Method to print event information
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

    // Setter method for eventLocation
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    // Setter method for eventDescription
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    // Setter method for eventDate
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    // Setter method for eventTitle
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    // Getter method for participants
    public List<Participant> getParticipants() {
        return participants;
    }

    public List<Participant> getParticipantsForEvent() throws SQLException {
        try {
            return Database.getParticipantsForEvent(this.eventID);
        } catch (SQLException e) {
            throw e;
        }
    }

    // Method to remove a participant from the event
    public void removeParticipant(int participantID) {
        participants.removeIf(participant -> participant.getParticipantID() == participantID);
    }

    // Method to update a participant within the event's participant list
    public void updateParticipant(Participant updatedParticipant) {
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getParticipantID() == updatedParticipant.getParticipantID()) {
                participants.set(i, updatedParticipant);
                return;
            }
        }
    }


    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
}
