import java.util.ArrayList;

public class Van extends Truck {
    private Branch home;
    private Package myTask = null;
    private int myTaskIndex = -1, vTime = 300;

    public Van(Branch br) {
        super();
        this.home = br;
        home.availableTrucks++;
        System.out.println("Creating Van [truckID = " + truckID + ", available = " + available + "]");
    }

    @Override
    public synchronized void collectPackage(Package p) {
        super.collectPackage(p);
        p.addTracking(this, p.status);
    }

    @Override
    public void run() {
        while (!home.pSort.lastDelivered) {
            while (available) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }

            }
            myTask = findPackage(truckID);
            int travelBack = timeLeft;
            if (myTask != null) {
                if (myTask.status == Status.CREATION) {
                    System.out.println(toString() + " has collected package " + myTask.packageID + ", time to arrive: " + timeLeft);
                    drive(vTime);
                    collectPackage(myTask);
                    home.deliverPackage(myTask);
                    timeLeft = travelBack;
                    drive(vTime);
                    packages.get(0).setListedTruckId(-1);
                    deliverPackage(myTask);
                    home.collectPackage(myTask);
                    available = true;
                    home.availableTrucks++;
                    System.out.println(toString() + " has collected package " + myTask.packageID + " and arrived back to " + home.toString());
                }
                if (myTask.status == Status.DELIVERY) {
                    collectPackage(myTask);
                    home.deliverPackage(myTask);
                    System.out.println(toString() + " is delivering package " + myTask.packageID + ", time to arrive: " + timeLeft);
                    drive(vTime);
                    deliverPackage(myTask);
                    timeLeft = travelBack;
                    drive(vTime);
                    available = true;
                    home.availableTrucks++;
                    System.out.println(toString() + " has delivered package " + myTask.packageID + " to the destination");
                    if (myTask instanceof SmallPackage)
                        if (((SmallPackage) myTask).acknowledge == true)
                            System.out.println("Acknowledge sent for package " + myTask.packageID);
                    home.pSort.mainOffice.deliveriesMade++;
                    if (home.pSort.mainOffice.deliveriesMade == home.pSort.mainOffice.expectedDeliveries) {
                        try {
                            home.pSort.lastDelivered = true;
                            home.pSort.mainOffice.stopThreads();
                            home.pSort.listTrucks.get(0).begin();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            }
            myTask = null;
            available = true;
            home.availableTrucks++;
        }
    }

    private synchronized Package findPackage(int id) {
        for (int i = 0; i < home.listPackages.size(); i++) {
            Package pack = home.listPackages.get(i);
            if (pack != null && pack.listedTruckId == id) {
                myTaskIndex = i;
                return pack;
            }
        }
        myTaskIndex = -1;
        return null;
    }

    @Override
    public String toString() {
        return "Van " + truckID;
    }

    @Override
    public void begin() {
        synchronized (this) {
            this.notifyAll();
        }
    }
}