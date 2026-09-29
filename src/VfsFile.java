public class VfsFile extends VfsNode {
    private final byte[] content;

    public VfsFile(String name, byte[] content) {
        super(name);
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }
}