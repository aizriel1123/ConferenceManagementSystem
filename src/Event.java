import java.util.ArrayList;
import java.util.List;

class Event {
    private int eventID;
    private String eventTitle;
    private String eventDate;
    private String eventLocation;
    private String eventDescription;
    private List<Participant> participants;

    // Constructors
    public Event(String eventTitle, String eventDate, String eventLocation, String eventDescription) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.participants = new ArrayList<>();
    }

    public Event(int eventID, String eventTitle, String eventDate, String eventLocation, String eventDescription) {
        this.eventID = eventID;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.participants = new ArrayList<>();
    }

    // Getter and Setter methods
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

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    // Methods to manage participants
    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public void removeParticipant(int participantID) {
        for (Participant participant : participants) {
            if (participant.getParticipantID() == participantID) {
                participants.remove(participant);
                return;
            }
        }
    }

    public void updateParticipant(Participant updatedParticipant) {
        for (int i = 0; i < participants.size(); i++) {
            Participant participant = participants.get(i);
            if (participant.getParticipantID() == updatedParticipant.getParticipantID()) {
                participants.set(i, updatedParticipant);
                return;
            }
        }
    }
}