class Clothings extends Product {
    private String size;
    private String color;

    public Clothings(String productId, String productName, int availableItems, double price, String size, String color) {
        super(productId, productName, availableItems, price);
        this.size = size;
        this.color = color;
    }

    public static void clear() {
    }

    public static void add(Product product) {
    }

    // Getters and setters for Clothing-specific attributes
    public void setSize(String size) {
        this.size = size;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }
}