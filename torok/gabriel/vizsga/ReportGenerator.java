package torok.gabriel.vizsga;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class ReportGenerator {

    private static final int TOP_COUNT = 5;
    private static final double DISCOUNT_PERCENT = 10;
    private static final String SAMPLE_MODEL = "D6T";

    private ArrayList<Machine> machines;
    private ArrayList<Part> parts;
    private ArrayList<SalvageEstimate> estimates;

    public ReportGenerator(ArrayList<Machine> machines, ArrayList<Part> parts,
            ArrayList<SalvageEstimate> estimates) {
        this.machines = machines;
        this.parts = parts;
        this.estimates = estimates;
    }

    // polymorphism: works on machines and parts too, through the Sellable interface
    public static int countLossMaking(ArrayList<? extends Sellable> items) {
        int count = 0;
        for (Sellable s : items) {
            if (s.getProfit() < 0) {
                count++;
            }
        }
        return count;
    }

    public static double totalSellingPrice(ArrayList<? extends Sellable> items) {
        double sum = 0;
        for (Sellable s : items) {
            sum += s.getSellingPrice();
        }
        return sum;
    }

    // filtering: parts with the given condition (NEW or USED)
    private ArrayList<Part> filterByCondition(String condition) {
        ArrayList<Part> result = new ArrayList<Part>();
        for (Part p : parts) {
            if (p.getConditionName().equals(condition)) {
                result.add(p);
            }
        }
        return result;
    }

    // aggregation: stock value = sellingPrice * quantity
    private double stockValue(ArrayList<Part> list) {
        double sum = 0;
        for (Part p : list) {
            sum += p.calculateValue();
        }
        return sum;
    }

    // searching: parts fitting a machine model, through the Compatible interface
    private ArrayList<Part> findCompatible(String model) {
        ArrayList<Part> result = new ArrayList<Part>();
        for (Part p : parts) {
            Compatible c = p;
            if (c.isCompatibleWith(model)) {
                result.add(p);
            }
        }
        return result;
    }

    // value of used parts that came from this machine
    private double partsValueFrom(String machineId) {
        double sum = 0;
        for (Part p : parts) {
            if (p instanceof UsedPart) {
                String source = ((UsedPart) p).getSourceMachineId();
                if (source != null && source.equals(machineId)) {
                    sum += p.calculateValue();
                }
            }
        }
        return sum;
    }

    private SalvageEstimate findEstimate(String machineId) {
        for (SalvageEstimate e : estimates) {
            if (e.getMachineId().equals(machineId)) {
                return e;
            }
        }
        return null;
    }

    private String fmt(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    public void writeReport(String path) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(path));
        try {
            out.println("HEAVY MACHINERY REPORT");
            out.println("Valid machines: " + machines.size());
            out.println("Valid parts: " + parts.size());
            out.println("Objects created: " + BaseEntity.getCount());
            out.println();

            out.println("Machines total selling price: " + fmt(totalSellingPrice(machines)));
            out.println("Loss-making machines: " + countLossMaking(machines));
            out.println("Loss-making parts (valid, but unprofitable): " + countLossMaking(parts));
            out.println();

            ArrayList<Part> newParts = filterByCondition("NEW");
            ArrayList<Part> usedParts = filterByCondition("USED");
            out.println("New parts: " + newParts.size() + ", stock value " + fmt(stockValue(newParts)));
            out.println("Used parts: " + usedParts.size() + ", stock value " + fmt(stockValue(usedParts)));
            out.println("Total stock value: " + fmt(stockValue(parts)));

            double discounted = 0;
            for (Part p : parts) {
                discounted += p.calculateValue(DISCOUNT_PERCENT);
            }
            out.println("Total stock value with " + DISCOUNT_PERCENT + "% discount: " + fmt(discounted));
            out.println();

            out.println("DISMANTLING");
            for (Machine m : machines) {
                if (!m.getStatus().equals("FOR_DISMANTLING")) {
                    continue;
                }
                SalvageEstimate e = findEstimate(m.getId());
                if (e == null) {
                    out.println(m + ": no salvage estimate");
                    continue;
                }
                double profit = partsValueFrom(m.getId()) - m.getPurchasePrice()
                        - e.getLaborCost() - e.getTransportCost();
                String verdict = profit > 0 ? "WORTH DISMANTLING" : "NOT WORTH IT";
                out.println(m + " -> salvage profit " + fmt(profit) + " " + verdict);
            }
            out.println();

            // sorting: most valuable stock lines, copy so the original order stays
            ArrayList<Part> sorted = new ArrayList<Part>(parts);
            Collections.sort(sorted, new Comparator<Part>() {
                public int compare(Part a, Part b) {
                    return Double.compare(b.calculateValue(), a.calculateValue());
                }
            });
            out.println("TOP " + TOP_COUNT + " PARTS BY STOCK VALUE");
            for (int i = 0; i < TOP_COUNT && i < sorted.size(); i++) {
                out.println(sorted.get(i) + " " + fmt(sorted.get(i).calculateValue()));
            }
            out.println();

            out.println("PARTS COMPATIBLE WITH " + SAMPLE_MODEL);
            for (Part p : findCompatible(SAMPLE_MODEL)) {
                out.println(p);
            }
        } finally {
            out.close();
        }
    }

    public void writeInvalid(String path, ArrayList<String> invalidItems) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(path));
        try {
            for (String s : invalidItems) {
                out.println(s);
            }
        } finally {
            out.close();
        }
    }
}