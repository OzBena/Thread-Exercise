import java.util.ArrayList;

public class Branch implements Runnable, Node<Branch> {
    private static int branchIdCnt = 0;
    protected int branchID, nextVan = 0;
    protected ArrayList<Truck> listTrucks;
    protected ArrayList<Package> listPackages;
    protected int availableTrucks = 0;
    private int availablePackages = 0;
    Hub pSort;

    public Branch(Hub hub) {
        this.branchID = branchIdCnt;
        String bName = toString();
        if (branchID == 0)
            bName = "HUB";
        branchIdCnt++;
        listTrucks = new ArrayList<>();
        listPackages = new ArrayList<>();
        System.out.println("Creating Branch " + branchID + ", Branch name: " + bName + ", Packages: " + listPackages.size() + ", Trucks: " + listTrucks.size());
        pSort = hub;
    }

    public void wakeUp() {
        synchronized (this) {
            this.notifyAll();
        }
    }


    @Override
    public void run() {
        while (!pSort.lastDelivered) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
            checkAvailable();
            while (availablePackages != 0 && availableTrucks != 0) {
                Van readyV = null;
                while (availableTrucks != 0) {
                    if (listTrucks.get(nextVan).available) {
                        readyV = (Van) listTrucks.get(nextVan);
                        nextVan = (nextVan +1) % listTrucks.size();
                        break;
                    }
                    nextVan = (nextVan +1) % listTrucks.size();
                }
                setTask(readyV);
                decAvailablePackages();
                readyV.begin();
            }
        }
    }

    private synchronized void setTask(Van readyV) {
        readyV.setTimeLeft(eta());
        readyV.available = false;
        for(Package pack: listPackages) {
        	if(pack.listedTruckId == -1)
        		if( pack.status == Status.CREATION || pack.status == Status.DELIVERY)
        				pack.setListedTruckId(readyV.truckID);
        }
        availableTrucks--;
    }

    private synchronized void checkAvailable() {
        availablePackages = 0;
        for (int i = 0; i < listPackages.size(); i++) {
            Status curStatus = listPackages.get(i).status;
            if (curStatus == Status.CREATION || curStatus == Status.DELIVERY)
                availablePackages++;
        }
    }

    public synchronized void collectPackage(Package p) {
        listPackages.add(p);
        p.statusChange();
        p.addTracking(this, p.status);
        
    }

    public synchronized void deliverPackage(Package p) {
        listPackages.remove(p);
    }

    @Override
    public String toString() {
        if (branchID == 0)
            return "HUB";
        return "Branch " + branchID;
    }

    private int eta() {                  // eta = estimated time of arrival
        int time = (listPackages.get(0).senderAddress.getStreet() % 10) + 1;
        return time;
    }

    public synchronized void incAvailablePackages() {
        availablePackages++;
    }

    private synchronized void decAvailablePackages() {
        availablePackages--;
    }

}
