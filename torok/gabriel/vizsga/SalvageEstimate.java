package torok.gabriel.vizsga;

public class SalvageEstimate {

    private String machineId;
    private double laborCost;
    private double transportCost;

    public SalvageEstimate(String machineId, double laborCost, double transportCost) {
        this.machineId = machineId;
        this.laborCost = laborCost;
        this.transportCost = transportCost;
    }

    public String getMachineId() { return machineId; }
    public double getLaborCost() { return laborCost; }
    public double getTransportCost() { return transportCost; }
}