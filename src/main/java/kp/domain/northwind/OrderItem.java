package kp.domain.northwind;

/**
 * OrderItem bridges the relationship to the Product and its quantity.
 *
 * @param discount
 * @param quantity
 * @param unitPrice
 * @param product
 */
public record OrderItem(
        String discount,
        String quantity,
        String unitPrice,
        Product product) {
}
