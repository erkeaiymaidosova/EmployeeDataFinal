package com.example.databasefinal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DepartmentData {
    private Connection conn;

    public DepartmentData(Connection conn) {
        this.conn = conn;
    }

    public ObservableList<Department> getAllDepartments() {
        ObservableList<Department> departmentList = FXCollections.observableArrayList();
        String query = "SELECT id, dept_name FROM department";

        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String deptName = resultSet.getString("dept_name");
                departmentList.add(new Department(id, deptName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to fetch departments: " + e.getMessage());
        }

        return departmentList;
    }

    public boolean addDepartment(Department department) {
        String query = "INSERT INTO department (dept_name) VALUES (?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, department.getDeptName());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to add department: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDepartment(int departmentId) {
        String query = "DELETE FROM department WHERE id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, departmentId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to delete department: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDepartment(Department department) {
        String query = "UPDATE department SET dept_name = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, department.getDeptName());
            preparedStatement.setInt(2, department.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to update department: " + e.getMessage());
            return false;
        }
    }
}
