package com.example.databasefinal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DepartmentAdder {
    public static void main(String[] args) {
        // Данные для подключения к базе данных.
        String url = "jdbc:mysql://localhost:3306/employee_db"; // Замените на ваш URL базы данных.
        String user = "root"; // Замените на ваше имя пользователя.
        String password = "1978"; // Замените на ваш пароль.

        // SQL-запрос для добавления нового отдела.
        String sql = "INSERT INTO departments (name) VALUES (?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Установка значения параметра. Параметр ? будет заменен на "New Department".
            statement.setString(1, "New Department");

            // Выполнение запроса.
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new department was added successfully!");
            }

        } catch (SQLException e) {
            // Обработка ошибок при подключении к базе данных или выполнении запроса.
            System.out.println("An error occurred while adding the department: " + e.getMessage());
        }
    }
}
