/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package torok.gabriel.vizsga;

/**
 *
 * @author ministar
 */
public abstract class Machine extends BaseEntity implements Sellable{
    
    private String manufacturer;
    private String model;
    private int year;
    private double purchasePrice;
    private double estimatedSellingPrice;
    private String status;
    private int hours;
    
    public Machine(String id, String manufacturer, String model, int year,
            double purchasePrice, double estimatedSellingPrice,
            String status, int hours)
    {
        super(id);
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
        this.purchasePrice = purchasePrice;
        this.estimatedSellingPrice = estimatedSellingPrice;
        this.status = status;
        this.hours = hours;
    }
    
    // overloaded constructor: hours unknown, defaults to 0
    public Machine(String id, String manufacturer, String model, int year,
            double purchasePrice, double estimatedSellingPrice, String status) {
        this(id, manufacturer, model, year, purchasePrice,
                estimatedSellingPrice, status, 0);
    }
    
    public abstract String getTypeName();
    
    public String getManufacturer () {return manufacturer;}
    public String getModel() {return model;}
    public int getYear() {return year;}
    public String getStatus() {return status;}
    public int getHours() {return hours;}
    
    public double getPurchasePrice() {return purchasePrice;}
    public double getSellingPrice() {return estimatedSellingPrice;}
    
    public double getProfit()
    {
        return getSellingPrice() - getPurchasePrice();
    }
    
    //overload method: profit after extras costs
    public double getProfit(double extraCosts)
    {
        return getProfit() - extraCosts;
    }
    
    // Business key: id, because it uniquely identifies a machine
    @Override
    public String businessKey() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Machine)) {
            return false;
        }
        return id.equals(((Machine) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getTypeName() + " " + id + " " + manufacturer + " " + model;
    }
    
}
