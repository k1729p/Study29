package kp.domain.northwind;

import java.util.List;

public record Order(
        Integer orderID,
        String freight,
        String orderDate,
        String requiredDate,
        String shippedDate,
        String shipName,
        String shipCountry,
        String shipRegion,
        String shipCity,
        String shipPostalCode,
        String shipAddress,
        // from relation
        Customer customer,
        // from relation
        Shipper shipper,
        // from relation
        Employee employee,
        // relation ORDERS
        List<OrderItem> orderedProducts,
        // relation CONTAINS
        List<OrderItem> containedProducts) {
}