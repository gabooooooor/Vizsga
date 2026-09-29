package torok.gabriel.vizsga;

import java.util.ArrayList;

public class UsedPart extends Part {

    private String sourceMachineId;

    public UsedPart(String id, String partNumber, String name, String category,
            double purchasePrice, double sellingPrice, int quantity,
            ArrayList<String> compatibleModels, String sourceMachineId) {
        super(id, partNumber, name, category, purchasePrice,
                sellingPrice, quantity, compatibleModels);
        this.sourceMachineId = sourceMachineId;
    }

    public String getSourceMachineId() {
        return sourceMachineId;
    }

    @Override
    public String getConditionName() {
        return "USED";
    }
}