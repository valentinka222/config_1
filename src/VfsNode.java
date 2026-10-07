import java.time.LocalDateTime;

public abstract class VfsNode {
    private final String name;
    private String owner;
    private final String group;
    private final LocalDateTime modified;

    public VfsNode(String name) {
        this.name = name;
        this.owner = "user";
        this.group = "users";
        this.modified = LocalDateTime.now();
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

    public String getGroup() {
        return group;
    }

    public LocalDateTime getModified() {
        return modified;
    }
}