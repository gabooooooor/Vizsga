
package torok.gabriel.vizsga;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TorokGabrielVizsga {

        public static void main(String[] args) {
        DataLoader loader = new DataLoader();
        try {
            loader.readData("data.json");
        } catch (FileNotFoundException e) {
            System.out.println("data.json not found: " + e.getMessage());
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
        for (String s : loader.getInvalidItems()) {
            System.out.println("INVALID " + s);
        }
    }
    
}
