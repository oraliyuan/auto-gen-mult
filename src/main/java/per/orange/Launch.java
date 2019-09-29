package per.orange;

import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import per.orange.entity.AutoGenConfig;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Launch {

    /**
     * 流水线步骤：
     * 1. 读取配置，覆盖默认值，以供后面使用（流水线中的功能应该是可配置的）
     * 2. 扫面路径下的文件装载到容器中去
     * 3. 备份扫描到的文件
     * 4. 遍历每个扫描到的文件，正则配置其中需要替换为多语言的地方，将这些地方记录
     * 5. 将记录的地方写excel 中
     * 6. 替换源文件中需要替换的
     * 7. 根据记录生成脚本，防止到规定目录下
     * @param args
     */
    public static void main(String[] args) {
//        File desktopDir = FileSystemView.getFileSystemView() .getHomeDirectory();
//        String desktopPath = desktopDir.getAbsolutePath();
//        System.out.println(desktopPath);

        Yaml yaml = new Yaml();
        InputStream inputStream;
        try {
            inputStream = Files.newInputStream(Paths.get("D:/work/keepStudyJava/auto-gen-mult/src/main/resources/config.yaml"));
        } catch (IOException e) {
            throw new RuntimeException("创建配置文件流时出错");
        }
        Map<String, Object> map = yaml.loadAs(inputStream, Map.class);

        AutoGenConfig autoGenConfig = new AutoGenConfig();
        if (map.containsKey("bak") && ((LinkedHashMap) map.get("bak")).containsValue("path")) {
            Object o = ((LinkedHashMap) map.get("bak")).get("path");
            System.out.println(o.toString());
        }

        List<String> list = getAllConfigKeyPath();
        Pair<String, String> pair;
        for (int i = 0; i < list.size(); i++) {
            String keyPath = list.get(i);

            pair = readByKeyPath(keyPath, map);
            if (equalsAnyOne(keyPath)) {
                String[] strs = pair.getValue().split(",");
                System.out.println(StringUtils.join(strs, "_"));
            } else {
                System.out.println(pair.getValue());
            }
        }
    }

    private static Pair<String, String> readByKeyPath(String keyPath, Map<String, Object> map) {
        int index = keyPath.indexOf(".");
        if (index == -1) {
            if (map.containsKey(keyPath)) {
                return new Pair<>(keyPath, (String) map.get(keyPath));
            }
            return null;
        }
        String currentKey = keyPath.substring(0, index);
        String spareKey = keyPath.substring(index+1);
        if (map.containsKey(currentKey)) {
            Object o = map.get(currentKey);
            if (o instanceof LinkedHashMap) {
                return readByKeyPath(spareKey, (LinkedHashMap<String, Object>) o);
            }
        }

        // todo
        throw new RuntimeException("没找到对应键值");
    }

    private static List<String> getAllConfigKeyPath() {
        return Arrays.asList("bak.path", "mark.prefix", "mark.suffix", "trans.requestUrl", "trans.transTo");
    }

    private static boolean equalsAnyOne(String s) {
        List<String> list = Arrays.asList("trans.transTo");
        for (String t : list) {
            if (t.equals(s)) return true;
        }
        return false;
    }
}
