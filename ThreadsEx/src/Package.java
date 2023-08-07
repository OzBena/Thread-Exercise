import java.util.ArrayList;

public class Package {
    static int packageIdCnt = 1000;
    int packageID,
            listedTruckId = -1;
    Status status;
    Address senderAddress,
            destinationAddress;
    ArrayList tracking;

    public Package(Address senderAddress, Address destinationAddress) {
        this.senderAddress = senderAddress;
        this.destinationAddress = destinationAddress;
        packageID = packageIdCnt;
        tracking = new ArrayList();
        packageIdCnt++;
    }

    public void addTracking(Node node, Status status) {
        tracking.add(new Tracking(node, status));
    }

    public void printTracking(String s) {
        System.out.println("TRACKING " + s);
        for (int i = 0; i < tracking.size(); i++) {
            System.out.println(tracking.get(i));
        }
    }

    public void setListedTruckId(int listedTruckId) {
        this.listedTruckId = listedTruckId;
    }

    public void statusChange() {
        switch (status) {
            case CREATION:
                status = Status.COLLECTION;
                break;
            case COLLECTION:
                status = Status.BRANCH_STORAGE;
                break;
            case BRANCH_STORAGE:
                status = Status.HUB_TRANSPORT;
                break;
            case HUB_TRANSPORT:
                status = Status.HUB_STORAGE;
                break;
            case HUB_STORAGE:
                status = Status.BRANCH_TRANSPORT;
                break;
            case BRANCH_TRANSPORT:
                status = Status.DELIVERY;
                break;
            case DELIVERY:
                status = Status.DISTRIBUTION;
                break;
            case DISTRIBUTION:
                status = Status.DELIVERED;
                break;
        }
    }
}
