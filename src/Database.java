import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String url = "jdbc:mysql://localhost:3306/db_event_registration"; // Update URL with your database name
    private static final String username = "root"; // Update username with your MySQL username
    private static final String password = "12345678"; // Update password with your MySQL password

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
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String sql = "SELECT * FROM Event";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int eventID = resultSet.getInt("eventID");
                String eventTitle = resultSet.getString("eventTitle");
                String eventDate = resultSet.getString("eventDate");
                String eventLocation = resultSet.getString("eventLocation");
                String eventDescription = resultSet.getString("eventDescription");
                Event event = new Event(eventID, eventTitle, eventDate, eventLocation, eventDescription);
                events.add(event);
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

        return events;
    }

    public static void addEventData(Event event) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            String sql = "INSERT INTO Event (eventTitle, eventDate, eventLocation, eventDescription) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, event.getEventTitle()); // Use getter method to access member variable
            statement.setString(2, event.getEventDate());
            statement.setString(3, event.getEventLocation());
            statement.setString(4, event.getEventDescription());
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
