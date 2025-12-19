package com.hotel.dao;

import com.hotel.entity.Employee;
import com.hotel.entity.Position;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // Обновленный метод getAllEmployees с JOIN для должности
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, p.name as position_name FROM employees e " +
                "LEFT JOIN positions p ON e.position_id = p.id " +
                "ORDER BY e.last_name, e.first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Employee employee = extractEmployeeFromResultSet(rs);
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка сотрудников: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    public void addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (last_name, first_name, middle_name, position_id, " +
                "phone, email, passport_series, passport_number, hire_date, salary, " +
                "status, address, birth_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("Добавление сотрудника: " + employee.getLastName() + " " + employee.getFirstName());
        System.out.println("HireDate: " + employee.getHireDate());
        System.out.println("PositionId: " + employee.getPositionId());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setEmployeeParameters(pstmt, employee);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employee.setId(generatedKeys.getInt(1));
                    System.out.println("ID сотрудника: " + employee.getId());
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении сотрудника: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            throw new RuntimeException("Не удалось добавить сотрудника", e);
        }
    }

    public void updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET last_name = ?, first_name = ?, middle_name = ?, " +
                "position_id = ?, phone = ?, email = ?, passport_series = ?, passport_number = ?, " +
                "hire_date = ?, salary = ?, status = ?, address = ?, birth_date = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setEmployeeParameters(pstmt, employee);
            pstmt.setInt(14, employee.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении сотрудника: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось обновить данные сотрудника", e);
        }
    }

    // Метод удаления сотрудника
    public void deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении сотрудника: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось удалить сотрудника", e);
        }
    }

    // Метод получения сотрудника по ID
    public Employee getEmployeeById(int id) {
        String sql = "SELECT e.*, p.name as position_name FROM employees e " +
                "LEFT JOIN positions p ON e.position_id = p.id " +
                "WHERE e.id = ?";
        Employee employee = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                employee = extractEmployeeFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении сотрудника: " + e.getMessage());
            e.printStackTrace();
        }

        return employee;
    }

    // Метод поиска сотрудников
    public List<Employee> searchEmployees(String searchTerm) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, p.name as position_name FROM employees e " +
                "LEFT JOIN positions p ON e.position_id = p.id " +
                "WHERE e.last_name LIKE ? OR e.first_name LIKE ? " +
                "OR p.name LIKE ? OR e.phone LIKE ? OR e.passport_number LIKE ? " +
                "ORDER BY e.last_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);
            pstmt.setString(4, likeTerm);
            pstmt.setString(5, likeTerm);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Employee employee = extractEmployeeFromResultSet(rs);
                employees.add(employee);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при поиске сотрудников: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    // Метод получения сотрудников по статусу
    public List<Employee> getEmployeesByStatus(String status) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, p.name as position_name FROM employees e " +
                "LEFT JOIN positions p ON e.position_id = p.id " +
                "WHERE e.status = ? ORDER BY e.last_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Employee employee = extractEmployeeFromResultSet(rs);
                employees.add(employee);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении сотрудников по статусу: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    // Метод получения общей суммы зарплат
    public double getTotalSalaryExpenses() {
        String sql = "SELECT SUM(salary) as total FROM employees WHERE status = 'Работает'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении общей суммы зарплат: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    // Метод получения сотрудников по должности
    public List<Employee> getEmployeesByPosition(String positionName) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, p.name as position_name FROM employees e " +
                "JOIN positions p ON e.position_id = p.id " +
                "WHERE p.name = ? AND e.status = 'Работает' " + // Только работающие
                "ORDER BY e.last_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, positionName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Employee employee = extractEmployeeFromResultSet(rs);
                employees.add(employee);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении сотрудников по должности: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    // Вспомогательные методы
    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt("id"));
        employee.setLastName(rs.getString("last_name"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setMiddleName(rs.getString("middle_name"));
        employee.setPositionId(rs.getInt("position_id"));
        employee.setPhone(rs.getString("phone"));
        employee.setEmail(rs.getString("email"));
        employee.setPassportSeries(rs.getString("passport_series"));
        employee.setPassportNumber(rs.getString("passport_number"));
        employee.setHireDate(rs.getDate("hire_date"));
        employee.setSalary(rs.getDouble("salary"));
        employee.setStatus(rs.getString("status"));
        employee.setAddress(rs.getString("address"));
        employee.setBirthDate(rs.getDate("birth_date"));
        employee.setCreatedAt(rs.getTimestamp("created_at"));
        employee.setPositionName(rs.getString("position_name"));
        return employee;
    }

    private void setEmployeeParameters(PreparedStatement pstmt, Employee employee) throws SQLException {
        pstmt.setString(1, employee.getLastName());
        pstmt.setString(2, employee.getFirstName());
        pstmt.setString(3, employee.getMiddleName());
        pstmt.setInt(4, employee.getPositionId());
        pstmt.setString(5, employee.getPhone());
        pstmt.setString(6, employee.getEmail());
        pstmt.setString(7, employee.getPassportSeries());
        pstmt.setString(8, employee.getPassportNumber());

        // Исправлено: проверка на null для hireDate
        if (employee.getHireDate() != null) {
            pstmt.setDate(9, new java.sql.Date(employee.getHireDate().getTime()));
        } else {
            pstmt.setDate(9, null);
        }

        pstmt.setDouble(10, employee.getSalary());
        pstmt.setString(11, employee.getStatus());
        pstmt.setString(12, employee.getAddress());

        // Уже было правильно
        pstmt.setDate(13, employee.getBirthDate() != null ?
                new java.sql.Date(employee.getBirthDate().getTime()) : null);
    }
}