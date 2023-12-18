package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DBManager {
    public static Statement stmt;
    public static ResultSet rs;
    public DefaultTableModel dtm;
    
    private static DBManager instance;
    private static Connection connection;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/pemo2";
    private static final String DB_USERNAME = "alvin";
    private static final String PASSWORD = "";

    
    public DBManager() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, PASSWORD);
    }
    
    
    public static DBManager getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }
    
    
    public void connect(String url, String username, String password) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        } try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            throw new SQLException("JDBC Driver not found");
        }
    }
    
    
    public Connection getConnection() throws SQLException, ClassNotFoundException { 
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, PASSWORD);
        return connection;
    }
    
    
    public boolean isConnectionClosed() throws SQLException {
        return connection != null && !connection.isClosed();
    }
    
    
    public ResultSet executeQuery(String query, Object... parameters) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Connection is not established.");
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) { 
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println(e);
            throw new SQLException("Error executing the query");
        }
    }

    
    public int executeUpdate(String query, Object... parameters) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Connection is not established.");
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) { 
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error executing the update");
        }
    }
    
    
    public static void insertData(String tableName, List<Object> columnNames, List<Object> values) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        for (int i = 0; i < columnNames.size(); i++) {
            sqlBuilder.append(columnNames.get(i));
            if (i < columnNames.size() - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(") VALUES (");
        for (int i = 0; i < values.size(); i++) {
            sqlBuilder.append("?");
            if (i < values.size() - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(");");
        int rowsAffected;
            try (PreparedStatement preparedStatement = connection.
                        prepareStatement(sqlBuilder.toString())) {
                for (int i = 0; i < values.size(); i++) { 
                    preparedStatement.setObject(i + 1, values.get(i));
                }   rowsAffected = preparedStatement.executeUpdate();

            }
        if (rowsAffected > 0)
            JOptionPane.showMessageDialog(null, "Berhasil memasukan data!");
        else
            JOptionPane.showMessageDialog(null, "Gagal memasukan data!");
    }   

    
    public static boolean editData(String tableName, List<Object> columnNames, List<Object> values) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName + " SET ");

        for (int i = 0; i < columnNames.size(); i++) {
//            if (values.get(i).getClass().getSimpleName()) 
//                sqlBuilder.append(columnNames.get(i) + " = " + values.get(i));
//            else
                sqlBuilder.append(columnNames.get(i) + " = " + "'" + values.get(i) + "'");
            if (i < columnNames.size() - 1) {
                sqlBuilder.append(", ");
            }
        }

        sqlBuilder.append(" WHERE " + columnNames.get(0) + " = " + values.get(0) + ");");
        System.out.println(sqlBuilder);
        int rowsAffected;
        try (PreparedStatement preparedStatement = connection.
                    prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < values.size(); i++) { 
                preparedStatement.setObject(i + 1, values.get(i));
            }   rowsAffected = preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Berhasil mengedit data!");
        }
        JOptionPane.showMessageDialog(null, "Gagal mengedit data!"); 
        
        return rowsAffected > 0;  
    }
    
    
    public List<String[]> retrieveData(String tableName, String query) {
        List<String[]> list = new ArrayList<>();
        try {
            try (
                ResultSet resultSet = connection.createStatement().executeQuery(query)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    String[] rowData = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = resultSet.getString(i);
                    }
                    list.add(rowData);
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        System.out.println(list);
        return list;
    }
    
    
    public static int updateData(String tableName, String columnName, String searchWord, String newValue) {
        String sqlQuery = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + columnName + " LIKE ?";
        int rowsAffected = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);) {
            preparedStatement.setString(1, newValue);
            preparedStatement.setString(2, "%" + searchWord + "%");
            rowsAffected = preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Berhasil mengupdate data!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate data!");
        }
        return rowsAffected;
    }
    
    
    public static DefaultTableModel searchData(String tableName, String columnName, String searchWord) {
        DefaultTableModel tableModel = new DefaultTableModel();
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE " + columnName + " LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);) {
            preparedStatement.setString(1, "%" + searchWord + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                Object[] columnNames = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    columnNames[i - 1] = resultSet.getMetaData().getColumnName(i);
                }
                tableModel.setColumnIdentifiers(columnNames);
                while (resultSet.next()) {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = resultSet.getObject(i);
                    }
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return tableModel;
    }
    
    
    public static boolean deleteData(String query) throws SQLException, ClassNotFoundException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Berhasil menghapus data!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data!");
            return false;
        }
        return false;
    }
    
    
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection closed.");
        }
    }
}
