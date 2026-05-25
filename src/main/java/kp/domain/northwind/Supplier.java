package kp.domain.northwind;

import java.util.List;

public record Supplier(
        Integer supplierID,
        String companyName,
        String contactName,
        String contactTitle,
        String phone,
        String fax,
        String homePage,
        String country,
        String city,
        String region,
        String postalCode,
        String address,
        // relation SUPPLIES
        List<Product> suppliedProducts) {
}
