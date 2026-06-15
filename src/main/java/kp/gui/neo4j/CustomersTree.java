package kp.gui.neo4j;

import kp.domain.northwind.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static kp.gui.GuiClientConstants.*;

/**
 * Tree with customers.
 */
public class CustomersTree {
    private static final AtomicInteger atomic1 = new AtomicInteger();
    private static final AtomicInteger atomic2 = new AtomicInteger();
    private static final AtomicInteger atomic3 = new AtomicInteger();
    private static final AtomicInteger atomic4 = new AtomicInteger();

    /**
     * Creates tree cell renderer for customers tree.
     */
    public static DefaultTreeCellRenderer createRenderer() {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                          boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                setFont(new Font(getFont().getName(), Font.BOLD, 12));
                final String nodeText = value.toString();
                boolean opaqueFlag = false;
                if (!sel && ("Customers".equals(nodeText) || nodeText.matches("^Customer \\d+$"))) {
                    setBackground(Color.YELLOW);
                    opaqueFlag = true;
                }
                if (!sel && ("Orders".equals(nodeText) || nodeText.matches("^Order \\d+$"))) {
                    setBackground(BISQUE);
                    opaqueFlag = true;
                }
                if (!sel && ("Order Items".equals(nodeText) || nodeText.matches("^Order Item \\d+$"))) {
                    setBackground(LIGHT_CYAN);
                    opaqueFlag = true;
                }
                if (!sel && nodeText.matches("^Quantity: \\d+$")) {
                    setBackground(Color.ORANGE);
                    opaqueFlag = true;
                }
                if (!sel && "Product".equals(nodeText)) {
                    setBackground(Color.GREEN);
                    opaqueFlag = true;
                }
                if (!sel && ("Categories".equals(nodeText) || nodeText.matches("^Category \\d+$"))) {
                    setBackground(MISTY_ROSE);
                    opaqueFlag = true;
                }
                setOpaque(opaqueFlag);
                return this;
            }
        };
    }

    /**
     * Creates tree nodes for customers.
     *
     * @param customers the customer list
     * @return the customers node
     */
    public static DefaultMutableTreeNode createTreeNodesForCustomers(List<Customer> customers) {

        final DefaultMutableTreeNode nodeCustomers = new DefaultMutableTreeNode("Customers");
        atomic1.set(0);
        customers.forEach(customer -> addTreeNodesForCustomer(nodeCustomers, customer));
        return nodeCustomers;
    }

    /**
     * Adds tree nodes for customers.
     *
     * @param nodeCustomers the tree node for customers
     * @param customer      the customer
     */
    private static void addTreeNodesForCustomer(DefaultMutableTreeNode nodeCustomers, Customer customer) {

        final DefaultMutableTreeNode nodeCustomer = new DefaultMutableTreeNode(
                String.format("Customer %d", atomic1.incrementAndGet()));
        nodeCustomers.add(nodeCustomer);
        final DefaultMutableTreeNode nodeCustomerID = new DefaultMutableTreeNode(
                String.format("Customer ID: %s", customer.customerID()));
        nodeCustomer.add(nodeCustomerID);
        final DefaultMutableTreeNode nodeCustomerCompanyName = new DefaultMutableTreeNode(
                String.format("Company Name: %s", customer.companyName()));
        nodeCustomer.add(nodeCustomerCompanyName);
        final DefaultMutableTreeNode nodeOrders = new DefaultMutableTreeNode("Orders");
        nodeCustomer.add(nodeOrders);
        atomic2.set(0);
        customer.purchasedOrders().forEach(order -> addTreeNodesForOrders(nodeOrders, order));
    }

    /**
     * Adds tree nodes for orders.
     *
     * @param nodeOrders the tree node for orders
     * @param order      the order
     */
    private static void addTreeNodesForOrders(DefaultMutableTreeNode nodeOrders, Order order) {

        final DefaultMutableTreeNode nodeOrder = new DefaultMutableTreeNode(
                String.format("Order %d", atomic2.incrementAndGet()));
        nodeOrders.add(nodeOrder);
        final DefaultMutableTreeNode nodeOrderID = new DefaultMutableTreeNode(
                String.format("Order ID %d", order.orderID()));
        nodeOrder.add(nodeOrderID);
        final DefaultMutableTreeNode nodeShipName = new DefaultMutableTreeNode(
                String.format("Ship Name: %s", order.shipName()));
        nodeOrder.add(nodeShipName);
        final DefaultMutableTreeNode nodeOrderItems = new DefaultMutableTreeNode("Order Items");
        nodeOrder.add(nodeOrderItems);
        atomic3.set(0);
        order.orderedProducts().forEach(orderItem -> addTreeNodesForOrderItems(nodeOrderItems, orderItem));
    }

    /**
     * Adds tree nodes for order items.
     *
     * @param nodeOrderedProducts the tree node for order items
     * @param orderItem           the order item
     */
    private static void addTreeNodesForOrderItems(DefaultMutableTreeNode nodeOrderedProducts, OrderItem orderItem) {

        final DefaultMutableTreeNode nodeOrderedProduct = new DefaultMutableTreeNode(
                String.format("Order Item %d", atomic3.incrementAndGet()));
        nodeOrderedProducts.add(nodeOrderedProduct);
        final DefaultMutableTreeNode nodeQuantity = new DefaultMutableTreeNode(
                String.format("Quantity: %s", orderItem.quantity()));
        nodeOrderedProduct.add(nodeQuantity);
        final DefaultMutableTreeNode nodeProduct = new DefaultMutableTreeNode("Product");
        nodeOrderedProduct.add(nodeProduct);
        final Product product = orderItem.product();
        final DefaultMutableTreeNode nodeProductID = new DefaultMutableTreeNode(
                String.format("Product ID: %d", orderItem.product().productID()));
        nodeProduct.add(nodeProductID);
        final DefaultMutableTreeNode nodeProductName = new DefaultMutableTreeNode(
                String.format("Product Name: %s", product.productName()));
        nodeProduct.add(nodeProductName);
        final DefaultMutableTreeNode nodePartOfCategories = new DefaultMutableTreeNode("Categories");
        nodeProduct.add(nodePartOfCategories);
        atomic4.set(0);
        product.partOfCategories().forEach(category -> addTreeNodesForOrderItems(nodePartOfCategories, category));
    }

    /**
     * Adds tree nodes for categories.
     *
     * @param nodePartOfCategories the tree node for categories
     * @param category             the category
     */
    private static void addTreeNodesForOrderItems(DefaultMutableTreeNode nodePartOfCategories, Category category) {

        final DefaultMutableTreeNode nodeCategory = new DefaultMutableTreeNode(
                String.format("Category %d", atomic4.incrementAndGet()));
        nodePartOfCategories.add(nodeCategory);
        final DefaultMutableTreeNode nodeCategoryID = new DefaultMutableTreeNode(
                String.format("Category ID: %s", category.categoryID()));
        nodeCategory.add(nodeCategoryID);
        final DefaultMutableTreeNode nodeCategoryName = new DefaultMutableTreeNode(
                String.format("Category Name: %s", category.categoryName()));
        nodeCategory.add(nodeCategoryName);
    }

}
