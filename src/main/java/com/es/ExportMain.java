package com.es;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExportMain {
    private static Logger logger = LoggerFactory.getLogger(ExportMain.class);

    private static final int SIZE = 1000;

    public static void main(String[] args) {

        long totalSize = ESRestClient.totalSize;

        int pageSize = (int) ((totalSize+SIZE-1)/SIZE);

        JSONArray docs;
        JSONObject json;
        List<String[]> arrList;String[] arr;
        List<String> headers = new ArrayList<String>();
        for(int i=1;i<=pageSize;i++ ) {
            docs = ESRestClient.getDocs((i - 1)*SIZE,SIZE);
            if(i==1 && !docs.isEmpty()) {
                json = docs.optJSONObject(0);
                Iterator it = json.keys();
                while(it.hasNext()) {
                    headers.add(it.next().toString());
                }
            }

            arrList = new ArrayList<String[]>();
            for(int j=0;j<docs.size();j++) {
                json = docs.optJSONObject(j);
                arr = new String[headers.size()];

                for(int k=0;k<headers.size();k++) {
                    arr[k] = json.optString(headers.get(k),"");
                }

                arrList.add(arr);
            }

            FileOutputStream fos = null;
            try {

                File file = new File(System.getProperty("user.dir")+ File.separator + ("data_"+i+".xls"));


                fos = new FileOutputStream(file);
                ExcelUtil.exportEventExcel("data", headers.toArray(new String[headers.size()]), arrList, fos);

                fos.flush();
            } catch (Exception e) {
                logger.error("Export excel file error.", e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        logger.error("Close io error.", e);
                    }
                }
            }

        }

        System.exit(1);

    }



}
