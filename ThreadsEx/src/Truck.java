import java.util.ArrayList;

public abstract class Truck implements Runnable, Node<Truck> {
    protected static int truckIdCnt = 2000;
    protected int truckID,
            timeLeft;
    protected boolean available;
    protected ArrayList<Package> packages;

    public Truck() {
        truckID = truckIdCnt;
        truckIdCnt++;
        timeLeft = 0;
        available = true;
        packages = new ArrayList<Package>();
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    @Override
    public synchronized void collectPackage(Package p) {
        packages.add(p);
        p.statusChange();
        
    }

    @Override
    public synchronized void deliverPackage(Package p) {
        packages.remove(p);
        if (p.status == Status.DISTRIBUTION) {
            p.statusChange();
            p.addTracking(null, p.status);
        }
    }

    protected void drive(int time, String truckKind, Branch destination) {
        while (timeLeft != 0) {
            System.out.println(truckKind + " " + truckID + " is on it's way to " + destination + ", time to arrive: " + timeLeft);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                return;
            }
            timeLeft--;
        }
        System.out.println(truckKind + " " + truckID + " is arrived to " + destination);
    }

    protected void drive(int time) {
        while (timeLeft != 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                return;
            }
            timeLeft--;
        }
    }

    public abstract void begin();
}