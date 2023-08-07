import java.util.ArrayList;

public class Hub extends Branch {
    public boolean allHome = false;
    private ArrayList<Branch> branches;
    MainOffice mainOffice;
    int deliveredCnt = 0;
    boolean lastDelivered = false;
    int i = 0;

    public Hub() {
        super(null);
        branches = new ArrayList<>();
    }

    @Override
    public void run() {
        int nextTruck = 0, nextBranch = 0;
        StandardTruck readyT = (StandardTruck) listTrucks.get(0);

        while (!lastDelivered) {
            if (availableTrucks == 0) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
            while (availableTrucks != 0) {
                if (listTrucks.get(nextTruck).available) {
                    readyT = (StandardTruck) listTrucks.get(nextTruck);
                    nextTruck = (nextTruck + 1) % listTrucks.size();
                    break;
                }
                nextTruck = (nextTruck + 1) % listTrucks.size();
            }
            readyT.setDestination(branches.get(nextBranch % branches.size()));
            readyT.timeLeft = (int) (Math.random() * 10) + 1;
            readyT.available = false;
            availableTrucks--;
            nextBranch++;
            readyT.begin();
        }
    }

    public void setMainOffice(MainOffice mainOffice) {
        this.mainOffice = mainOffice;
    }

    public void startEngines() {
        synchronized (this) {
            this.notify();
        }
    }

    public ArrayList<Branch> getBranches() {
        return branches;
    }
}
