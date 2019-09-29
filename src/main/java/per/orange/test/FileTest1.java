package per.orange.test;

public class FileTest1 {

    public static void main(String[] args) {

    }
// 之前代码的备份
//    public synchronized void overwriteCharsByIndex(List<MiniProcessUnit> list) throws IOException {
//        String path = "C:/Users/liyuan/Desktop/source/test.txt";
//        RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
//        int count = 1, pointerPosi = 0;
//        long length = randomAccessFile.length();
//        if (length == 0L) {
//            throw new RuntimeException("文件内容为空");
//        } else {
//            int posi = 0;
//            while(posi < length) {
//                posi++;
//                randomAccessFile.seek(posi);
//                if (randomAccessFile.readByte() == '\n') {
//                    String line = new String(randomAccessFile.readLine().getBytes("ISO8859-1"), "UTF-8");
//                    System.out.println(line);
//                    count++;
//                    MiniProcessUnit t = isNeedModifyLine(count, list);
//                    if (t != null) {
////                        randomAccessFile.seek(pointerPosi);
//                        byte[] bytes = line.replace(t.getOldString(), t.getNewString()).getBytes(Charset.forName("UTF-8"));
////                        randomAccessFile.write(line.replace(t.getOldString(), t.getNewString()).getBytes(Charset.forName("UTF-8")), 0, bytes.length);
//                        randomAccessFile.writeChars(line.replace(t.getOldString(), t.getNewString()));
//                        return;
//                    }
//                    pointerPosi = posi + 1;
//                }
//            }
//        }
//    }
}
