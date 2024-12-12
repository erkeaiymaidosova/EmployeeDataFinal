package com.example.databasefinal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDepartmentData {
    private Connection conn;

    public EmployeeDepartmentData(Connection conn) {
        this.conn = conn;
    }

    public ObservableList<EmployeeDepartment> getAllEmployeeDepartments() {
        ObservableList<EmployeeDepartment> employeeDepartmentList = FXCollections.observableArrayList();
        String query = """
                SELECT ed.employee_id, e.name AS employee_name, ed.department_id, d.name AS department_name
                FROM employee_department ed
                JOIN employee e ON ed.employee_id = e.id
                JOIN department d ON ed.department_id = d.id
                """;

        try (PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int employeeId = resultSet.getInt("employee_id");
                String employeeName = resultSet.getString("employee_name");
                int departmentId = resultSet.getInt("department_id");
                String departmentName = resultSet.getString("department_name");

                employeeDepartmentList.add(new EmployeeDepartment(employeeId, employeeName, departmentId, departmentName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to fetch employee-department data: " + e.getMessage());
        }

        return employeeDepartmentList;
    }

    public boolean addEmployeeDepartment(EmployeeDepartment employeeDepartment) {
        String query = "INSERT INTO employee_department (employee_id, department_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, employeeDepartment.getEmployeeId());
            preparedStatement.setInt(2, employeeDepartment.getDepartmentId());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to add employee-department link: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEmployeeDepartment(int employeeId, int departmentId) {
        String query = "DELETE FROM employee_department WHERE employee_id = ? AND department_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setInt(2, departmentId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to delete employee-department link: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEmployeeDepartment(int oldEmployeeId, int oldDepartmentId, EmployeeDepartment newEmployeeDepartment) {
        String query = """
                UPDATE employee_department
                SET employee_id = ?, department_id = ?
                WHERE employee_id = ? AND department_id = ?
                """;
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, newEmployeeDepartment.getEmployeeId());
            preparedStatement.setInt(2, newEmployeeDepartment.getDepartmentId());
            preparedStatement.setInt(3, oldEmployeeId);
            preparedStatement.setInt(4, oldDepartmentId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to update employee-department link: " + e.getMessage());
            return false;
        }
    }
}
