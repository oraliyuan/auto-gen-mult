package per.orange.test;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileTest {

    public static void main(String[] args) throws Exception {
        replaceFile();
    }

    private static void replaceFile() throws Exception {
        String s1 = "提示";
        String s2 = "超级无敌长的字符串我很长我很长我很长我很长我很长我很长我很长我很长";

        String path = "C:/Users/liyuan/Desktop/source/test.txt";
        File file = new File(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
//        BufferedReader reader = Files.newBufferedReader(Paths.get(path));
        CharArrayWriter caw = new CharArrayWriter();

        String line = null;

        while ((line=reader.readLine()) != null) {
            line = line.replaceAll(s1, s2);
            caw.write(line);
            caw.append(System.getProperty("line.separator"));
        }

        reader.close();
        FileWriter fw = new FileWriter(file);
        caw.writeTo(fw);
        fw.close();
    }
}
