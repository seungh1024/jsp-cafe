package codesquad.javacafe.common.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Objects;

public class DBConnection {
    private static final Logger log = LoggerFactory.getLogger(DBConnection.class);
    private static String URL = "jdbc:mysql://localhost:3306/cafe?readTimeout=3000";
    private static String USERNAME = "seungh1024";
    private static String PASSWORD = "1234";

    private DBConnection(){}
    static{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

    public static Connection getConnection() {
        try {
            var connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            log.info("[Connection]  connection info = {}, class = {}", connection, connection.getClass());
            return connection;
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void setConnectionInfo(String url, String username, String password) {
        URL = url;
        USERNAME = username;
        PASSWORD = password;
    }

    public static void close(Connection connection, Statement stmt, ResultSet rs) {
        if (Objects.nonNull(rs)) {
            try {
                rs.close();
            } catch (SQLException exception) {
                log.error("[SQLException] Class Info = {}, Exception with ResultSet close", exception);
                exception.printStackTrace();
            }
        }

        if (Objects.nonNull(stmt)) {
            try {
                stmt.close();
            } catch (SQLException exception) {
                log.error("[SQLException] Class Info = {}, Exception with Statement close", exception);
                exception.printStackTrace();
            }
        }

        if (Objects.nonNull(connection)) {
            try {
                connection.close();
            } catch (SQLException exception) {
                log.error("[SQLException] Class Info = {}, Exception with Connection close", exception);
                exception.printStackTrace();
            }
        }

    }

}
