package util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class XMLUtils {

    /**创建带根节点的XML文件
     * @param xmlFile 文件的路径
     * @param  rootName 根元素名称*/
    public static void createXML(File xmlFile, String rootName) {  //创建带有根节点XML文件
        Document document = DocumentHelper.createDocument();    // 创建Document对象
        Element root = document.addElement(rootName);   // 创建根节点
        saveToFile(xmlFile, document);  //保存到destination文件中
    }

    /**给根节点添加元素
     * @param xmlFile XML文件
     * @param subName 子元素名称
     * @param attributeName 子元素属性
     * @param attributeValue 子元素属性值*/
    public static void addOneRecord(File xmlFile, String subName, String attributeName, String attributeValue) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document dom = reader.read(xmlFile);
        Element root = dom.getRootElement();
        Element subEle = root.addElement(subName);
        subEle.addAttribute(attributeName, attributeValue);
        saveToFile(xmlFile, dom);
    }

    public static void addOneRecordByAttributeList(File xmlFile, String subName, List<String> attributeNameList, List<String> attributeValueList) throws Exception {
        if (attributeNameList.size() != attributeValueList.size()){
            throw new Exception("集合属性和属性值的大小不一致");
        }
        SAXReader reader = new SAXReader();
        Document dom = reader.read(xmlFile);
        Element root = dom.getRootElement();
        Element subEle = root.addElement(subName);
        for (int i = 0; i < attributeNameList.size(); i++) {
            subEle.addAttribute(attributeNameList.get(i), attributeValueList.get(i));
        }
        saveToFile(xmlFile,dom);
    }

    /**获取XML文件指定的子元素指定的属性对应的属性值
     * @param xmlFile XML文件
     * @param subName 子元素名称
     * @param attributeName 子元素属性*/
    public static List<String> getAllRecord(File xmlFile, String subName, String attributeName) throws DocumentException {
        List<String> list = new ArrayList<>();
        SAXReader reader = new SAXReader();
        Document dom = reader.read(xmlFile);
        Element root = dom.getRootElement();
        if (root == null){
            return list;
        }
        List<Element> elementList = root.elements(subName);
        if (elementList == null || elementList.size() == 0){
            return list;
        }

        for (Element element : elementList) {
            list.add(element.attributeValue(attributeName));
        }
        return list;
    }

    public static boolean isExist(File xmlFile, String subName, String attributeName,String candidate) throws DocumentException{
        List<String> list = getAllRecord(xmlFile,subName,attributeName);
        for (String string:list){
            if (candidate.equals(string)){
                return true;
            }
        }
        return false;
    }

    public static void addOneRecordToChoseFolder(File destination, String folder, String subName, String fileName)  {
        try {
            SAXReader reader = new SAXReader();
            Document dom = reader.read(destination);
            Element root = dom.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> choseFolderList = root.elements();
            for (Element choseFolder : choseFolderList) {
                if (choseFolder.attributeValue("path").equals(folder)) {
                    Element subEle = choseFolder.addElement(subName);
                    subEle.setText(fileName);
                }
            }
            saveToFile(destination, dom);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void removeOneRecord(File destination, String attributeName, String deleteValue)  {
        try {
            SAXReader reader = new SAXReader();
            Document dom = reader.read(destination);
            Element root = dom.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> list = root.elements();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).attributeValue(attributeName).equals(deleteValue)) {
                    list.get(i).detach();
                }
            }
            saveToFile(destination, dom);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //删除ChoseFolder元素下面的所有子元素
    public static void removeChoseFolderSubElements(File destination, String folder) {
        try {
            SAXReader reader = new SAXReader();
            Document dom = reader.read(destination);
            Element root = dom.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> list = root.elements();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).attributeValue("path").equals(folder)) {
                    @SuppressWarnings("unchecked")
                    List<Element> songElementList = list.get(i).elements();
                    for (Element songElement : songElementList) {
                        songElement.detach();
                    }
                }
            }
            saveToFile(destination, dom);
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public static List<String> getAllSong(File destination, String folder) {
        List<String> list = new ArrayList<>();
        try {
            SAXReader reader = new SAXReader();
            Document dom = reader.read(destination);
            Element root = dom.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> choseFolderList = root.elements();
            for (Element choseFolder : choseFolderList) {
                if (choseFolder.attributeValue("path").equals(folder)) {
                    @SuppressWarnings("unchecked")
                    List<Element> songElementList = choseFolder.elements();
                    for (Element songElement : songElementList) {
                        list.add(songElement.getText());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    //对xml文件进行修改后执行的保存函数
    private static void saveToFile(File destination, Document dom) {
        try {
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            outputFormat.setExpandEmptyElements(true);
            // 创建XMLWriter对象
            XMLWriter writer = new XMLWriter(new FileOutputStream(destination), outputFormat);
            // 设置不自动进行转义
            writer.setEscapeText(false);
            // 生成XML文件
            writer.write(dom);
            // 关闭XMLWriter对象
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
