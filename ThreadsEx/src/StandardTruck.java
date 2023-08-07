public class StandardTruck extends Truck {
    private double maxWeight,
            currentWeight;
    private Branch destination;
    private Hub home;
    static int availableTrucks = 0;
    private static int finished = 0;


    public StandardTruck(Hub hub) {
        super();
        this.maxWeight = (int) (10 + Math.random() * 90);
        System.out.println("Creating StandardTruck [truckID = " + truckID + ", available = " + available + "]");
        home = hub;
        destination = hub;
    }

    public StandardTruck(int maxWeight) {
        super();
        this.maxWeight = maxWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setDestination(Branch destination) {
        this.destination = destination;
    }

    @Override
    public void run() {
        while (!home.lastDelivered) {
            while (available) {
                synchronized (this) {
                    try {
                        availableTrucks++;
                        home.wakeUp();
                        this.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
            try {
                hubLoading();
                System.out.println("StandardTruck " + truckID + " loaded at the HUB");
                drive(100, "StandardTruck", destination);
                branchWork();
                timeLeft = (int) (Math.random() * 6) + 1;
                setDestination(home);
                drive(100, "StandardTruck", destination);
                while (!packages.isEmpty()) {
                    unloading(destination, packages.get(0));
                }
                available = true;
                home.startEngines();
            } catch (Exception e) {
                return;
            }
        }
        finishedTrucks();
    }

    private synchronized void branchWork() {
        while (!packages.isEmpty()) {
            unloading(destination, packages.get(0));
            destination.wakeUp();
        }
        System.out.println("StandardTruck " + truckID + " unloaded packages at " + destination);
        currentWeight = 0;
        for (int i = destination.listPackages.size() - 1; i >= 0; i--) {
            Package curPack = destination.listPackages.get(i);
            if (curPack.status == Status.BRANCH_STORAGE) {
                if (curPack instanceof SmallPackage) {
                    if (maxWeight > currentWeight + 1)
                        loading(destination, curPack, 1);
                } else if (maxWeight > currentWeight + ((StandardPackage) curPack).getWeight())
                    loading(destination, curPack, ((StandardPackage) curPack).getWeight());
            }
        }
        System.out.println("StandardTruck " + truckID + " loaded packages at " + destination);
        destination.wakeUp();
    }

    @Override
    public synchronized void collectPackage(Package p) {
        super.collectPackage(p);
        p.addTracking(this, p.status);
    }

    private synchronized void loading(Branch destination, Package pack, double weight) {
        destination.deliverPackage(pack);
        collectPackage(pack);
        currentWeight += weight;
    }

    private synchronized void unloading(Branch destination, Package aPackage) {
        deliverPackage(aPackage);
        destination.collectPackage(aPackage);
        destination.incAvailablePackages();
    }

    public synchronized void hubLoading() {
        currentWeight = 0;
        for (int i = home.listPackages.size() - 1; i >= 0; i--) {
            Package curPack = home.listPackages.get(i);
            if (curPack.destinationAddress.getZip() == destination.branchID && curPack.status == Status.HUB_STORAGE) {
                if (curPack instanceof SmallPackage) {
                    if (maxWeight > currentWeight + 1)
                        loading(home, curPack, 1);
                } else {
                    if (maxWeight > currentWeight + ((StandardPackage) curPack).getWeight())
                        loading(home, curPack, ((StandardPackage) curPack).getWeight());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "StandardTruck " + truckID;
    }

    @Override
    public void begin() {
        synchronized (this) {
            notifyAll();
        }
    }

    private synchronized void finishedTrucks() {
        finished++;
        if (finished == home.listTrucks.size()) {
            home.mainOffice.printReport();
        }
    }
}