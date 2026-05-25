package kp.domain.northwind;

import java.util.List;

public record Customer(
        String customerID,
        String companyName,
        String contactName,
        String contactTitle,
        String phone,
        String fax,
        String country,
        String region,
        String city,
        String postalCode,
        String address,
        // relation PURCHASED
        List<Order> purchasedOrders) {
}