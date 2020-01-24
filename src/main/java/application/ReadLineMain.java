package application;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Count my code line.
 * @author super lollipop
 * @date 20-1-24
 */
public class ReadLineMain {

    private static int countLine = 0;
    private static List<File> fileList = new ArrayList<>();
    private static File root = new File("/media/ubuntu/Documents/IntelliJProject/neteasemusicplayer/src");
    public static void main(String[] args) throws IOException {
        getFile(root);
        for (File file : fileList){
            System.out.println(file.getPath());
            readLine(file);
        }
        System.out.println("readLineSum:" + countLine);
    }

    private static void readLine(File file) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        while (bufferedReader.readLine()!=null){
            countLine ++;
        }
        System.out.println(countLine);
    }

    private static void getFile(File targetFile){
        File[] files = targetFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory() || pathname.getPath().endsWith(".java") || pathname.getPath().endsWith(".fxml") || pathname.getPath().endsWith(".css") || pathname.getPath().endsWith(".xml")){
                    return true;
                }else {
                    return false;
                }
            }
        });
        for (File file : files){
            if (file.isDirectory()){
                getFile(file);
            }else {
                fileList.add(file);
            }
        }

    }
}
