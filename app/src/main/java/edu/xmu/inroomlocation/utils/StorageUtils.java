package edu.xmu.inroomlocation.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;


public class StorageUtils {
    public static void saveTxt(String s, String filename) {
        File root = Environment.getExternalStorageDirectory();
        File myDir = new File(root.getAbsolutePath() + "/inroomlocation");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        File newFile = new File(myDir, filename);
        try {
            FileOutputStream fs = new FileOutputStream(newFile);
            PrintWriter pw = new PrintWriter(fs);
            pw.print(s);
            pw.flush();
            pw.close();
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveImg(ByteArrayOutputStream bitmap, String filename) {
        File root = Environment.getExternalStorageDirectory();
        File myDir = new File(root.getAbsolutePath() + "/inroomlocation_pictemp");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File dest = new File(myDir, filename);
        try {
            dest.createNewFile();
            FileOutputStream fos = new FileOutputStream(dest);
            fos.write(bitmap.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getSaveImageFile(String filename) throws IOException {
        File root = Environment.getExternalStorageDirectory();
        File myDir = new File(root.getAbsolutePath() + "/inroomlocation_pictemp");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File dest = new File(myDir, filename);
//        File dest = File.createTempFile(filename, ".jpg", myDir);
        dest.createNewFile();
        lastImageFileName = dest;
        return dest;
    }

    public static File lastImageFileName = null;

    public static byte[] readFileToBytes(String filename) {

        return null;

    }

    public static byte[] readPicToBytes(String imageName) {
        File root = Environment.getExternalStorageDirectory();
        File myDir = new File(root.getAbsolutePath() + "/inroomlocation_pictemp");
        File dest = new File(myDir, imageName);

//        new ByteArrayOutputStream(new FileInputStream(dest));


        try {
            return Files.readAllBytes(Paths.get(dest.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
