import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShoppingCartGUI extends JFrame {
    private JComboBox<String> productTypeComboBox;
    private JTable productTable;
    private JTextArea productDetailsTextArea;
    private JButton addToCartButton;
    private JButton viewShoppingCartButton;

    private DefaultTableModel tableModel;
    private List<Product> productList;
    private ShoppingCart shoppingCart;

    private JFrame shoppingCartFrame;


    private void loadFromFile() {
        try (Scanner scanner = new Scanner(new File("Westminster_shopping.csv"))) {
            // Skip the header
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");

                // Extract product information and create instances of Electronics or Clothing
                if (fields.length >= 6) {
                    String productId = fields[0];
                    String productName = fields[1];
                    int availableItems = Integer.parseInt(fields[2]);
                    double price = Double.parseDouble(fields[3]);
                    String type = fields[4];

                    if ("Electronics".equals(type)) {
                        String brand = fields[5];
                        int warrantyPeriod = Integer.parseInt(fields[6]);
                        productList.add(new Electronics(productId, productName, availableItems, price, brand, warrantyPeriod));
                    } else if ("Clothing".equals(type)) {
                        String size = fields[5];
                        String color = fields[6];
                        productList.add(new Clothings(productId, productName, availableItems, price, size, color) {
                            @Override
                            public void decreaseAvailableItems() {

                            }
                        });
                    }
                }
            }

            System.out.println("Product list loaded from file successfully.");
        } catch (IOException e) {
            System.err.println("Error loading product list from file: " + e.getMessage());
        }
    }

    public ShoppingCartGUI() {
        // Initialize data
        productList = new ArrayList<>();
        loadFromFile();
        shoppingCart = new ShoppingCart();

        // Create UI components
        productTypeComboBox = new JComboBox<>(new String[]{"All", "Electronics", "Clothes"});
        productTable = new JTable();
        productDetailsTextArea = new JTextArea();
        addToCartButton = new JButton("Add to Cart");
        viewShoppingCartButton = new JButton("Shopping Cart");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(viewShoppingCartButton);

        // Add the buttons panel to the frame
        add(buttonPanel, BorderLayout.NORTH);


        // Set up table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Product ID");
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Available Items");
        tableModel.addColumn("Price");
        tableModel.addColumn("Info");
        productTable.setModel(tableModel);

        // Set up layout
        setLayout(new BorderLayout());

        // Create a split pane to separate the table and details panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(productTable), createDetailsPanel());

        // Add the split pane to the frame
        add(splitPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Product Type: "));
        topPanel.add(productTypeComboBox);
        add(topPanel, BorderLayout.NORTH);

        add(viewShoppingCartButton, BorderLayout.SOUTH);

        // Set up event handlers
        productTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterProductsByType((String) productTypeComboBox.getSelectedItem());
            }
        });

        productTable.getSelectionModel().addListSelectionListener(e -> {
            List<Product> electronics = new ArrayList<>();
            List<Product> clothing = new ArrayList<>();
            for (Product product : productList) {
                if (product instanceof Electronics) {
                    electronics.add(product);
                } else {
                    clothing.add(product);
                }
            }

            if ("All".equals((String) productTypeComboBox.getSelectedItem())) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    showProductDetails(productList.get(selectedRow));
                }
            } else if ("Electronics".equals((String) productTypeComboBox.getSelectedItem())) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    showProductDetails(electronics.get(selectedRow));
                }
            } else {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    showProductDetails(clothing.get(selectedRow));
                }
            }
        });

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Product> electronics = new ArrayList<>();
                List<Product> clothing = new ArrayList<>();
                for (Product product : productList) {
                    if (product instanceof Electronics) {
                        electronics.add(product);
                    } else {
                        clothing.add(product);
                    }
                }

                if ("All".equals((String) productTypeComboBox.getSelectedItem())) {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        addProductToCart(productList.get(selectedRow));
                    }
                } else if ("Electronics".equals((String) productTypeComboBox.getSelectedItem())) {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        addProductToCart(electronics.get(selectedRow));
                    }
                } else {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        addProductToCart(clothing.get(selectedRow));
                    }
                }
            }
        });



        viewShoppingCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showShoppingCart();
            }
        });
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.add(new JLabel("Product Details:"), BorderLayout.NORTH);
        detailsPanel.add(new JScrollPane(productDetailsTextArea), BorderLayout.CENTER);
        detailsPanel.add(addToCartButton, BorderLayout.SOUTH);
        return detailsPanel;
    }

    private void filterProductsByType(String productType) {
        // Clear existing rows in the table model
        tableModel.setRowCount(0);

        if ("All".equals(productType)) {
            // Add all products to the table
            for (Product product : productList) {
                addProductToTableModel(product, 1);
            }
        } else {
            // Add only products of the selected type to the table
            for (Product product : productList) {
                if (("Electronics".equals(productType) && product instanceof Electronics) ||
                        ("Clothes".equals(productType) && product instanceof Clothings)) {
                    addProductToTableModel(product, 1);
                }
            }
        }
    }

    private void addProductToTableModel(Product product, int i) {
        // Add a row to the table model with product information
        Object[] rowData = new Object[]{product.getProductId(), product.getProductName(),
                product.getAvailableItems(), product.getPrice(), getProductInfo(product)};
        tableModel.addRow(rowData);

    }
    private String getProductInfo(Product product) {
        // Return additional information based on the product type
        if (product instanceof Electronics) {
            Electronics electronics = (Electronics) product;
            return "Brand: " + electronics.getBrand() + ", Warranty: " + electronics.getWarrantyPeriod() + " months";
        } else if (product instanceof Clothings) {
            Clothings clothings = (Clothings) product;
            return "Size: " + clothings.getSize() + ", Color: " + clothings.getColor();
        } else {
            return ""; // Empty string for products without additional information
        }
    }



    private void showProductDetails(Product selectedProduct) {
        if (selectedProduct != null) {
            StringBuilder details = new StringBuilder();
            details.append("Product ID: ").append(selectedProduct.getProductId()).append("\n");
            details.append("Product Name: ").append(selectedProduct.getProductName()).append("\n");
            details.append("Available Items: ").append(selectedProduct.getAvailableItems()).append("\n");
            details.append("Price: LKR").append(selectedProduct.getPrice()).append("\n");

            if (selectedProduct instanceof Electronics) {
                details.append("Brand: ").append(((Electronics) selectedProduct).getBrand()).append("\n");
                details.append("Warranty Period: ").append(((Electronics) selectedProduct).getWarrantyPeriod()).append(" months\n");
            } else if (selectedProduct instanceof Clothings) {
                details.append("Size: ").append(((Clothings) selectedProduct).getSize()).append("\n");
                details.append("Color: ").append(((Clothings) selectedProduct).getColor()).append("\n");
            }

            productDetailsTextArea.setText(details.toString());
        } else {
            productDetailsTextArea.setText(""); // Clear the text area if no product is selected
        }
    }




    private void addProductToCart(Product selectedProduct) {
        if (selectedProduct != null) {
            // Check if the selected product is available
            if (selectedProduct.getAvailableItems() > 0) {
                // Decrease the available items and add the product to the shopping cart
                selectedProduct.decreaseAvailableItems();
                shoppingCart.addProduct(selectedProduct);

                // Add the product to the table model with quantity 1
                // addProductToTableModel(selectedProduct,1);

                // Display a message or perform any other actions as needed
                double finalPrice = shoppingCart.calculateTotalCost();
                // Update the UI or display the final price somewhere
            } else {
                JOptionPane.showMessageDialog(this, "The selected product is out of stock.", "Out of Stock", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }



    private void showShoppingCart() {
        List<Product> cartProducts = shoppingCart.getProducts();
        double totalCost = shoppingCart.calculateTotalCost();
        double discount = 0;

        // Apply 10% discount for the first purchase
        if (!shoppingCart.isDiscountApplied()) {
            discount += totalCost * 0.1;
            shoppingCart.setDiscountApplied(true);
        }

        // Check if there are three or more items of the same type
        int electroCount = countProductsOfTypeElectronics();
        int clothCount = countProductsOfTypeClothing();

        if (electroCount >= 3 || clothCount >= 3) {
            discount += totalCost * 0.2;
        }

        // Calculate the final price after discounts
        double finalPrice = totalCost - discount;

        // Create a DecimalFormat object with a pattern for two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        // Format the total cost, 10% discount, 20% discount, and final cost to a string with two decimal places
        String totalCostString = decimalFormat.format(totalCost);
        String discountTenString = decimalFormat.format(totalCost * 0.1);
        String discountTwentyString = decimalFormat.format(totalCost * 0.2);
        String finalPriceString = decimalFormat.format(finalPrice);

        // If shoppingCartFrame is null or not visible, create a new one
        if (shoppingCartFrame == null || !shoppingCartFrame.isVisible()) {
            shoppingCartFrame = new JFrame("Shopping Cart Details");
            shoppingCartFrame.setSize(600, 400);
        }

        // Create a table model for the details table
        DefaultTableModel detailsTableModel = new DefaultTableModel();
        detailsTableModel.setColumnIdentifiers(new Object[]{"Product Name", "Price", "Quantity", "Total"});
        JTable detailsTable = new JTable(detailsTableModel);

        // Populate the details table with cart products
        for (Product product : cartProducts) {
            detailsTableModel.addRow(new Object[]{product.getProductName(), product.getPrice(), 1, product.getPrice()});
        }

        // Set preferred size for the details table
        detailsTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        detailsTable.setFillsViewportHeight(true);

        // Add the details table to a scroll pane
        JScrollPane detailsScrollPane = new JScrollPane(detailsTable);

        // Set up layout for the shopping cart frame
        shoppingCartFrame.setLayout(new BorderLayout());


        // Create a panel for labels
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new GridLayout(0, 1)); // Display labels in a single column

        // Add labels for total cost, 10% discount, 20% discount, and final price
        labelsPanel.add(new JLabel("Total Cost                                                      : " + totalCostString + " LKR"));
        labelsPanel.add(new JLabel("First Purchase Discount(10%                         : " + discountTenString + " LKR"));
        labelsPanel.add(new JLabel("Three or More item in same category (20%): " + discountTwentyString + "LKR"));
        labelsPanel.add(new JLabel("Total Price                                                         : " + finalPriceString + " LKR"));

        // Add the labels panel to the frame
        shoppingCartFrame.add(labelsPanel, BorderLayout.SOUTH);

        // Add the details scroll pane to the frame
        shoppingCartFrame.add(detailsScrollPane, BorderLayout.CENTER);

        // Make the shopping cart frame visible
        shoppingCartFrame.setVisible(true);
    }

    private int countProductsOfTypeElectronics() {
        int electroCount = 0;
        for (Product product : shoppingCart.getProducts()) {
            if (product instanceof Electronics) {
                electroCount++;
            }
        }
        return electroCount;
    }

    private int countProductsOfTypeClothing() {
        int clothCount = 0;
        for (Product product : shoppingCart.getProducts()) {
            if (product instanceof Clothings) {
                clothCount++;
            }
        }
        return clothCount;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ShoppingCartGUI shoppingCartGUI = new ShoppingCartGUI();
                shoppingCartGUI.setSize(900, 700);
                shoppingCartGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                shoppingCartGUI.setVisible(true);
            }
        });
    }


}