public class Tracking {
    Node node;
    Status status;

    public Tracking(Node node, Status status) {
        this.node = node;
        this.status = status;
    }

    public Tracking(Status status) {
        this.node = null;
        this.status = status;
    }

    @Override
    public String toString() {
        if (node != null)
            return node.toString() + ", status = " + status.toString();
        return "Customer, status = " + status.toString();
    }
}
