package per.orange.handler;

import com.sun.org.apache.xerces.internal.xs.StringList;
import javafx.util.Pair;
import per.orange.entity.MiniProcessUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHandler {
    /**
     * 正则含义：匹配所有连续的中文字符串
     */
    private final Pattern htmlPattern = Pattern.compile("[\"']([\\u4e00-\\u9fa5:：？?！!。.]+)[\"']");
    private final Pattern scriptPattern = Pattern.compile("[\"']([\\u4e00-\\u9fa5]+)[\"']");

    /**
     * 找出字符串 s 中所有连续的中文字符串
     * @param unit
     * @return
     * 存在问题一种需要检测是 prompt=""
     * 另一种是 Aurora.showMessage('xxx','xxx')
     * 他们需要替换的字符是不同
     * 解决方：1 使用不同正则匹配不通替换位置
     * 2 使用行号分隔表示当前行在js 还是 screen代码中
     */
    public void findAll(MiniProcessUnit unit) {
//        List<String> result = new ArrayList<String>();
        Map<String, String> map = new HashMap<>();
        String temp = null;
        String s = unit.getOldString();
        Matcher matcher = htmlPattern.matcher(s);
        while(matcher.find()) {
            temp = matcher.group();
            if (temp != null && !"".equals(temp)) {
                map.put(matcher.group(), "");
            }
//            result.add(matcher.group());
        }
//        unit.setChinaeseChars(result);
        unit.setTransMap(map);
    }

    /**
     * 是否可以通过匹配正则
     * @param predicate
     * @param line
     * @return
     */
    public static boolean match(Predicate<String> predicate, String line) {
        return predicate.test(line);
    }

    public static void main(String[] args) {
        boolean f = RegexHandler.match(s -> s.matches(".*\\d+.*"), "fds<1234>fasdkl");
        System.out.println(f);
        System.out.println("fds<1234>fasdkl".matches(".*\\d+.*"));
    }
}
