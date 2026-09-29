package torok.gabriel.vizsga;

import java.util.ArrayList;

public class NewPart extends Part {

    public NewPart(String id, String partNumber, String name, String category,
            double purchasePrice, double sellingPrice, int quantity,
            ArrayList<String> compatibleModels) {
        super(id, partNumber, name, category, purchasePrice,
                sellingPrice, quantity, compatibleModels);
    }

    @Override
    public String getConditionName() {
        return "NEW";
    }
}