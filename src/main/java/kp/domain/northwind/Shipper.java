package kp.domain.northwind;

import java.util.List;

public record Shipper(
        Integer shipperID,
        String companyName,
        String phone,
        // relation SHIPS
        List<Order> shippedOrders) {
}
