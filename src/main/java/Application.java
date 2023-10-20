import java.sql.*;
import java.util.Objects;

public class Application {

    private static final String CREATE_TABLE =
            "CREATE TABLE employee (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT," +
                    "name VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) NOT NULL," +
                    "address VARCHAR(255) DEFAULT NULL," +
                    "PRIMARY KEY (id))";

    private static final String INSERT_EMPLOYEE =
            "INSERT INTO employee (name, email, address) " +
                    "VALUES (?, ?, ?)";

    private static final String UPDATE_EMPLOYEE =
            "UPDATE employee SET address = ?" +
                    "WHERE email = ?";

    private static final String SELECT_EMPLOYEE = "SELECT * FROM employee";

    private static final String DROP_TABLE = "DROP TABLE employee";


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = null;

        try {
            connection = startConnection();
            createTable(connection);

            Employee maxwell = new Employee("Maxwell", "manager@allcars.com", "Center Park Avenue");
            Employee mary = new Employee("Mary", "rh@allcars.com", "Hardware Store Avenue");

            insertEmployee(connection, maxwell);
            insertEmployee(connection, mary);

            selectEmployee(connection);

            updateEmployeeAddress(connection, maxwell.getEmail(), "Main Oak Street");
            selectEmployee(connection);

            dropTable(connection);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            throw new RuntimeException(e);

        } finally {
            stopConnection(connection);
        }
    }

    private static Connection startConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/infnet?createDatabaseIfNotExist=true", "root", "mysql123");
    }

    private static void stopConnection(Connection connection) throws SQLException {
        if (Objects.nonNull(connection)) {
            connection.close();
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(CREATE_TABLE);
        statement.close();
    }

    private static void dropTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(DROP_TABLE);
        statement.close();
    }

    private static void insertEmployee(Connection connection, Employee employee) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EMPLOYEE);
        preparedStatement.setString(1, employee.getName());
        preparedStatement.setString(2, employee.getEmail());
        preparedStatement.setString(3, employee.getAddress());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public static void selectEmployee(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_EMPLOYEE);

        while (resultSet.next()) {
            System.out.println(resultSet.getLong("id"));
            System.out.println(resultSet.getString("name"));
            System.out.println(resultSet.getString("email"));
            System.out.println(resultSet.getString("address"));
            System.out.println();
        }
        System.out.println("----");
    }

    public static void updateEmployeeAddress(Connection connection, String email, String address) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EMPLOYEE);
        preparedStatement.setString(1, address);
        preparedStatement.setString(2, email);
        preparedStatement.executeUpdate();
    }
}
