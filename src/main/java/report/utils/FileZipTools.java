package report.utils;

import sun.nio.cs.ext.GBK;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class FileZipTools {

    //压缩，zipFileName为压缩的全路径文件名，targetFile为压缩的文件夹
    //举例：FileZipTools.compression("D:\\2.zip",file);，将file压缩为d盘下2.zip
    public static void compression(String zipFileName, File targetFile) throws IOException {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
            BufferedOutputStream bos = new BufferedOutputStream(out);
            zip(out, targetFile, targetFile.getName(), bos);
            bos.close();
            out.close();
    }

    public static void zip(ZipOutputStream zOut, File targetFile, String name, BufferedOutputStream bos) throws IOException {
        if (targetFile.isDirectory()) {
            File[] files = targetFile.listFiles();
            if (files.length == 0) {//空文件夹
                zOut.putNextEntry(new ZipEntry(name + "/"));
            }
            for (File f : files) {
                zip(zOut, f, name + "/" + f.getName(), bos);
            }
        } else {
            zOut.putNextEntry(new ZipEntry(name));
            InputStream in = new FileInputStream(targetFile);
            BufferedInputStream bis = new BufferedInputStream(in);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            bis.close();
        }
    }

    //解压，targetFileName为文件名，parent为存放到那个目录下面
    public static void deCompression(String targetFileName, String parent) throws IOException {

            ZipInputStream zIn = new ZipInputStream(new FileInputStream(targetFileName), Charset.forName("GBK"));
            ZipEntry entry = null;
            File file = null;
            while ((entry = zIn.getNextEntry()) != null && !entry.isDirectory()) {
                file = new File(parent, entry.getName());
                if (!file.exists()) {
                    new File(file.getParent()).mkdirs();
                }
                OutputStream out = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(out);
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = zIn.read(bytes)) != -1) {
                    bos.write(bytes, 0, len);
                }
                bos.close();
            }
            zIn.close();
    }
}


