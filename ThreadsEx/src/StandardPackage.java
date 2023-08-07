public class StandardPackage extends Package {
    double weight;

    public StandardPackage(Address senderAddress, Address destinationAddress, double weight) {
        super(senderAddress, destinationAddress);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "StandardPackage [packageID=" + packageID + ", status=" + status + ", senderAddress=" + senderAddress + ", destinationAddress=" + destinationAddress + ", weight=" + weight + "]";
    }
}
