package per.orange.entity;

import javax.swing.filechooser.FileSystemView;

public class AutoGenConfig {

    private String prefix = "PROMPT.";

    private boolean enableSuffix = false;

    private String suffix = ".PROMPT";

    private String bakFilePath = FileSystemView.getFileSystemView() .getHomeDirectory().getAbsolutePath();

    private String repeatStrategy = "1248";

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isEnableSuffix() {
        return enableSuffix;
    }

    public void setEnableSuffix(boolean enableSuffix) {
        this.enableSuffix = enableSuffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getBakFilePath() {
        return bakFilePath;
    }

    public void setBakFilePath(String bakFilePath) {
        this.bakFilePath = bakFilePath;
    }

    public String getRepeatStrategy() {
        return repeatStrategy;
    }

    public void setRepeatStrategy(String repeatStrategy) {
        this.repeatStrategy = repeatStrategy;
    }
}
