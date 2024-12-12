package com.example.databasefinal;

public class Department {
    private int id;
    private String deptName;

    public Department() {
    }

    public Department(int id, String deptName) {
        this.id = id;
        this.deptName = deptName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Department Name: " + deptName;
    }

    public String getName() {
        return null;
    }
}
