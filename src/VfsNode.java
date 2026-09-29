public abstract class VfsNode {
    private final String name;

    public VfsNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}