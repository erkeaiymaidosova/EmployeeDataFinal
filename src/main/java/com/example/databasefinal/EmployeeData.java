package com.example.databasefinal;

import java.sql.*;
import java.util.ArrayList;

public class EmployeeData {
    private Connection conn;

    public EmployeeData() {
        String url = "jdbc:mysql://localhost:3306/employee_db";
        String username = "root";
        String password = "1978";

        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database is successfully connected...");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.toString());
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        return conn;
    }

    public void createEmployee(int id, String uname, String uposition, double usalary, Date udate) {
        String sql = "INSERT INTO employee (id, name, position, salary, hireDate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, uname);
            preparedStatement.setString(3, uposition);
            preparedStatement.setDouble(4, usalary);
            preparedStatement.setDate(5, udate);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            System.out.println("New employee added with ID: " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteEmployee(int id) {
        String sql = "DELETE FROM employee WHERE id=?";
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateEmployee(int id, String uname, String uposition, double usalary, Date udate) {
        String sql = "UPDATE employee SET name=?, position=?, salary=?, hireDate=? WHERE id=?";
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setString(1, uname);
            preparedStatement.setString(2, uposition);
            preparedStatement.setDouble(3, usalary);
            preparedStatement.setDate(4, udate);
            preparedStatement.setInt(5, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }

            System.out.println("Update is successful.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ArrayList<Employee> getAllEmployee() {
        ArrayList<Employee> employers = new ArrayList<>();
        String sql = "SELECT * FROM employee";
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String position = resultSet.getString("position");
                double salary = resultSet.getDouble("salary");
                Date hireDate = resultSet.getDate("hireDate");
                Employee employee = new Employee(id, name, position, salary, hireDate);
                employers.add(employee);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return employers;
    }

    public Employee getEmployee(int id) {
        Employee employee = null;
        String sql = "SELECT * FROM employee WHERE id=?";
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String position = resultSet.getString("position");
                    double salary = resultSet.getDouble("salary");
                    Date hireDate = resultSet.getDate("hireDate");
                    employee = new Employee(id, name, position, salary, hireDate);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return employee;
    }
}
