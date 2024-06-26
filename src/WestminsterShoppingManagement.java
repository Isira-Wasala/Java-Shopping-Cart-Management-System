import java.io.*;
import java.util.*;
import javax.swing.*;

class WestminsterShoppingManagement implements ShoppingManager {
    private List<Product> Products;

    public WestminsterShoppingManagement() {
        this.Products = new ArrayList<>();
    }

    // Other methods for managing the product list and system operations

    public void Menu() {

        Scanner scanner = new Scanner(System.in);
        int choose;
        do {;
            System.out.println("-----------Welcome to the Management Portral-------------");
            System.out.println("1. Add new product : ");
            System.out.println("2. Delete Added Product :");
            System.out.println("3. Display the Added Product List :");
            System.out.println("4. Save the Details in CSV File :");
            System.out.println("5. Open the Shopping Store (GUi) :");
            System.out.println("6. Exit from the program :");
            System.out.println("Enter your choice here :");
            choose = scanner.nextInt();

            switch (choose) {
                case 1:
                    ShoppingCartGUI r = new ShoppingCartGUI();
                    AddProducts();
                    break;
                case 2:
                    DeleteProducts();
                    break;
                case 3:
                    PrintProducts();
                    break;
                case 4:
                    SaveFile();
                    break;

                case 5:
                    // Load ShoppingCartGUI
                    OpenShoppingCartGui();
                    break;
                case 0:
                    System.out.println("Logging out from the store. Goodbye!");
                    break;
                default:
                    System.out.println("Enter character is invalid. please try again!");
                    break;
            }
        } while (choose != 0);
    }

    private void AddProducts() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Add products here ");
        System.out.println("1.Electronic Items");
        System.out.println("2.Clothing Items");
        System.out.print("Type 1 or 2 to choose the product type : ");

        int chooseProducts = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String Id, productItemName;
        int availableItems;
        double price;

        switch (chooseProducts) {
            case 1:
                // Add Electronics
                System.out.print("Enter the brand name: ");
                String brand = scanner.nextLine();
                System.out.print("Warranty period (In Months): ");
                int warrantyPeriod = scanner.nextInt();

                while (true) {
                    System.out.print("Enter product ID: ");
                    Id = scanner.next();
                    if (checkingAvailabilityOfProduct(Id)) {
                        System.out.println("Sorry, the entered product is already uses. Please try again!");
                    } else {
                        break;
                    }
                }

                System.out.print("Enter product name: ");
                productItemName = scanner.next();
                System.out.print("Enter the number of items to add : ");
                availableItems = scanner.nextInt();
                System.out.print("Enter Price : LKR ");
                price = scanner.nextDouble();

                // Create and add Electronics to the product list
                Electronics electronics = new Electronics(Id, productItemName, availableItems, price, brand, warrantyPeriod);
                Products.add(electronics);

                System.out.println("Successfully saved the added items");
                break;

            case 2:
                // Add Clothing
                System.out.print("Enter the size of cloth: ");
                String size = scanner.next();
                System.out.print("Enter the colour: ");
                String color = scanner.next();

                System.out.print("Enter the ID : ");
                Id = scanner.next();
                System.out.print("Enter the product name: ");
                productItemName = scanner.next();
                System.out.print("Enter the number of items to add : ");
                availableItems = scanner.nextInt();
                System.out.print("Enter price: ");
                price = scanner.nextDouble();

                // Create and add Clothing to the product list
                Clothings clothing = new Clothings(Id, productItemName, availableItems, price, size, color);
                Products.add(clothing);

                System.out.println("Successfully added the clothing items");
                break;

            default:
                System.out.println("The entered choice is invalid, please try again!");
                break;
        }
    }

    private boolean checkingAvailabilityOfProduct(String productId) {
        for (Product product : Products) {
            if (product.getProductId().equals(productId)) {
                return true; // Product ID already exists
            }
        }
        return false; // Product ID does not exist
    }


    private void DeleteProducts() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter ID to delete : ");
        String productIdToDelete = scanner.next();

        // Search for the product with the given ID
        Product productsToDelete = null;
        for (Product product : Products) {
            if (product.getProductId().equals(productIdToDelete)) {
                productsToDelete = product;
                break;
            }
        }

        if (productsToDelete != null) {
            // Display information about the product being deleted
            System.out.println("Processing the deletion !");
            System.out.println(productToString(productsToDelete));

            // Remove the product from the list
            Products.remove(productsToDelete);

            System.out.println("Successfully deleted the product!");
        } else {
            System.out.println("Entered product ID - '" + productIdToDelete + "' is not in the system. Please try again!");
        }
    }


    private void PrintProducts() {
        // Implement logic to print the list of products in alphabetical order based on the product ID
        Collections.sort(Products, Comparator.comparing(Product::getProductId));

        for (Product product : Products) {
            System.out.println(productToString(product));
        }
    }

    private void SaveFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Westminster_shopping.csv"))) {
            // Write header
            writer.println("ProductID,ProductName,AvailableItems,Price,Type,Brand,WarrantyPeriod,Size,Color");

            // Write each product
            for (Product product : Products) {
                if (product instanceof Electronics) {
                    writer.println(product.getProductId() + ","
                            + product.getProductName() + "," +
                            product.getAvailableItems() + ","
                            + product.getPrice() + ",Electronics," +
                            ((Electronics) product).getBrand() + ","
                            + ((Electronics) product).getWarrantyPeriod());
                } else if (product instanceof Clothings) {
                    writer.println(product.getProductId() + ","
                            + product.getProductName() + "," +
                            product.getAvailableItems() + ","
                            + product.getPrice() + ",Clothing," + "," + "," +
                            ((Clothings) product).getSize() + ","
                            + ((Clothings) product).getColor());
                }
            }

            System.out.println("Successfully saved the product!!!.");
        } catch (IOException e) {
            System.err.println("There is an error with the adding - " + e.getMessage());
        }
    }


    private String productToString(Product product) {
        if (product instanceof Electronics) {
            return "Electronics: " + product.getProductId() + " - " + product.getProductName() +
                    " (Brand: " + ((Electronics) product).getBrand() + ", Warranty: " + ((Electronics) product).getWarrantyPeriod() + ")";
        } else if (product instanceof Clothings) {
            return "Clothing: " + product.getProductId() + " - " + product.getProductName() +
                    " (Size: " + ((Clothings) product).getSize() + ", Color: " + ((Clothings) product).getColor() + ")";
        } else {
            return "Mismatched Product Type";
        }
    }

    private static void OpenShoppingCartGui() {
        SwingUtilities.invokeLater(() -> {
            ShoppingCartGUI shoppingCartGUI = new ShoppingCartGUI();
            shoppingCartGUI.setSize(800, 600);
            shoppingCartGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            shoppingCartGUI.setVisible(true);
        });
    }
}