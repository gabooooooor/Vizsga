package torok.gabriel.vizsga;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataLoader {

    private static final int YEAR_LENGTH = 4;

    private ArrayList<Machine> machines = new ArrayList<Machine>();
    private ArrayList<Part> parts = new ArrayList<Part>();
    private ArrayList<SalvageEstimate> estimates = new ArrayList<SalvageEstimate>();
    private ArrayList<String> invalidItems = new ArrayList<String>();

    // rules read from the "meta" part of data.json
    private int minYear;
    private int maxHours;
    private int currentYear;
    private ArrayList<String> allowedStatuses;
    private ArrayList<String> allowedCategories;

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    public ArrayList<SalvageEstimate> getEstimates() {
        return estimates;
    }

    public ArrayList<String> getInvalidItems() {
        return invalidItems;
    }

    public void readData(String path) throws IOException {
        FileReader reader = new FileReader(path);
        try {
            JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
            currentYear = Integer.parseInt(
                    root.get("inventoryDate").getAsString().substring(0, YEAR_LENGTH));
            loadMeta(root.getAsJsonObject("meta"));
            loadMachines(root.getAsJsonArray("machines"));
            loadParts(root.getAsJsonArray("parts"));
            loadEstimates(root.getAsJsonArray("salvageEstimates"));
        } finally {
            reader.close();
        }
    }

    private void loadMeta(JsonObject meta) {
        minYear = meta.get("minimumValidYear").getAsInt();
        maxHours = meta.get("maximumMachineHours").getAsInt();
        allowedStatuses = toList(meta.getAsJsonArray("allowedStatuses"));
        allowedCategories = toList(meta.getAsJsonArray("allowedCategories"));
    }

    private ArrayList<String> toList(JsonArray array) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < array.size(); i++) {
            list.add(array.get(i).getAsString());
        }
        return list;
    }

    private String requireString(JsonObject o, String field)
            throws DomainValidationException {
        JsonElement e = o.get(field);
        if (e == null || e.isJsonNull()) {
            throw new DomainValidationException("missing field: " + field);
        }
        return e.getAsString();
    }

    private String describe(JsonElement el) {
        try {
            return el.getAsJsonObject().get("id").getAsString();
        } catch (RuntimeException e) {
            return "unknown";
        }
    }

    private void loadMachines(JsonArray array) {
        for (int i = 0; i < array.size(); i++) {
            JsonElement el = array.get(i);
            try {
                Machine m = parseMachine(el.getAsJsonObject());
                validateMachine(m);
                machines.add(m);
            } catch (DomainValidationException e) {
                invalidItems.add("Machine " + describe(el) + ": " + e.getMessage());
            } catch (RuntimeException e) {
                invalidItems.add("Machine " + describe(el) + ": parsing error ("
                        + e.getClass().getSimpleName() + ")");
            }
        }
    }

    private Machine parseMachine(JsonObject o) throws DomainValidationException {
        String id = requireString(o, "id");
        String type = requireString(o, "type");
        String manufacturer = requireString(o, "manufacturer");
        String model = requireString(o, "model");
        String status = requireString(o, "status");
        int year = o.get("year").getAsInt();
        double purchase = o.get("purchasePrice").getAsDouble();
        double selling = o.get("estimatedSellingPrice").getAsDouble();
        int hours = o.get("hours").getAsInt();

        if (type.equals("TRACTOR")) {
            return new Tractor(id, manufacturer, model, year, purchase, selling, status, hours);
        }
        if (type.equals("BULLDOZER")) {
            return new Bulldozer(id, manufacturer, model, year, purchase, selling, status, hours);
        }
        throw new DomainValidationException("unknown machine type: " + type);
    }

    private void validateMachine(Machine m) throws DomainValidationException {
        if (m.getManufacturer().trim().length() == 0) {
            throw new DomainValidationException("empty manufacturer");
        }
        if (m.getYear() < minYear || m.getYear() > currentYear) {
            throw new DomainValidationException("invalid year: " + m.getYear());
        }
        if (m.getPurchasePrice() < 0 || m.getSellingPrice() < 0) {
            throw new DomainValidationException("negative price");
        }
        if (m.getHours() < 0 || m.getHours() > maxHours) {
            throw new DomainValidationException("invalid hours: " + m.getHours());
        }
        if (!allowedStatuses.contains(m.getStatus())) {
            throw new DomainValidationException("invalid status: " + m.getStatus());
        }
        if (machines.contains(m)) {
            throw new DomainValidationException("duplicate id");
        }
    }

    private void loadParts(JsonArray array) {
        for (int i = 0; i < array.size(); i++) {
            JsonElement el = array.get(i);
            try {
                Part p = parsePart(el.getAsJsonObject());
                validatePart(p);
                parts.add(p);
            } catch (DomainValidationException e) {
                invalidItems.add("Part " + describe(el) + ": " + e.getMessage());
            } catch (RuntimeException e) {
                invalidItems.add("Part " + describe(el) + ": parsing error ("
                        + e.getClass().getSimpleName() + ")");
            }
        }
    }

    private Part parsePart(JsonObject o) throws DomainValidationException {
        String id = requireString(o, "id");
        String partNumber = requireString(o, "partNumber");
        String name = requireString(o, "name");
        String category = requireString(o, "category");
        String condition = requireString(o, "condition");
        double purchase = o.get("purchasePrice").getAsDouble();
        double selling = o.get("sellingPrice").getAsDouble();
        int quantity = o.get("quantity").getAsInt();
        // a plain string here throws ClassCastException -> parsing error
        ArrayList<String> models = toList(o.getAsJsonArray("compatibleModels"));

        if (condition.equals("NEW")) {
            return new NewPart(id, partNumber, name, category, purchase, selling, quantity, models);
        }
        if (condition.equals("USED")) {
            String source = null;
            JsonElement s = o.get("sourceMachineId");
            if (s != null && !s.isJsonNull()) {
                source = s.getAsString();
            }
            return new UsedPart(id, partNumber, name, category, purchase, selling,
                    quantity, models, source);
        }
        throw new DomainValidationException("invalid condition: " + condition);
    }

    private Machine findMachine(String id) {
        for (int i = 0; i < machines.size(); i++) {
            if (machines.get(i).getId().equals(id)) {
                return machines.get(i);
            }
        }
        return null;
    }

    private void validatePart(Part p) throws DomainValidationException {
        if (p.getName().trim().length() == 0) {
            throw new DomainValidationException("empty name");
        }
        if (p.getPurchasePrice() < 0 || p.getSellingPrice() < 0) {
            throw new DomainValidationException("negative price");
        }
        if (p.getQuantity() < 0) {
            throw new DomainValidationException("negative quantity");
        }
        if (!allowedCategories.contains(p.getCategory())) {
            throw new DomainValidationException("invalid category: " + p.getCategory());
        }
        if (parts.contains(p)) {
            throw new DomainValidationException("duplicate partNumber: " + p.getPartNumber());
        }
        if (p instanceof UsedPart) {
            String source = ((UsedPart) p).getSourceMachineId();
            if (source != null && findMachine(source) == null) {
                throw new DomainValidationException("unknown source machine: " + source);
            }
        }
    }

    private void loadEstimates(JsonArray array) {
        for (int i = 0; i < array.size(); i++) {
            try {
                JsonObject o = array.get(i).getAsJsonObject();
                estimates.add(new SalvageEstimate(
                        o.get("machineId").getAsString(),
                        o.get("estimatedLaborCost").getAsDouble(),
                        o.get("estimatedTransportCost").getAsDouble()));
            } catch (RuntimeException e) {
                invalidItems.add("SalvageEstimate #" + i + ": parsing error");
            }
        }
    }
}