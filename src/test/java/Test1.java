import report.utils.FileZipTools;
import report.utils.HttpTools;

import java.io.File;
import java.io.IOException;

public class Test1 {
    public static void main(String[] args) throws IOException {
        File file = new File("D:\\1");
        FileZipTools.compression("D:\\2.zip",file);
        FileZipTools.deCompression("D:\\2.zip","E:/2");
    }
}
