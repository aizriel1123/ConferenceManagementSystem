
class Participant {
    private int participantID;
    private String participantName;
    private String participantEmail;

    // Constructor
    public Participant(int participantID, String participantName, String participantEmail) {
        this.participantID = participantID;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
    }

    // Getter method for participantID
    public int getParticipantID() {
        return participantID;
    }

    // Method to print participant information
    public void printInfo() {
        System.out.println("  Participant ID: " + participantID);
        System.out.println("  Participant Name: " + participantName);
        if (participantEmail != null && !participantEmail.isEmpty()) {
            System.out.println("  Participant Email: " + participantEmail);
        }
    }

    // Getter and setter methods for participantName and participantEmail
    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }
    public void setParticipantID(int participantID) {
        this.participantID = participantID;
    }

    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
    }
}

