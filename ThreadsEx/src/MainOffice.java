import java.util.ArrayList;
import java.util.Random;

public class MainOffice {
    Thread[] myTreads;
    Hub hub;
    ArrayList<Package> packages = new ArrayList<>();
    int packagesAmount, expectedDeliveries, threadCnt = 1, deliveriesMade = 0;
    Random rand = new Random();


    public MainOffice(int branches, int trucksForBranch, int packages) {
        int totalThreads = (branches + 1) * trucksForBranch + branches + 1;
        myTreads = new Thread[totalThreads];
        hub = new Hub();
        for (int i = 0; i < trucksForBranch; i++) {
            hub.listTrucks.add(new StandardTruck(hub));
        }
        for (int i = 0; i < branches; i++) {
            System.out.println();
            hub.getBranches().add(new Branch(hub));
            for (int j = 0; j < trucksForBranch; j++) {
                Branch br = hub.getBranches().get(i);
                br.listTrucks.add(new Van(br));
            }
        }
        System.out.println();
        packagesAmount = packages;
        expectedDeliveries = packages;
    }

    public synchronized void printReport() {
        System.out.println("\n");
        for (int i = 0; i < packages.size(); i++) {
            packages.get(i).printTracking(packages.get(i).toString());
            System.out.println();
        }
    }

    void addPackage() {
        while (packagesAmount != 0) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Package p;
            char packSize = rand.nextBoolean() ? 's' : 'r';
            Address sender = newAd(),
                    destination = newAd();
            if (packSize == 's') {
                p = new SmallPackage(sender, destination, rand.nextBoolean());
            } else {
                p = new StandardPackage(sender, destination, (rand.nextDouble() * 9) + 1);
            }
            Branch sendBr = hub.getBranches().get(sender.getZip() - 1);
            sendBr.listPackages.add(p);
            sendBr.incAvailablePackages();
            p.status = Status.CREATION;
            p.addTracking(null, p.status);
            packages.add(p);
            System.out.println("Creating " + p);
            sendBr.wakeUp();
            packagesAmount--;
        }
    }

    public Address newAd() {
        int add = rand.nextInt(900000) + 100000,
                zip = rand.nextInt(hub.getBranches().size()) + 1;
        return new Address(zip, add);
    }

    public void play() {
        System.out.println("\n========================== START ==========================");
        myTreads[0] = new Thread(hub);
        myTreads[0].start();
        for (int i = 0; i < hub.listTrucks.size(); i++) {
            Thread temp = new Thread(hub.listTrucks.get(i));
            myTreads[threadCnt] = temp;
            temp.start();
            threadCnt++;
        }
        for (int i = 0; i < hub.getBranches().size(); i++) {
            Thread temp = new Thread(hub.getBranches().get(i));
            myTreads[threadCnt] = temp;
            threadCnt++;
            temp.start();
            for (int j = 0; j < hub.getBranches().get(i).listTrucks.size(); j++) {
                Thread tempT = new Thread(hub.getBranches().get(i).listTrucks.get(j));
                myTreads[threadCnt] = tempT;
                tempT.start();
                threadCnt++;
            }
        }
        hub.startEngines();
        hub.setMainOffice(this);
        addPackage();
    }

    public void stopThreads() throws InterruptedException {
        for (int i = 0; i < myTreads.length; i++) {
            myTreads[i].interrupt();
        }
    }
}
