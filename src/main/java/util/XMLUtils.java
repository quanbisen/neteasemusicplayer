package util;

import model.PlayListSong;
import model.RecentSong;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultAttribute;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class XMLUtils {

    /**创建带根节点的XML文件
     * @param xmlFile 文件的路径
     * @param  rootName 根元素名称*/
    public static void createXML(File xmlFile, String rootName) {  //创建带有根节点XML文件
        Element root = DocumentHelper.createDocument().addElement(rootName);   // 创建根节点
        saveToFile(xmlFile, root.getDocument());  //保存到destination文件中
    }

    /**获取xml文件的根元素
     * @param xmlFile
     * @return Element*/
    public static Element getRootElement(File xmlFile) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document dom = reader.read(xmlFile);
        return dom.getRootElement();
    }

    /**给根节点添加元素
     * @param xmlFile XML文件
     * @param subName 子元素名称
     * @param attributeName 子元素属性
     * @param attributeValue 子元素属性值*/
    public static void addOneRecord(File xmlFile, String subName, String attributeName, String attributeValue) throws DocumentException {
        Element root = getRootElement(xmlFile);
        Element subEle = root.addElement(subName);
        subEle.addAttribute(attributeName, attributeValue);
        saveToFile(xmlFile, root.getDocument());
    }

    /**获取XML文件指定的子元素指定的属性对应的属性值
     * @param xmlFile XML文件
     * @param subName 子元素名称
     * @param attributeName 子元素属性*/
    public static List<String> getAllRecord(File xmlFile, String subName, String attributeName) throws DocumentException {
        List<String> list = null;
        Element root = getRootElement(xmlFile);
        if (root == null){
            return null;
        }
        List<Element> elementList = root.elements(subName);
        if (elementList == null || elementList.size() == 0){
            return null;
        }
        if (list == null){
            list = new ArrayList<>();
        }
        for (Element element : elementList) {
            list.add(element.attributeValue(attributeName));
        }
        return list;
    }

    /**给根节点添加元素
     * @param xmlFile XML文件
     * @param subName 子元素名称
     * @param attributeNameList 子元素属性集合
     * @param attributeValueList 子元素属性值集合*/
    public static void addOneRecord(File xmlFile, String subName, List<String> attributeNameList, List<String> attributeValueList) throws Exception {
        if (attributeNameList.size() != attributeValueList.size()){
            throw new Exception("集合属性和属性值的大小不一致");
        }
        Element root = getRootElement(xmlFile);
        Element subEle = root.addElement(subName);
        for (int i = 0; i < attributeNameList.size(); i++) {
            subEle.addAttribute(attributeNameList.get(i), attributeValueList.get(i));
        }
        saveToFile(xmlFile,root.getDocument());
    }

    /**获取XML文件的下所有最近播放歌曲
     * @param xmlFile XML文件
     * @param subName 子元素名称
     * @return List<RecentSong> 最近播放歌曲模型集合
     **/
    public static List<RecentSong> getRecentPlaySongs(File xmlFile, String subName) throws DocumentException {
        List<RecentSong> recentSongs = new ArrayList<>();
        Element root = getRootElement(xmlFile);
        List<Element> elementList = root.elements(subName);
        elementList.forEach(element -> {
            List<DefaultAttribute> defaultAttributeList = element.attributes();
            List<String> list = new ArrayList<>();
            defaultAttributeList.forEach(defaultAttribute -> {
                list.add(defaultAttribute.getText());
            });
            recentSongs.add(new RecentSong(list.get(0),list.get(1),list.get(2),list.get(3),list.get(4)));
        });
        return recentSongs;
    }

    /**删除存储最近播放歌曲文件中的一条最近播放歌曲记录的函数
     * @param xmlFile 存储文件
     * @param playListSong 最近播放列表的歌曲对象*/
    public static void removeOneRecord(File xmlFile, PlayListSong playListSong) throws DocumentException {
        Element root = getRootElement(xmlFile);
        List<Element> list = root.elements();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).attributeValue("resource").equals(playListSong.getResource())){
                list.get(i).detach();
                break;
            }
        }
        saveToFile(xmlFile, root.getDocument());

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

    public static void removeOneRecord(File destination, String attributeName, String deleteValue) throws DocumentException {
        Element root = getRootElement(destination);
        List<Element> list = root.elements();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).attributeValue(attributeName).equals(deleteValue)) {
                list.get(i).detach();
                break;
            }
        }
        saveToFile(destination, root.getDocument());
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
