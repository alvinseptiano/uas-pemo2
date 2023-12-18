package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Table {
    
    private static DefaultTableModel tableModel;
    DBManager dbManager;
    
    public Table() throws SQLException, ClassNotFoundException {
        dbManager.getConnection();
    }

    public static void setData(JTable table, String tableName, String query) throws SQLException, ClassNotFoundException {
        tableModel = new DefaultTableModel();
        Statement statement = DBManager.getInstance().getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
        }
        while (resultSet.next()) {
            Vector rowData = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                rowData.add(resultSet.getObject(i));
            }
            tableModel.addRow(rowData); 
        }
        table.setModel(tableModel);
    }

 
    public static DefaultTableModel searchData(String tableName, String columnName, String searchWord) throws SQLException, ClassNotFoundException {
        tableModel = new DefaultTableModel();
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE " + columnName + " LIKE ?";
        Connection connection = DBManager.getInstance().getConnection();
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
}
