package torok.gabriel.vizsga;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TorokGabrielVizsga {

    private static final String INPUT_FILE = "data.json";
    private static final String REPORT_FILE = "report.txt";
    private static final String INVALID_FILE = "invalid_items.txt";

    public static void main(String[] args) {
        DataLoader loader = new DataLoader();
        try {
            loader.readData(INPUT_FILE);
        } catch (FileNotFoundException e) {
            System.out.println(INPUT_FILE + " not found: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("Read error: " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            // broken JSON syntax or missing main sections
            System.out.println("Invalid file structure: " + e.getMessage());
            return;
        }

        System.out.println("Machines: " + loader.getMachines().size());
        System.out.println("Parts: " + loader.getParts().size());
        System.out.println("Invalid records: " + loader.getInvalidItems().size());

        ReportGenerator gen = new ReportGenerator(loader.getMachines(),
                loader.getParts(), loader.getEstimates());
        try {
            gen.writeReport(REPORT_FILE);
            gen.writeInvalid(INVALID_FILE, loader.getInvalidItems());
            System.out.println(REPORT_FILE + " and " + INVALID_FILE + " written.");
        } catch (IOException e) {
            System.out.println("Cannot write output: " + e.getMessage());
        }
    }
}