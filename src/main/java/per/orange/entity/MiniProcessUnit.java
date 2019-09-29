package per.orange.entity;

import javafx.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * 最小处理单元：对应一个文件中的一行
 */
public class MiniProcessUnit {
    /**
     * 处理行号
     */
    private int rownum;

    private String oldString;

    private String newString;

    private boolean isScriptLine;

    private List<String> chinaeseChars;

    private Map<String, String> transMap;

    private MiniProcessUnit(Builder builder) {
        this.rownum = builder.rownum;
        this.oldString = builder.oldString;
        this.newString = builder.newString;
        this.isScriptLine = builder.isScriptLine;
    }

    public void setChinaeseChars(List<String> chinaeseChars) {
        this.chinaeseChars = chinaeseChars;
    }

    public void setNewString(String newString) {
        this.newString = newString;
    }

    public void setTransMap(Map<String, String> transMap) {
        this.transMap = transMap;
    }

    public int getRownum() {
        return rownum;
    }

    public String getOldString() {
        return oldString;
    }

    public String getNewString() {
        return newString;
    }

    public boolean isScriptLine() {
        return isScriptLine;
    }

    public List<String> getChinaeseChars() {
        return chinaeseChars;
    }

    public Map<String, String> getTransMap() {
        return transMap;
    }

    public static class Builder {
        private int rownum;
        private String oldString;
        private String newString;
        private boolean isScriptLine;

        public Builder addRownum(int rownum) {
            this.rownum = rownum;
            return this;
        }

        public Builder addOldString(String oldString) {
            this.oldString = oldString;
            return this;
        }

        public Builder addNewString(String newString) {
            this.newString = newString;
            return this;
        }

        public Builder addIsScriptLine(boolean isScriptLine) {
            this.isScriptLine = isScriptLine;
            return this;
        }

        public MiniProcessUnit build() {
            return new MiniProcessUnit(this);
        }
    }
}
