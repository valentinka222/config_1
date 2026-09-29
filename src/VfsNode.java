public abstract class VfsNode {
    private final String name;
    private String owner;

    public VfsNode(String name) {
        this.name = name;
        this.owner = "user";
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}