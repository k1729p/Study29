package kp.gui.neo4j;

import kp.domain.northwind.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static kp.gui.GuiClientConstants.*;

/**
 * Tree with orders.
 */
public class OrdersTree {
    private static final AtomicInteger atomic = new AtomicInteger();

    /**
     * Creates tree cell renderer for orders tree.
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
                if (!sel && "Customer".equals(nodeText)) {
                    setBackground(Color.YELLOW);
                    opaqueFlag = true;
                } else if (!sel && "Shipper".equals(nodeText)) {
                    setBackground(HONEYDEW);
                    opaqueFlag = true;
                } else if (!sel && "Employee".equals(nodeText)) {
                    setBackground(Color.ORANGE);
                    opaqueFlag = true;
                } else if (!sel && ("Managers".equals(nodeText) || nodeText.matches("^Manager \\d+$"))) {
                    setBackground(CORNSILK);
                    opaqueFlag = true;
                } else if (!sel && ("Territories".equals(nodeText) || nodeText.matches("^Territory \\d+$"))) {
                    setBackground(Color.GREEN);
                    opaqueFlag = true;
                } else if (!sel && "Region".equals(nodeText)) {
                    setBackground(BISQUE);
                    opaqueFlag = true;
                } else if (!sel && ("Ordered Products".equals(nodeText) || nodeText.matches("^Ordered Product \\d+$"))) {
                    setBackground(LIGHT_CYAN);
                    opaqueFlag = true;
                } else if (!sel && ("Contained Products".equals(nodeText) || nodeText.matches("^Contained Product \\d+$"))) {
                    setBackground(MISTY_ROSE);
                    opaqueFlag = true;
                }
                setOpaque(opaqueFlag);
                return this;
            }
        };
    }

    /**
     * Creates tree nodes for orders.
     *
     * @param orders the order list
     * @return the orders node
     */
    public static DefaultMutableTreeNode createTreeNodesForOrders(List<Order> orders) {
        final DefaultMutableTreeNode nodeOrders = new DefaultMutableTreeNode("Orders");
        orders.forEach(order -> addTreeNodesForOrders(nodeOrders, order));
        return nodeOrders;
    }

    /**
     * Adds tree nodes for orders.
     *
     * @param nodeOrders the tree node for orders
     * @param order      the order
     */
    private static void addTreeNodesForOrders(DefaultMutableTreeNode nodeOrders, Order order) {

        final DefaultMutableTreeNode nodeOrder = new DefaultMutableTreeNode("Order");
        nodeOrders.add(nodeOrder);
        final DefaultMutableTreeNode nodeShipName = new DefaultMutableTreeNode(
                String.format("Ship Name: %s", order.shipName()));
        nodeOrder.add(nodeShipName);
        final DefaultMutableTreeNode nodeCustomer = new DefaultMutableTreeNode("Customer");
        nodeOrder.add(nodeCustomer);
        final DefaultMutableTreeNode nodeCustomerCompanyName = new DefaultMutableTreeNode(
                String.format("Company Name: %s", order.customer().companyName()));
        nodeCustomer.add(nodeCustomerCompanyName);
        final DefaultMutableTreeNode nodeShipper = new DefaultMutableTreeNode("Shipper");
        nodeOrder.add(nodeShipper);
        final DefaultMutableTreeNode nodeShipperCompanyName = new DefaultMutableTreeNode(
                String.format("Company Name: %s", order.shipper().companyName()));
        nodeShipper.add(nodeShipperCompanyName);
        final DefaultMutableTreeNode nodeEmployee = new DefaultMutableTreeNode("Employee");
        nodeOrder.add(nodeEmployee);
        final DefaultMutableTreeNode nodeEmployeeFullName = new DefaultMutableTreeNode(
                String.format("Full Name: %s %s", order.employee().firstName(), order.employee().lastName()));
        nodeEmployee.add(nodeEmployeeFullName);
        final DefaultMutableTreeNode nodeEmployeeTitle = new DefaultMutableTreeNode(
                String.format("Title: %s", order.employee().title()));
        nodeEmployee.add(nodeEmployeeTitle);

        final DefaultMutableTreeNode nodeManagers = new DefaultMutableTreeNode("Managers");
        nodeEmployee.add(nodeManagers);
        atomic.set(0);
        order.employee().managers().forEach(manager ->
                addTreeNodesForManagers(nodeManagers, manager));
        final DefaultMutableTreeNode nodeTerritories = new DefaultMutableTreeNode("Territories");
        nodeEmployee.add(nodeTerritories);
        atomic.set(0);
        order.employee().territories().forEach(territory ->
                addTreeNodesForTerritories(nodeTerritories, territory));
        final DefaultMutableTreeNode nodeOrderedProducts =
                new DefaultMutableTreeNode("Ordered Products");
        nodeOrder.add(nodeOrderedProducts);
        atomic.set(0);
        order.orderedProducts().forEach(orderItem -> {
            final DefaultMutableTreeNode nodeOrderedProduct = new DefaultMutableTreeNode(
                    String.format("Ordered Product %d", atomic.incrementAndGet()));
            nodeOrderedProducts.add(nodeOrderedProduct);
            addTreeNodesForOrderItems(nodeOrderedProduct, orderItem);
        });
        atomic.set(0);
        final DefaultMutableTreeNode nodeContainedProducts =
                new DefaultMutableTreeNode("Contained Products");
        nodeOrder.add(nodeContainedProducts);
        order.containedProducts().forEach(orderItem -> {
            final DefaultMutableTreeNode nodeContainedProduct = new DefaultMutableTreeNode(
                    String.format("Contained Product %d", atomic.incrementAndGet()));
            nodeContainedProducts.add(nodeContainedProduct);
            addTreeNodesForOrderItems(nodeContainedProduct, orderItem);
        });
    }

