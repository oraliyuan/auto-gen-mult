package per.orange.trans;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.ws.util.StringUtils;

import java.util.Locale;

public class TranslateHandler {

    private final String APP_ID = "20190226000271292";
    private final String SECURITY_KEY = "wtvRQyLGl_eToWapNZgB";

    /**
     * 翻译字符串
     * @param s
     * @return
     */
    public String translate(String s) {
        if (s.isEmpty()) {
            return null;
        }
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        return api.getTransResult(s, "auto", "en");
    }

    // todo 读取配置
    private String upperAndjoinWord(String s) {
        return "PROMPT_" + s.toUpperCase(Locale.ENGLISH).replaceAll(" ", "_") + "_SUFFIX";
    }

    public static void main(String[] args) {
        TranslateHandler translateHandler = new TranslateHandler();
        String out = translateHandler.translate("蛇皮");
        System.out.println(out);
        JSONObject json = JSONObject.parseObject(out);
        JSONArray array = json.getJSONArray("trans_result");
        JSONObject jsonObject = (JSONObject) array.get(0);
        String word = (String) jsonObject.get("dst");
        System.out.println(translateHandler.upperAndjoinWord(word));
    }
}
