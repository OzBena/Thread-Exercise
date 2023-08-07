public class SmallPackage extends Package {
    boolean acknowledge;
    double weight;

    public SmallPackage(Address senderAddress, Address destinationAddress, boolean acknowledge) {
        super(senderAddress, destinationAddress);
        this.acknowledge = acknowledge;
        this.weight = 1;
    }

    @Override
    public String toString() {
        return "SmallPackage [packageID=" + packageID + ", status=" + status + ", senderAddress=" + senderAddress + ", destinationAddress=" + destinationAddress + ", acknowledge=" + acknowledge + "]";
    }
}
