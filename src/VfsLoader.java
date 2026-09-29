import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Base64;

public class VfsLoader {
    /**
     * Загружает VFS из XML файла и строит дерево объектов в памяти
     * @param path путь к файлу
     * @return корневая папка построенного дерева
     * @throws Exception если файл не найден или некорректный файл
     */
    public static VfsFolder load(String path) throws Exception{
        File xmlFile = new File(path);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);

        return (VfsFolder) parseElement(document.getDocumentElement());
    }

    /**
     * Преобразует один XML элемент в узел дерева VFS
     * @param element XML элемент, описывающий файл или папку
     * @return созданный узел VfsFile(файл) или VfsFolder(папка)
     */
    private static VfsNode parseElement(Element element) {
        String name = element.getAttribute("name");

        if (element.getTagName().equals("file")) {
            byte[] content = Base64.getDecoder().decode(element.getTextContent());
            return new VfsFile(name, content);
        }

        VfsFolder folder = new VfsFolder(name);
        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                folder.addChild(parseElement((Element) node));
            }
        }
        return folder;
    }
}
