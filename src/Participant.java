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

    // Getter and Setter methods
    public int getParticipantID() {
        return participantID;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
    }

    public void setParticipantID(int participantID) {
        this.participantID = participantID;
    }
}
