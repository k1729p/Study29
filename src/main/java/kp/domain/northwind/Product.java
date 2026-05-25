package kp.domain.northwind;

import java.util.List;

public record Product(
        Integer productID,
        String productName,
        String unitPrice,
        String unitsInStock,
        String unitsOnOrder,
        String reorderLevel,
        String discontinued,
        // from relation
        Supplier supplier,
        // relation PART_OF
        List<Category> partOfCategories) {
}
