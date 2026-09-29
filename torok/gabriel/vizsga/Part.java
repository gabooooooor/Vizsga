package torok.gabriel.vizsga;

import java.util.ArrayList;

public abstract class Part extends BaseEntity implements Sellable, Compatible {

    private String partNumber;
    private String name;
    private String category;
    private double purchasePrice;
    private double sellingPrice;
    private int quantity;
    private ArrayList<String> compatibleModels;

    public Part(String id, String partNumber, String name, String category,
            double purchasePrice, double sellingPrice, int quantity,
            ArrayList<String> compatibleModels) {
        super(id);
        this.partNumber = partNumber;
        this.name = name;
        this.category = category;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.compatibleModels = compatibleModels;
    }

    public abstract String getConditionName();

    public String getPartNumber() { return partNumber; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public ArrayList<String> getCompatibleModels() { return compatibleModels; }

    public double getPurchasePrice() { return purchasePrice; }
    public double getSellingPrice() { return sellingPrice; }

    public double getProfit() {
        return sellingPrice - purchasePrice;
    }

    // stock value of this part
    public double calculateValue() {
        return sellingPrice * quantity;
    }

    // overloaded: stock value with a discount in percent
    public double calculateValue(double discountPercent) {
        return calculateValue() * (100 - discountPercent) / 100;
    }

    public boolean isCompatibleWith(String model) {
        return compatibleModels.contains(model);
    }

    // Business key: partNumber, because it uniquely identifies a part
    @Override
    public String businessKey() {
        return partNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Part)) {
            return false;
        }
        return partNumber.equals(((Part) o).partNumber);
    }

    @Override
    public int hashCode() {
        return partNumber.hashCode();
    }

    @Override
    public String toString() {
        return getConditionName() + " part " + partNumber + " " + name;
    }
}