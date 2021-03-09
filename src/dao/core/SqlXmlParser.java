package dao.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SqlXmlParser {

    public static Map<String, String> readXmlByDOM(InputStream inputStream){
        Map<String, String> sqls = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(inputStream);

            Element root = dom.getDocumentElement();
            /**【查找所有Sql节点】**/
            NodeList items = root.getElementsByTagName("sql");
            for (int i = 0; i < items.getLength(); i++) {
                Element sqlNode = (Element) items.item(i);
                /**【获取Sql节点的id属性】**/

                String id = sqlNode.getAttribute("id");
                String statment = sqlNode.getTextContent();
                sqls.put(id, statment);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return sqls;
    }
}
