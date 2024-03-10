import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {

    private static final String url = "jdbc:mysql://localhost:3306/db_event_registration";
    private static final String username = "root";
    private static final String password = "12345678";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: MySQL JDBC driver not found!");
        }
    }

    public static List<Event> getEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM event");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int eventID = resultSet.getInt("eventID");
                String eventTitle = resultSet.getString("eventTitle");
                String eventDate = resultSet.getString("eventDate");
                String eventLocation = resultSet.getString("eventLocation");
                String eventDescription = resultSet.getString("eventDescription");
                Event event = new Event(eventID, eventTitle, eventDate, eventLocation, eventDescription);
                events.add(event);
            }
        }
        return events;
    }

    public static void addEventData(Event event) throws SQLException {
        Connection connection = null;
        PreparedStatement eventStatement = null;
        PreparedStatement participantStatement = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            String eventSql = "INSERT INTO event (eventTitle, eventDate, eventLocation, eventDescription) VALUES (?, ?, ?, ?)";
            eventStatement = connection.prepareStatement(eventSql, PreparedStatement.RETURN_GENERATED_KEYS);
            eventStatement.setString(1, event.getEventTitle());
            eventStatement.setString(2, event.getEventDate());
            eventStatement.setString(3, event.getEventLocation());
            eventStatement.setString(4, event.getEventDescription());
            eventStatement.executeUpdate();

            ResultSet generatedKeys = eventStatement.getGeneratedKeys();
            int eventId = 0;
            if (generatedKeys.next()) {
                eventId = generatedKeys.getInt(1);
            }

            String participantSql = "INSERT INTO participant (eventID, participantName, participantEmail) VALUES (?, ?, ?)";
            participantStatement = connection.prepareStatement(participantSql);
            for (Participant participant : event.getParticipants()) {
                participantStatement.setInt(1, eventId);
                participantStatement.setString(2, participant.getParticipantName());
                participantStatement.setString(3, participant.getParticipantEmail());
                participantStatement.addBatch();
            }
            participantStatement.executeBatch();

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (eventStatement != null) {
                eventStatement.close();
            }
            if (participantStatement != null) {
                participantStatement.close();
            }
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    public static void updateEvent(Event event) throws SQLException {
        Connection connection = null;
        PreparedStatement eventStatement = null;
        PreparedStatement participantDeleteStatement = null;
        PreparedStatement participantInsertStatement = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            // Update event details
            String eventSql = "UPDATE event SET eventTitle=?, eventDate=?, eventLocation=?, eventDescription=? WHERE eventID=?";
            eventStatement = connection.prepareStatement(eventSql);
            eventStatement.setString(1, event.getEventTitle());
            eventStatement.setString(2, event.getEventDate());
            eventStatement.setString(3, event.getEventLocation());
            eventStatement.setString(4, event.getEventDescription());
            eventStatement.setInt(5, event.getEventID());
            eventStatement.executeUpdate();

            // Delete existing participants for the event
            String deleteSql = "DELETE FROM participant WHERE eventID=?";
            participantDeleteStatement = connection.prepareStatement(deleteSql);
            participantDeleteStatement.setInt(1, event.getEventID());
            participantDeleteStatement.executeUpdate();

            // Insert updated participants
            String participantSql = "INSERT INTO participant (eventID, participantName, participantEmail) VALUES (?, ?, ?)";
            participantInsertStatement = connection.prepareStatement(participantSql);
            for (Participant participant : event.getParticipants()) {
                participantInsertStatement.setInt(1, event.getEventID());
                participantInsertStatement.setString(2, participant.getParticipantName());
                participantInsertStatement.setString(3, participant.getParticipantEmail());
                participantInsertStatement.addBatch();
            }
            participantInsertStatement.executeBatch();

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            // Close resources
            if (eventStatement != null) {
                eventStatement.close();
            }
            if (participantDeleteStatement != null) {
                participantDeleteStatement.close();
            }
            if (participantInsertStatement != null) {
                participantInsertStatement.close();
            }
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }


    public static boolean deleteEvent(int eventID) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM event WHERE eventID=?")) {
            statement.setInt(1, eventID);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    public static Event getEventByID(int eventID) throws SQLException {
        Connection connection = null;
        PreparedStatement eventStatement = null;
        PreparedStatement participantStatement = null;
        ResultSet eventResultSet = null;
        ResultSet participantResultSet = null;

        try {
            connection = getConnection();

            String eventSql = "SELECT * FROM event WHERE eventID=?";
            eventStatement = connection.prepareStatement(eventSql);
            eventStatement.setInt(1, eventID);
            eventResultSet = eventStatement.executeQuery();

            Event event = null;
            if (eventResultSet.next()) {
                String eventTitle = eventResultSet.getString("eventTitle");
                String eventDate = eventResultSet.getString("eventDate");
                String eventLocation = eventResultSet.getString("eventLocation");
                String eventDescription = eventResultSet.getString("eventDescription");

                event = new Event(eventID, eventTitle, eventDate, eventLocation, eventDescription);

                // Fetch participants for the event
                List<Participant> participants = getParticipantsByEventID(eventID);
                for (Participant participant : participants) {
                    event.addParticipant(participant);
                }
            }

            return event;
        } finally {
            // Close resources
            if (eventResultSet != null) {
                eventResultSet.close();
            }
            if (participantResultSet != null) {
                participantResultSet.close();
            }
            if (eventStatement != null) {
                eventStatement.close();
            }
            if (participantStatement != null) {
                participantStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }


    public static List<Participant> getParticipantsForEvent(int eventID) throws SQLException {
        List<Participant> participants = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String sql = "SELECT * FROM participant WHERE eventID=?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, eventID);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int participantID = resultSet.getInt("participantID");
                String participantName = resultSet.getString("participantName");
                String participantEmail = resultSet.getString("participantEmail");
                Participant participant = new Participant(participantID, participantName, participantEmail);
                participants.add(participant);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return participants;
    }

    public static int addParticipant(Participant participant) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO participant (participantName, participantEmail) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, participant.getParticipantName());
            statement.setString(2, participant.getParticipantEmail());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Return the generated participantID
            } else {
                throw new SQLException("Failed to add participant, no ID obtained.");
            }
        }
    }


    public static void updateParticipant(Participant participant) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE participant SET participantName=?, participantEmail=? WHERE participantID=?")) {
            statement.setString(1, participant.getParticipantName());
            statement.setString(2, participant.getParticipantEmail());
            statement.setInt(3, participant.getParticipantID());
            statement.executeUpdate();
        }
    }

    public static boolean deleteParticipant(int participantID) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM participant WHERE participantID=?")) {
            statement.setInt(1, participantID);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    public static List<Participant> getAllParticipants() throws SQLException {
        List<Participant> participants = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM participant");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int participantID = resultSet.getInt("participantID");
                String participantName = resultSet.getString("participantName");
                String participantEmail = resultSet.getString("participantEmail");
                Participant participant = new Participant(participantID, participantName, participantEmail);
                participants.add(participant);
            }
        }
        return participants;
    }



    public static List<Event> searchEvents(int choice, String searchCriteria) throws SQLException {
        List<Event> searchResults = new ArrayList<>();

        try (Connection connection = getConnection()) {
            String sql = "";

            switch (choice) {
                case 1: // Search by title
                    sql = "SELECT * FROM event WHERE eventTitle LIKE ?";
                    break;
                case 2: // Search by date
                    sql = "SELECT * FROM event WHERE eventDate = ?";
                    break;
                case 3: // Search by location
                    sql = "SELECT * FROM event WHERE eventLocation LIKE ?";
                    break;
                case 4: // Search by ID
                    try {
                        int eventID = Integer.parseInt(searchCriteria);
                        sql = "SELECT * FROM event WHERE eventID = ?";
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid event ID format.");
                        return searchResults; // Return empty list if input is not a valid ID
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid search choice.");
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (choice == 4) {
                    try {
                        int eventID = Integer.parseInt(searchCriteria); // Parse input string to integer
                        statement.setInt(1, eventID);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid event ID format.");
                        return searchResults; // Return empty list if input is not a valid ID
                    }
                } else {
                    statement.setString(1, "%" + searchCriteria + "%");
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int eventID = resultSet.getInt("eventID");
                        String eventTitle = resultSet.getString("eventTitle");
                        String eventDate = resultSet.getString("eventDate");
                        String eventLocation = resultSet.getString("eventLocation");
                        String eventDescription = resultSet.getString("eventDescription");

                        // Fetch participants for the event
                        List<Participant> participants = getParticipantsForEvent(eventID);

                        Event event = new Event(eventID, eventTitle, eventDate, eventLocation, eventDescription);
                        event.setParticipants(participants);
                        searchResults.add(event);
                    }
                }
            }
        }

        return searchResults;
    }





    public static void updateEventParticipants(int eventID, List<Participant> participants) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            /*/ First, delete existing participants for the event
            String deleteSql = "DELETE FROM participant WHERE eventID=?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
            deleteStatement.setInt(1, eventID);
            deleteStatement.executeUpdate();
            */
            // Then, insert the updated participants
            String participantSql = "UPDATE participant SET eventID = ? WHERE participantID = ?";
            statement = connection.prepareStatement(participantSql);
            for (Participant participant : participants) {
                statement.setInt(1, eventID);
                statement.setInt(2, participant.getParticipantID());
                statement.addBatch();
            }
            statement.executeBatch();

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            // Close resources
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    // Method to retrieve participants by event ID from the database
    public static List<Participant> getParticipantsByEventID(int eventID) throws SQLException {
        List<Participant> participants = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM participant WHERE eventID = ?");
        ) {
            statement.setInt(1, eventID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int participantID = resultSet.getInt("participantID");
                    String participantName = resultSet.getString("participantName");
                    String participantEmail = resultSet.getString("participantEmail");
                    Participant participant = new Participant(participantID, participantName, participantEmail);
                    participants.add(participant);
                }
            }
        }
        return participants;
    }

    public static List<Event> searchEventByTitle(String title) throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM event WHERE eventTitle LIKE ?")) {
            statement.setString(1, "%" + title + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int eventID = resultSet.getInt("eventID");
                    String eventTitle = resultSet.getString("eventTitle");
                    String eventDate = resultSet.getString("eventDate");
                    String eventLocation = resultSet.getString("eventLocation");
                    String eventDescription = resultSet.getString("eventDescription");
                    Event event = new Event(eventID, eventTitle, eventDate, eventLocation, eventDescription);
                    // Fetch participants for the event
                    List<Participant> participants = getParticipantsByEventID(eventID);
                    for (Participant participant : participants) {
                        event.addParticipant(participant);
                    }
                    events.add(event);

                }
            }
        }
        return events;
    }

    public static List<Event> searchEventByDate(String date) throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM event WHERE eventDate = ?")) {
            statement.setString(1, date);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int eventID = resultSet.getInt("eventID");
                    String eventTitle = resultSet.getString("eventTitle");
                    String eventDate = resultSet.getString("eventDate");
                    String eventLocation = resultSet.getString("eventLocation");
                    String eventDescription = resultSet.getString("eventDescription");
                    Event event = new Event(eventID, eventTitle, eventDate, eventLocation, eventDescription);
                    List<Participant> participants = getParticipantsByEventID(eventID);
                    for (Participant participant : participants) {
                        event.addParticipant(participant);
                    }
                    events.add(event);

                }
            }
        }
        return events;
    }

    public static List<Event> searchEventByLocation(String location) throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM event WHERE eventLocation LIKE ?")) {
            statement.setString(1, "%" + location + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int eventID = resultSet.getInt("eventID");
                    String eventTitle = resultSet.getString("eventTitle");
                    String eventDate = resultSet.getString("eventDate");
                    String eventLocation = resultSet.getString("eventLocation");
                    String eventDescription = resultSet.getString("eventDescription");
                    Event event = new Event(eventID, eventTitle, eventDate, eventLocation, eventDescription);
                    List<Participant> participants = getParticipantsByEventID(eventID);
                    for (Participant participant : participants) {
                        event.addParticipant(participant);
                    }
                    events.add(event);
                }
            }
        }
        return events;
    }






}
