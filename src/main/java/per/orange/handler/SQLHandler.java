package per.orange.handler;

import java.sql.*;

public class SQLHandler {

    private static Connection CONN = null;

    private static String DRIVER = "oracle.jdbc.driver.OracleDriver";

    private static String URL = "jdbc:oracle:thin:@//192.168.4.18:1521/template_new";

    private static String USERNAME = "srm";

    private static String PASSWORD = "srm";

    static {

    }

    private static synchronized Connection getConn() {
        if (CONN == null) {
            try {
                Class.forName(DRIVER);
                CONN = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("连接oracle 出错");
            }
        }
        return CONN;
    }

    public String queryPromptMessageExist(String message) {
        String messageCode = "";

        Connection connection = getConn();
        PreparedStatement stmt;
        ResultSet rs;

        String sql = "SELECT prompt_code " +
                    "  FROM sys_prompts " +
                    " WHERE language = 'ZHS' " +
                    "       AND description = ? " +
                    " GROUP BY prompt_code " +
                    " ORDER BY length(prompt_code) ASC ";

        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, message);
            rs = stmt.executeQuery();
            while(rs.next()) {
                messageCode = rs.getString("prompt_code");
                break;
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL 执行错误");
        }
        return messageCode;
    }

    public static void main(String[] args) {
        SQLHandler sqlHandler = new SQLHandler();
        String result = sqlHandler.queryPromptMessageExist("订单");
        System.out.println(result);

        String result1 = sqlHandler.queryPromptMessageExist("蛇皮棒棒糖");
        if (result1.isEmpty()) {
            System.out.println("没有");
        } else {
            System.out.println(result1);
        }
    }
}
