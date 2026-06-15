package kp.gui;

import kp.clients.neo4j.Neo4jClient;
import kp.domain.northwind.Customer;
import kp.domain.northwind.Order;
import kp.gui.neo4j.CustomersTree;
import kp.gui.neo4j.OrdersTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static kp.gui.GuiClientConstants.*;

/**
 * Swing application.
 */
public class GuiClientLauncher {
    /**
     * Private constructor to prevent instantiation.
     */
    private GuiClientLauncher() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the GUI application.
     */
    static void main() {
        SwingUtilities.invokeLater(GuiClientLauncher::createAndShowGUI);
    }

    /**
     * Creates and shows GUI.
     */
    private static void createAndShowGUI() {

        Logger.getLogger("org.neo4j.driver").setLevel(Level.WARNING);
        JFrame frame = new JFrame(TITLE);
        Optional.ofNullable(GuiClientLauncher.class.getResource(APP_ICON))
                .map(ImageIcon::new).map(ImageIcon::getImage).ifPresent(frame::setIconImage);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(DIMENSION);
        frame.setMinimumSize(DIMENSION);
        frame.setMaximumSize(DIMENSION);
        frame.getContentPane().add(createTabbedPane());
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates tabbed pane.
     *
     * @return the tabbed pane
     */
    private static JTabbedPane createTabbedPane() {

        final JTabbedPane tabbedPane = new JTabbedPane();
        for (int i = 0; i < 2; i++) {
            int key = i;
            final Box box = Box.createVerticalBox();
            final ActionListener actionListener = _ -> {
                if (box.getComponentCount() > 1) {
                    box.remove(box.getComponentCount() - 1);
                }
                box.add(createScrollPane(key));
                box.revalidate();
                box.repaint();
            };
            box.add(createQueryButton(actionListener));
            tabbedPane.addTab(i == 0 ? GET_CUSTOMERS_TITLE : GET_ORDERS_TITLE, null, box, null);
        }
        return tabbedPane;
    }

    /**
     * Creates query button.
     *
     * @param actionListener the action listener
     * @return the button
     */
    private static JButton createQueryButton(ActionListener actionListener) {

        final JButton button = new JButton("<html><font color=#bb0000>Run Query</font>");
        final Dimension dimension = new Dimension(68, 22);
        button.setPreferredSize(dimension);
        button.setMaximumSize(dimension);
        button.setMinimumSize(dimension);
        button.setMargin(new Insets(0, 1, 0, 1));
        button.setVerticalAlignment(SwingConstants.TOP);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(actionListener);
        return button;
    }

    /**
     * Creates scroll pane.
     *
     * @param key the selection key
     * @return the scroll pane
     */
    private static JScrollPane createScrollPane(int key) {
        DefaultMutableTreeNode rootNode;
        DefaultTreeCellRenderer renderer;
        if (key == 0) {
            final List<Customer> customerList = Neo4jClient.getCustomers();
            rootNode = CustomersTree.createTreeNodesForCustomers(customerList);
            renderer = CustomersTree.createRenderer();
        } else {
            final List<Order> orderList = Neo4jClient.getOrders();
            rootNode = OrdersTree.createTreeNodesForOrders(orderList);
            renderer = OrdersTree.createRenderer();
        }
        final JTree tree = new JTree(rootNode);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        renderer.setLeafIcon(null);
        tree.setCellRenderer(renderer);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        return new JScrollPane(tree);
    }
}
