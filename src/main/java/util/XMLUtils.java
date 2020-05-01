package util;

import javafx.scene.control.Label;
import model.GroupSong;
import model.LocalSong;
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
import pojo.Group;
import pojo.Song;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public static Element getRootElement(File xmlFile) {
        try {
            SAXReader reader = new SAXReader();
            Document dom = reader.read(xmlFile);
            return dom.getRootElement();
        }catch (DocumentException e){
            return null;
        }
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

    /**保存本地歌曲到歌单的函数操作
     * @param xmlFile 存储的xml文件
     * @param group 歌单对象
     * @param song 歌曲对象*/
    public static String addOneRecord(File xmlFile, Group group, Object song) throws DocumentException {
        Element root = getRootElement(xmlFile);
        //先判断当前歌单是否存在
        List<Element> groupsElement = root.elements("Group");
        Element groupElement = getGroupElement(groupsElement,group);
        if (groupElement == null){    //如果root元素下没有这个歌单元素,添加
            groupElement = root.addElement("Group");
            groupElement.addAttribute("name",group.getName());
        }
        if (song instanceof LocalSong){ //LocalSong部分
            LocalSong localSong = (LocalSong) song;
            //先判断当前歌单是否已经存在该歌曲
            List<Element> songsElement = groupElement.elements("song");
            for (int i = 0; i < songsElement.size(); i++) {
                if (songsElement.get(i).attributeValue("resourceURL").equals(localSong.getResource())){ //以resourceURL作为判断是否重复的依据
                    return "歌曲已存在";
                }
            }
            //然后在groupName元素下添加song元素
            Element songElement = groupElement.addElement("song");
            songElement.addAttribute("name",localSong.getName());
            songElement.addAttribute("singer",localSong.getSinger());
            songElement.addAttribute("album",localSong.getAlbum());
            songElement.addAttribute("totalTime",localSong.getTotalTime());
            songElement.addAttribute("resourceURL",localSong.getResource());
            songElement.addAttribute("addTime",String.valueOf(new Date().getTime()));
        }else if (song instanceof GroupSong){   //GroupSong部分
            GroupSong groupSong = (GroupSong) song;
            //先判断当前歌单是否已经存在该歌曲
            List<Element> songsElement = groupElement.elements("song");
            for (int i = 0; i < songsElement.size(); i++) {
                if (songsElement.get(i).attributeValue("resourceURL").equals(groupSong.getResourceURL())){ //以resourceURL作为判断是否重复的依据
                    return "歌曲已存在";
                }
            }
            //然后在groupName元素下添加song元素
            Element songElement = groupElement.addElement("song");
            songElement.addAttribute("name",groupSong.getName());
            songElement.addAttribute("singer",groupSong.getSinger());
            songElement.addAttribute("album",groupSong.getAlbum());
            songElement.addAttribute("totalTime",groupSong.getTotalTime());
            songElement.addAttribute("resourceURL",groupSong.getResourceURL());
            songElement.addAttribute("addTime",String.valueOf(new Date().getTime()));
        }
        //最后保存
        saveToFile(xmlFile,root.getDocument());
        return "添加成功";
    }

    public static Element getGroupElement(List<Element> groupsElement,Group group){
        if (groupsElement != null && group != null){
            for (int i = 0; i < groupsElement.size(); i++) {
                if (groupsElement.get(i).attributeValue("name").equals(group.getName())){
                    return groupsElement.get(i);
                }
            }
        }
        return null;
    }

    public static String removeOneRecord(File xmlFile, Group group, GroupSong groupSong) throws DocumentException {
        Element root = getRootElement(xmlFile);
        String message = "移除失败";
        List<Element> songList = getGroupElement(root.elements("Group"),group).elements("song");
        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).attributeValue("resourceURL").equals(groupSong.getResourceURL())){
                songList.get(i).detach();
                message = "移除成功";
                break;
            }
        }
        saveToFile(xmlFile,root.getDocument());
        return message;
    }

    /**获取xml文件歌单分组下所有的歌曲信息
     * @param xmlFile xml文件
     * @param group 分组对象信息*/
    public static List<GroupSong> getGroupSongs(File xmlFile,Group group) throws DocumentException {
        List<GroupSong> groupSongs = new ArrayList<>();
        Element root = getRootElement(xmlFile);
        Element groupElement = getGroupElement(root.elements("Group"),group);
        if (groupElement != null){
            List<Element> songList = groupElement.elements("song");
            for (int i = 0; i < songList.size(); i++) {
                GroupSong groupSong = new GroupSong();
                groupSong.setName(songList.get(i).attributeValue("name"));
                groupSong.setSinger(songList.get(i).attributeValue("singer"));
                groupSong.setAlbum(songList.get(i).attributeValue("album"));
                groupSong.setTotalTime(songList.get(i).attributeValue("totalTime"));
                groupSong.setAddTime(new Date(Long.parseLong(songList.get(i).attributeValue("addTime"))));
                groupSong.setResourceURL(songList.get(i).attributeValue("resourceURL"));
                groupSongs.add(groupSong);
            }
            return groupSongs;
        }
        return null;
    }

    public static void removeGroup(File groupsSongFile, Group group) throws DocumentException {
        Element root = getRootElement(groupsSongFile);
        if (root != null){  //如果根元素不为null
            Element groupElement = getGroupElement(root.elements("Group"),group);
            if (groupElement != null){
                groupElement.detach();
                saveToFile(groupsSongFile,root.getDocument());
            }
        }
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
