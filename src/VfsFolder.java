import java.util.ArrayList;
import java.util.List;

public class VfsFolder extends VfsNode {
    private final List<VfsNode> children;

    public VfsFolder(String name) {
        super(name);
        this.children = new ArrayList<>();
    }

    public List<VfsNode> getChildren() {
        return children;
    }

    public void addChild(VfsNode child) {
        children.add(child);
    }
}