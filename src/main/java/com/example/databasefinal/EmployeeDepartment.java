package com.example.databasefinal;

public class EmployeeDepartment {
    private int employeeId;
    private int departmentId;

    public EmployeeDepartment(int employeeId, String employeeName, int departmentId, String departmentName) {
        this.employeeId = employeeId;
        this.departmentId = departmentId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return "Employee ID: " + employeeId + ", Department ID: " + departmentId;
    }
}