    /**
     * Adds tree nodes for managers.
     *
     * @param nodeManagers the tree node for managers
     * @param manager      the manager
     */
    private static void addTreeNodesForManagers(DefaultMutableTreeNode nodeManagers, Employee manager) {

        final DefaultMutableTreeNode nodeManager = new DefaultMutableTreeNode(
                String.format("Manager %d", atomic.incrementAndGet()));
        nodeManagers.add(nodeManager);
        final DefaultMutableTreeNode nodeManagerFullName = new DefaultMutableTreeNode(
                String.format("Full Name: %s %s", manager.firstName(), manager.lastName()));
        nodeManager.add(nodeManagerFullName);
        final DefaultMutableTreeNode nodeManagerTitle = new DefaultMutableTreeNode(
                String.format("Title: %s", manager.title()));
        nodeManager.add(nodeManagerTitle);
    }

    /**
     * Adds tree nodes for territories.
     *
     * @param nodeTerritories the tree node for territories
     * @param territory       the territory
     */
    private static void addTreeNodesForTerritories(DefaultMutableTreeNode nodeTerritories, Territory territory) {

        final DefaultMutableTreeNode nodeTerritory = new DefaultMutableTreeNode(
                String.format("Territory %d", atomic.incrementAndGet()));
        nodeTerritories.add(nodeTerritory);
        final DefaultMutableTreeNode nodeTerritoryDescription = new DefaultMutableTreeNode(
                String.format("Territory Description: %s", territory.territoryDescription()));
        nodeTerritory.add(nodeTerritoryDescription);
        territory.regions().forEach(region -> {
            final DefaultMutableTreeNode nodeRegion = new DefaultMutableTreeNode("Region");
            nodeTerritory.add(nodeRegion);
            final DefaultMutableTreeNode nodeRegionDescription = new DefaultMutableTreeNode(
                    String.format("Region Description: %s", region.regionDescription()));
            nodeRegion.add(nodeRegionDescription);
        });
    }

    /**
     * Adds tree nodes for order items.
     *
     * @param nodeProducts the tree node for products
     * @param orderItem    the order item
     */
    private static void addTreeNodesForOrderItems(DefaultMutableTreeNode nodeProducts, OrderItem orderItem) {

        final DefaultMutableTreeNode nodeQuantity = new DefaultMutableTreeNode(
                String.format("Quantity: %s", orderItem.quantity()));
        nodeProducts.add(nodeQuantity);
        final DefaultMutableTreeNode nodeProduct = new DefaultMutableTreeNode("Product");
        nodeProducts.add(nodeProduct);
        final Product product = orderItem.product();
        final DefaultMutableTreeNode nodeProductName = new DefaultMutableTreeNode(
                String.format("Product Name: %s", product.productName()));
        nodeProduct.add(nodeProductName);
        final DefaultMutableTreeNode nodeSupplier = new DefaultMutableTreeNode("Supplier");
        nodeProduct.add(nodeSupplier);
        final String companyName = Optional.ofNullable(product.supplier())
                .map(Supplier::companyName).orElse("N/A");
        final DefaultMutableTreeNode nodeCompanyName = new DefaultMutableTreeNode(
                String.format("Company Name: %s", companyName));
        nodeSupplier.add(nodeCompanyName);
        final DefaultMutableTreeNode nodeCategory = new DefaultMutableTreeNode("Category");
        nodeProduct.add(nodeCategory);
        final String categoryName = Optional.ofNullable(product.partOfCategories())
                .map(List::getFirst).map(Category::categoryName).orElse("N/A");
        final DefaultMutableTreeNode nodeCategoryName = new DefaultMutableTreeNode(
                String.format("Category Name: %s", categoryName));
        nodeCategory.add(nodeCategoryName);
    }

}
