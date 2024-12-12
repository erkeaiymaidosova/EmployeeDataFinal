package com.example.databasefinal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Button addDepartmentButton;
    @FXML
    private Button addEmployeeButton;
    @FXML
    private Button addEmployeeDeptButton;
    @FXML
    private Button deleteDepartmentButton;
    @FXML
    private Button deleteEmployeeButton;
    @FXML
    private Button deleteEmployeeDeptButton;
    @FXML
    private ListView<String> departmentListView;
    @FXML
    private TextField deptIdField;
    @FXML
    private TextField deptNameField;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField empIdField;
    @FXML
    private TextField empNameField;
    @FXML
    private ListView<String> employeeDeptListView;
    @FXML
    private ListView<String> employeeListView;
    @FXML
    private DatePicker hireDateField;
    @FXML
    private TextField positionField;
    @FXML
    private TextField salaryField;
    @FXML
    private Button updateDepartmentButton;
    @FXML
    private Button updateEmployeeButton;
    @FXML
    private Button updateEmployeeDeptButton;

    private Connection conn;
    private ObservableList<Employee> employeeList;

    public HelloController() {
        this.employeeList = FXCollections.observableArrayList();
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_db", "root", "1978");
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAdd(ActionEvent event) {
        String selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
        boolean isUpdating = selectedEmployee != null;

        try {
            String query;
            PreparedStatement preparedStatement;

            if (isUpdating) {
                query = "UPDATE employee SET name = ?, position = ?, salary = ?, hire_date = ? WHERE id = ?";
                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setInt(5, Integer.parseInt(idField.getText()));
            } else {
                query = "INSERT INTO employee (id, name, position, salary, hire_date) VALUES (?, ?, ?, ?, ?)";
                preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            }

            preparedStatement.setInt(1, Integer.parseInt(idField.getText()));
            preparedStatement.setString(2, nameField.getText());
            preparedStatement.setString(3, positionField.getText());
            preparedStatement.setDouble(4, Double.parseDouble(salaryField.getText()));
            preparedStatement.setDate(5, Date.valueOf(hireDateField.getValue()));

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Operation failed, no rows affected.");
            }

            if (!isUpdating) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idField.setText(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
            }

            showAlert("Success", isUpdating ? "Updated successfully." : "Inserted successfully.");
            refreshEmployeeList();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database operation failed: " + e.getMessage());
        }
    }

    @FXML
    protected void onUpdate(ActionEvent event) {
        if (employeeListView.getSelectionModel().getSelectedItem() == null) {
            showAlert("Error", "Please select an employee to update.");
            return;
        }

        if (areFieldsEmpty(idField, nameField, positionField, salaryField, hireDateField)) {
            showAlert("Error", "All fields are required to update the employee.");
            return;
        }

        onAdd(event);
    }

    private boolean areFieldsEmpty(TextField idField, TextField nameField, TextField positionField, TextField salaryField, DatePicker hireDateField) {
        return false;
    }

    private boolean areFieldsEmpty(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @FXML
    protected void onDelete(ActionEvent event) {
        String selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Error", "Please select an employee to delete.");
            return;
        }

        String employeeID = selectedEmployee.split(",")[0].split(":")[1].trim();

        try {
            String query = "DELETE FROM employee WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
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
            showAlert("Error", "An error occurred while deleting the employee: " + e.getMessage());
        }
    }

    @FXML
    void onAddDepartment(ActionEvent event) {
        if (areFieldsEmpty(deptIdField, deptNameField)) {
            showAlert("Error", "Please fill out all fields to add a department.");
            return;
        }

        try {
            String query = "INSERT INTO department (id, dept_name) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(deptIdField.getText()));
            preparedStatement.setString(2, deptNameField.getText());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Department added successfully.");
                refreshDepartmentList();
                clearFields();
            } else {
                showAlert("Error", "Failed to add department.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while adding the department: " + e.getMessage());
        }
    }

    @FXML
    void onDeleteDepartment(ActionEvent event) {
        String selectedDepartment = departmentListView.getSelectionModel().getSelectedItem();
        if (selectedDepartment == null) {
            showAlert("Error", "Please select a department to delete.");
            return;
        }

        String departmentID = selectedDepartment.split(",")[0].split(":")[1].trim();

        try {
            String query = "DELETE FROM department WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(departmentID));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                departmentListView.getItems().remove(selectedDepartment);
                showAlert("Success", "Department deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete department.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while deleting the department: " + e.getMessage());
        }
    }

    @FXML
    void onAddEmployeeDept(ActionEvent event) {
        String selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
        String selectedDepartment = departmentListView.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null || selectedDepartment == null) {
            showAlert("Error", "Please select both an employee and a department.");
            return;
        }

        try {
            String employeeID = selectedEmployee.split(",")[0].split(":")[1].trim();
            String departmentID = selectedDepartment.split(",")[0].split(":")[1].trim();

            String query = "INSERT INTO employee_department (employee_id, department_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(employeeID));
            preparedStatement.setInt(2, Integer.parseInt(departmentID));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Employee assigned to department successfully.");
                refreshEmployeeDeptList();
            } else {
                showAlert("Error", "Failed to assign employee to department.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while adding the employee to the department: " + e.getMessage());
        }
    }

    @FXML
    void onDeleteEmployeeDept(ActionEvent event) {
        String selectedEmployeeDept = employeeDeptListView.getSelectionModel().getSelectedItem();
        if (selectedEmployeeDept == null) {
            showAlert("Error", "Please select an employee-department relation to delete.");
            return;
        }

        String[] parts = selectedEmployeeDept.split(",");
        String employeeID = parts[0].split(":")[1].trim();
        String departmentID = parts[1].split(":")[1].trim();

        try {
            String query = "DELETE FROM employee_department WHERE employee_id = ? AND department_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(employeeID));
            preparedStatement.setInt(2, Integer.parseInt(departmentID));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Employee removed from department successfully.");
                refreshEmployeeDeptList();
            } else {
                showAlert("Error", "Failed to remove employee from department.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while deleting the employee-department relation: " + e.getMessage());
        }
    }

    private void refreshEmployeeList() {
        ObservableList<String> employees = FXCollections.observableArrayList();
        try {
            String query = "SELECT id, name, position, salary, hire_date FROM employee";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String employee = String.format("ID: %d, Name: %s, Position: %s, Salary: %.2f, Hire Date: %s",
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("position"),
                        resultSet.getDouble("salary"),
                        resultSet.getDate("hire_date").toString());
                employees.add(employee);
            }
            employeeListView.setItems(employees);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to refresh the employee list: " + e.getMessage());
        }
    }


    private void refreshDepartmentList() {
        ObservableList<String> departments = FXCollections.observableArrayList();
        try {
            String query = "SELECT id, dept_name FROM department";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String department = String.format("ID: %d, Name: %s",
                        resultSet.getInt("id"),
                        resultSet.getString("dept_name"));
                departments.add(department);
            }
            departmentListView.setItems(departments);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to refresh the department list: " + e.getMessage());
        }
    }


    private void refreshEmployeeDeptList() {
        ObservableList<String> employeeDeptList = FXCollections.observableArrayList();
        try {
            String query = "SELECT e.id, e.name, d.name AS department_name " +
                    "FROM employee e " +
                    "JOIN department d ON e.department_id = d.id";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String employeeInfo = String.format("ID: %d, Name: %s, Department: %s",
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("department_name"));
                employeeDeptList.add(employeeInfo);
            }

            employeeDeptListView.setItems(employeeDeptList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to refresh the employee-department list: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        positionField.clear();
        salaryField.clear();
        hireDateField.setValue(null);
        deptIdField.clear();
        deptNameField.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshEmployeeList();
        refreshDepartmentList();
        refreshEmployeeDeptList();
    }

    public void onInst(ActionEvent actionEvent) {
    }

    public void onUpdateDepartment(ActionEvent actionEvent) {
    }

    public void onUpdateEmployeeDept(ActionEvent actionEvent) {

    }

    @FXML
    private void onUpdateEmployee(ActionEvent actionEvent) {
        try {
            if (validateEmployeeFields()) {
                updateEmployee();
            } else {
                System.out.println("Invalid input fields.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateEmployeeFields() {
        return !empNameField.getText().isEmpty() &&
                !positionField.getText().isEmpty() &&
                !salaryField.getText().isEmpty() &&
                hireDateField.getValue() != null &&
                !empIdField.getText().isEmpty() &&
                isDouble(salaryField.getText());
    }

    private void updateEmployee() {
        String query = "UPDATE employees SET name = ?, position = ?, salary = ?, hire_date = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, empNameField.getText());
            pstmt.setString(2, positionField.getText());
            pstmt.setDouble(3, Double.parseDouble(salaryField.getText()));

            String formattedDate = hireDateField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            pstmt.setDate(4, Date.valueOf(formattedDate));

            pstmt.setInt(5, Integer.parseInt(empIdField.getText()));

            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
