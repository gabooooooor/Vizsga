/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package torok.gabriel.vizsga;

/**
 *
 * @author ministar
 */
public class Tractor extends Machine {
    
    public Tractor(String id, String manufacturer, String model, int year,
            double purchasePrice, double estimatedSellingPrice,
            String status, int hours) {
        super(id, manufacturer, model, year, purchasePrice,
                estimatedSellingPrice, status, hours);
    }

    @Override
    public String getTypeName() {
        return "TRACTOR";
    }
}
