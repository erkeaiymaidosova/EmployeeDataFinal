package com.example.databasefinal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;



import java.sql.*;
import java.util.ArrayList;

public class HelloController {
    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ListView<String> employeeListView;

    @FXML
    private DatePicker hireDateField;

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField positionField;

    @FXML
    private TextField salaryField;

    @FXML
    private Button updateButton;
    private Connection conn;

    private EmployeeData employeeData;
    private ObservableList<Employee> employeeList;

    public HelloController() {
        this.employeeData = new EmployeeData();
        this.employeeList = FXCollections.observableArrayList();
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5434/employee_db", "postgres", "silvi");
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAdd(ActionEvent event) {
        String selectedEmployee = (String) employeeListView.getSelectionModel().getSelectedItem();
        EmployeeData employeeData = new EmployeeData();
        boolean isUpdating = selectedEmployee != null;

        try {
            String query;
            if (isUpdating) {
                query = "UPDATE employee SET name = ?, position = ?, salary = ?, hireDate = ? WHERE id = ?";
            } else {
                query = "INSERT INTO employee (id, name, position, salary, hireDate) VALUES (?, ?, ?, ?, ?)";
            }

            PreparedStatement preparedStatement = employeeData.getConn().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            if (isUpdating) {
                preparedStatement.setString(1, nameField.getText());
                preparedStatement.setString(2, positionField.getText());
                preparedStatement.setDouble(3, Double.parseDouble(salaryField.getText()));
                preparedStatement.setDate(4, Date.valueOf(hireDateField.getValue()));
                preparedStatement.setInt(5, Integer.parseInt(idField.getText()));
            } else {
                preparedStatement.setInt(1, Integer.parseInt(idField.getText()));
                preparedStatement.setString(2, nameField.getText());
                preparedStatement.setString(3, positionField.getText());
                preparedStatement.setDouble(4, Double.parseDouble(salaryField.getText()));
                preparedStatement.setDate(5, Date.valueOf(hireDateField.getValue()));
            }

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Operation failed");
            }

            if (!isUpdating) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idField.setText(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
            }

            showAlert("Success", isUpdating ? "Updated successfully." : "Inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database operation failed: " + e.getMessage());
        }

        refreshEmployeeList();
        clearFields();
    }


    @FXML
    protected void onUpdate(ActionEvent event) {
        String selectedEmployee = (String) employeeListView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Error", "Please select an employee to update.");
            return;
        }

        if (areFieldsEmpty(idField, nameField, positionField, salaryField)) {
            showAlert("Error", "All fields are required to update the employee.");
            return;
        }

        onAdd(event);
    }

    @FXML
    protected void onDelete(ActionEvent event) {
        String selectedEmployee = (String) employeeListView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Error", "Please select an employee to delete.");
            return;
        }

        String employeeID = selectedEmployee.split(",")[0].split(":")[1].trim(); // Extract the ID from the formatted string
        EmployeeData employeeData = new EmployeeData();

        try {
            String query = "DELETE FROM employee WHERE id = ?";
            PreparedStatement preparedStatement = employeeData.getConn().prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(employeeID));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                employeeListView.getItems().remove(selectedEmployee);
                clearFields();
                showAlert("Success", "Deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete employee.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while deleting the employee: " + e.getMessage());
        }
    }

    private void refreshEmployeeList() {
        EmployeeData employeeData = new EmployeeData();
        employeeListView.getItems().clear();
        for (Employee employee : employeeData.getAllEmployee()) {
            employeeListView.getItems().add(formatEmployeeInfo(employee));
        }
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        positionField.clear();
        salaryField.clear();
        hireDateField.setValue(null);
    }

    private String formatEmployeeInfo(Employee employee) {
        return "ID: " + employee.getId() + ", Name: " + employee.getName() + ", Position: " + employee.getPosition() +
                ", Salary: " + employee.getSalary() + ", Hire Date: " + employee.getHireDate();
    }

    private boolean areFieldsEmpty(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    public void onInst(ActionEvent actionEvent) {
        showAlert("Instruction", "Instructions on how to use the system: Add, Update, and Delete employees.Fill fields and click on 'add' to add new employee,select employee and click 'delete' if you want to delete,select employee and fill fields with new value and click 'update' to update the employee");
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}