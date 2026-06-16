package com.stadela.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Sesuaikan dengan konfigurasi server Anda
    private static final String HOST   = "127.0.0.1";
    private static final String PORT   = "3306";
    private static final String DBNAME = "db_stadela";
    private static final String USER   = "root";
    private static final String PASS   = "kaudanaku";

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DBNAME +
        "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=CONVERT_TO_NULL";

    private static Connection instance;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASS);
        }
        return instance;
    }

    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
            }
        } catch (SQLException ignored) {}
    }
}
