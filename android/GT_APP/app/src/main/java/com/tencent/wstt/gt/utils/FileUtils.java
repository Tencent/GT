package com.tencent.wstt.gt.utils;

import com.tencent.wstt.gt.GTConfig;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by p_guilfu on 2017/11/25.
 */

public class FileUtils {

    private static final int BUFFER_LEN = 8192;

    public static File[] checkFileIsExit() throws Exception {
        File file = new File(GTConfig.gtrDirPath);
        //文件夹是否存在
        if (!file.exists() && !file.isDirectory()) {
            System.out.println("//不存在");
            throw new Exception("File not exist");
        } else {
            System.out.println("//目录存在");
            File[] fileArray = file.listFiles();
            if (fileArray != null) {
                return fileArray;
//                for(int i=0;i<lists.length;i++)
//                {
//                    list(lists[i]);//是目录就递归进入目录内再进行判断
//                }
            }
        }
        return null;
    }


    public static void list(File file) {
        if (file.isDirectory())//判断file是否是目录
        {
            File[] lists = file.listFiles();
            if (lists != null) {
                for (int i = 0; i < lists.length; i++) {
                    list(lists[i]);//是目录就递归进入目录内再进行判断
                }
            }
        }
        System.out.println(file);//file不是目录，就输出它的路径名，这是递归的出口
    }

    public static void copy(String src, String des) throws Exception {
        File file1 = new File(src);
        File[] fs = file1.listFiles();
        File file2 = new File(des);
        if (file2.exists()) {
            FileUtil.deleteFile(file2);
        }
        if (!file2.exists()) {
            file2.mkdirs();
        }
        if (fs!=null&&fs.length>0){
            for (File f : fs) {
                if (f.isFile()) {
                    fileCopy(f.getPath(), des + "/" + f.getName()); //调用文件拷贝的方法
                } else if (f.isDirectory()) {
                    copy(f.getPath(), des + "/" + f.getName());
                }
            }
        }else{
            throw new Exception("is file");
        }
    }

    /**
     * 文件拷贝的方法
     */
    private static void fileCopy(String src, String des) {
        BufferedReader br = null;
        PrintStream ps = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
            ps = new PrintStream(new FileOutputStream(des));
            String s = null;
            while ((s = br.readLine()) != null) {
                ps.println(s);
                ps.flush();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (ps != null) ps.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除root目录下所有文件夹和文件
     * @param root
     */
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) {
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    /**
     * 压缩文件
     *
     * @param resFilePath 待压缩文件路径
     * @param zipFilePath 压缩文件路径
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO 错误时抛出
     */
    public static boolean zipFile(final String resFilePath, final String zipFilePath) throws IOException {
        return zipFile(resFilePath, zipFilePath, null);
    }

    /**
     * 压缩文件
     *
     * @param resFilePath 待压缩文件路径
     * @param zipFilePath 压缩文件路径
     * @param comment     压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO 错误时抛出
     */
    public static boolean zipFile(final String resFilePath, final String zipFilePath, final String comment) throws IOException {
        return zipFile(getFileByPath(resFilePath), getFileByPath(zipFilePath), comment);
    }

    /**
     * 压缩文件
     *
     * @param resFile 待压缩文件
     * @param zipFile 压缩文件
     * @param comment 压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO 错误时抛出
     */
    public static boolean zipFile(final File resFile, final File zipFile, final String comment) throws IOException {
        if (resFile == null || zipFile == null) return false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            zos.setLevel(4);
            return zipFile(resFile, "", zos, comment);
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param resFile  待压缩文件
     * @param rootPath 相对于压缩文件的路径
     * @param zos      压缩文件输出流
     * @param comment  压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO 错误时抛出
     */
    private static boolean zipFile(final File resFile, String rootPath, final ZipOutputStream zos, final String comment) throws IOException {
        rootPath = rootPath + (isSpace(rootPath) ? "" : File.separator) + resFile.getName();
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            if (fileList == null || fileList.length <= 0) {
                ZipEntry entry = new ZipEntry(rootPath + '/');
                if (!isSpace(comment)) entry.setComment(comment);
                zos.putNextEntry(entry);
                zos.closeEntry();
            } else {
                for (File file : fileList) {
                    // 如果递归返回 false 则返回 false
                    if (!zipFile(file, rootPath, zos, comment)) return false;
                }
            }
        } else {
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(resFile));
                ZipEntry entry = new ZipEntry(rootPath);
                if (!isSpace(comment)) entry.setComment(comment);
                zos.putNextEntry(entry);
                byte buffer[] = new byte[BUFFER_LEN];
                int len;
                while ((len = is.read(buffer, 0, BUFFER_LEN)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            } finally {
                is.close();
            }
        }
        return true;
    }

    private static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
