package kp.domain.northwind;

import java.util.List;

public record Employee(
        Integer employeeID,
        String firstName,
        String lastName,
        String title,
        String titleOfCourtesy,
        String birthDate,
        String hireDate,
        String notes,
        String photoPath,
        String homePhone,
        String extension,
        String country,
        String region,
        String city,
        String postalCode,
        String address,
        // relation IN_TERRITORY
        List<Territory> territories,
        // relation REPORTS_TO
        List<Employee> managers,
        // relation SOLD
        List<Order> soldOrders) {
}
