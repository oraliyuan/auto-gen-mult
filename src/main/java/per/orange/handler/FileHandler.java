package per.orange.handler;

import per.orange.entity.MiniProcessUnit;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileHandler {

    private enum FileType{
        /*
        .screen 文件
         */
        SCRREN(1, ".screen"),
        /*
        .bm 文件
         */
        BM(2, ".bm");

        private int code;
        private String name;

        FileType(int code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    /**
     * 获取自定义配置配置
     */
    public void getConfigFile() {
        String configPath = System.getProperty("user.dir") + "/default.conf";
    }

    // 此处应分
    public Map<String, List<String>> getAllFilePath(String dirPath) {
        Map<String, List<String>> map = new HashMap<>(16);

        Path path = Paths.get(dirPath);

        // 读取配置文件需要对哪些文件进行替换
        map.put(FileType.SCRREN.name, new ArrayList<>());
        map.put(FileType.BM.name, new ArrayList<>());

        ergodicPath(path, map);

        return map;
    }

    private void ergodicPath(Path sourPath, Map<String, List<String>> container) {
        // TODO 优化为读取配置文件中 includFilePath 和 excludFilePath
        if (Files.isRegularFile(sourPath)) {
            String k = sourPath.getFileName().toString();
            int idx = k.lastIndexOf(".");
            if (idx < 0) idx = 0;
            String key = k.substring(idx);
            // TODO 此处需要优化
            if (container.containsKey(key)) {
                container.get(key).add(sourPath.getParent() + File.separator + k);
            }
            return;
        }

        if (Files.isDirectory(sourPath)) {
            try {
                Stream<Path> files = Files.list(sourPath);
                // foreach 循环：被循环的对象被锁定，不能对循环中的内容进行增删改操作
                files.forEach(file -> ergodicPath(file, container));
            } catch (IOException e) {
                throw new RuntimeException("遍历文件出错");
            }
        }
    }

    public static void main(String[] args) throws Exception{
        FileHandler fileHandler = new FileHandler();
        // 测试扫描文件
        Map<String,List<String>> map = fileHandler.getAllFilePath("C:/Users/liyuan/Desktop/source");
//        List<String> list = map.get(".screen");
//        list.forEach(System.out::println);

        // 测试备份文件
//        fileHandler.copyBackupFile(map, "C:/Users/liyuan/Desktop/bak");

        // 测试读取文件
        List<MiniProcessUnit> list1 = fileHandler.readEveryLine(map.get(".screen").get(0));
        list1.forEach(unit -> {
            System.out.println("Line: " + unit.getRownum());
            System.out.println("String: " + unit.getOldString());
            System.out.printf("Replace: \n");
//            List<String> ulist = unit.getChinaeseChars();
//            for (int i = 0; i < ulist.size(); i++) {
//                System.out.printf("\t" + ulist.get(i) + "\n");
//            }
            Map<String, String> map1 = unit.getTransMap();
            int ii = 0;
            for (Map.Entry<String, String> entry : map1.entrySet()) {
                if (ii++ % 2 == 0) {
                    map1.put(entry.getKey(), "我是长字符串我很长我很长我很长我很长我很长我很长我很长我很长我很长我很长");
                } else {
                    map1.put(entry.getKey(), "我是短字符串");
                }
                System.out.printf("\t" + entry.getKey() + " : " + entry.getValue() + "\n");
            }
        });
        // 测试写入文件
//        for (int i = 0; i < list1.size(); i++) {
//            if (i % 2 == 0) {
//                list1.get(i).setNewString("超级无敌长的字符串我很长我很长我很长我很长我很长我很长我很长我很长我很长对我不短\n");
//            } else {
//                list1.get(i).setNewString("替换后\n");
//            }
//        }
        fileHandler.overwriteCharsByIndex(list1);
    }

    /**
     * 读取文件中某一行字符串
     */
    public List<MiniProcessUnit>  readEveryLine(String path) {
        List<MiniProcessUnit> list = new ArrayList<>();
        RegexHandler regexHandler = new RegexHandler();
        MiniProcessUnit unit = null;
        boolean isScriptLine = true;
        String str = null;
        int idx = 0;
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(path));
            while((str = reader.readLine()) != null) {
                // <script> 行
                if (RegexHandler.match(s ->s.matches("<script>"), str)) {
                    isScriptLine = true;
                    continue;
                }
                // </script> 行
                if (RegexHandler.match(s ->s.matches("</script>"), str)) {
                    isScriptLine = false;
                    continue;
                }
                // 存在中文行
                idx++;
                if (RegexHandler.match(s ->s.matches(".*[\"']([\\u4e00-\\u9fa5:：？?！!。.]+)[\"'].*"), str)) {
                    unit = new MiniProcessUnit.Builder().addRownum(idx).addOldString(str).addIsScriptLine(isScriptLine).build();
                    regexHandler.findAll(unit);
                    list.add(unit);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("读取文件时错误");
        }

        return list;
    }

    /**
     * 将原来的代码替换为使用多语言的代码
     * todo 此处代码可以使用RandomAccessFile优化
     */
    public synchronized void overwriteCharsByIndex(List<MiniProcessUnit> list) throws IOException {
        String path = "C:/Users/liyuan/Desktop/source/test.txt";
        File file = new File(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        CharArrayWriter caw = new CharArrayWriter();
        String line = null, oldstr, newstr;
        int rownum = 1;
        MiniProcessUnit unit = null;

        while ((line = reader.readLine()) != null) {
            unit = isNeedModifyLine(rownum, list);
            if (unit != null) {
                for (Map.Entry<String, String> entry : unit.getTransMap().entrySet()) {
                    if (unit.isScriptLine()) {
                        newstr = "\"${l:" + entry.getValue() + "}\"";
                    } else {
                        newstr = "\"" + entry.getValue() + "\"";
                    }
                    line = line.replace(entry.getKey(), newstr);
                }
                unit.setNewString(line);
            }
            caw.write(line);
            caw.append(System.getProperty("line.separator"));
            rownum++;
        }
        reader.close();

        FileWriter fw = new FileWriter(file);
        caw.writeTo(fw);
        fw.close();
    }

    // todo 不严谨
    private MiniProcessUnit isNeedModifyLine(int rownum, List<MiniProcessUnit> list) {
        for (int i = 0; i < list.size(); i++) {
            if (rownum == list.get(i).getRownum()) {
                return list.get(i);
            }
        }
        return null;
    }

    /**
     * 创建新文件
     */
    public void createNewFile() {

    }

    /**
     * 备份现有文件
     */
    public void copyBackupFile(Map<String, List<String>> container, String bakPath) {
        int len;
        Path path = Paths.get(bakPath);
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException("创建备份目录时出错");
            }
        }
        String[] fileTypes = {".screen", ".bm"};
        List<String> filesPath = null;
        for (int i = 0; i < (len = fileTypes.length); i++) {
            filesPath = container.get(fileTypes[i]);

            filesPath.forEach(file -> {
                int idx = file.lastIndexOf(File.separator);
                if (idx < 0) idx = 0;
                String fileName = file.substring(idx);
                try {
                    Files.copy(Paths.get(file), Paths.get(bakPath + File.separator + fileName));
                } catch (IOException e) {
                    throw new RuntimeException("备份" + fileName + "文件时出错");
                }
            });
        }
    }
}
