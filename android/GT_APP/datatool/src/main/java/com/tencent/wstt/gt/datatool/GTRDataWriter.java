package com.tencent.wstt.gt.datatool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class GTRDataWriter {

    String dataFilePath = "";
    File dataFile;

    public GTRDataWriter(String dataFilePath) throws IOException {
        this.dataFilePath = dataFilePath;
        this.dataFile = new File(dataFilePath);
        if (dataFile.exists()) {
            dataFile.delete();
        }
        dataFile.getParentFile().mkdirs();
        dataFile.createNewFile();
    }

    void putData(String line) {
        try {

            dataFile.createNewFile();
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(dataFile, true), "utf-8"));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(line).append("\n");
                bufferedWriter.write(stringBuilder.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
