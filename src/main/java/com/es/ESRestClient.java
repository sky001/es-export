package com.es;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;


public class ESRestClient {
    private static Logger logger = LoggerFactory.getLogger(ESRestClient.class);
    private static final String METHOD_POST = "POST";

    private static String endpoint, query, orderField, orderType;
    public static Integer totalSize;

    private static RestClient restClient;

    static {

        Properties properties = loadProperties();
        endpoint = properties.getProperty("elasticsearch.index");
        query = properties.getProperty("elasticsearch.query");
        orderField = properties.getProperty("elasticsearch.order.field");
        orderType = properties.getProperty("elasticsearch.order.type","desc");
        totalSize = Integer.parseInt(properties.getProperty("elasticsearch.total.size"));


        int port = Integer.parseInt(properties.getProperty("elasticsearch.port","9200"));
        String addresses = properties.getProperty("elasticsearch.ip");
        String account = properties.getProperty("elasticsearch.account");

        String[] addressArray = addresses.split(",");
        HttpHost[] httpHosts = new HttpHost[addressArray.length];
        for (int i = 0; i < addressArray.length; i++) {
            httpHosts[i] = new HttpHost(addressArray[i], port, "http");
        }

        if (StringUtils.isBlank(account)) {

            restClient = RestClient.builder(httpHosts).build();
        } else {
            Header header = new BasicHeader("Authorization", "Basic " + new String(Base64.encodeBase64(account.getBytes())));
            restClient = RestClient.builder(httpHosts).setDefaultHeaders(new Header[]{header}).build();
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        InputStream in=null;
        try {
            in = new FileInputStream(new File(System.getProperty("user.dir")+ File.separator+"application.properties"));
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(in!=null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    /**
     * 查询ES
     *
     * @param from        分页开始条数
     * @param size        每页数量
     * @return
     */
    public static JSONArray getDocs(Integer from, Integer size) {

        JSONObject json = new JSONObject();
        json.put("query", query);

        //分页
        json.put("from", from);
        json.put("size", size);

        //排序
        JSONArray sortArr = new JSONArray();
        JSONObject sortJson = new JSONObject();
        JSONObject orderJson = new JSONObject();
        orderJson.put("order", orderType);
        sortJson.put(orderField, orderJson);
        sortArr.add(sortJson);
        json.put("sort", sortArr);

        logger.info("ES Query:" + json);
        HttpEntity entity = new NStringEntity(json.toString(), ContentType.APPLICATION_JSON);
        Response response;
        String responseStr;
        JSONObject responseJson, sourceJson;
        JSONArray hits, docs = new JSONArray();
        try {
            response = restClient.performRequest(METHOD_POST, endpoint.toLowerCase()+"/_search", Collections.<String, String>emptyMap(), entity);
            responseStr = EntityUtils.toString(response.getEntity());

            responseJson = JSONObject.fromObject(responseStr);

            int total = responseJson.optJSONObject("hits").optInt("total");
            if (total == 0) {
                return docs;
            }
            hits = responseJson.optJSONObject("hits").optJSONArray("hits");
            for (int i = 0; i < hits.size(); i++) {
                sourceJson = hits.optJSONObject(i).optJSONObject("_source");
                docs.add(sourceJson);
            }

        } catch (IOException e) {
            logger.error("ES response error", e);
        }

        return docs;

    }




    /**
     * 查询数量
     *
     * @return
     */
    public static long getCount() {
        JSONObject json = new JSONObject();
        json.put("query", query);

        HttpEntity entity = new NStringEntity(json.toString(), ContentType.APPLICATION_JSON);
        Response response;
        String responseStr;
        JSONObject responseJson;
        long total = 0;
        try {
            response = restClient.performRequest(METHOD_POST, endpoint.toLowerCase() + "/_count", Collections.<String, String>emptyMap(), entity);
            responseStr = EntityUtils.toString(response.getEntity());
            logger.info("ES getCount response:" + responseStr);

            responseJson = JSONObject.fromObject(responseStr);

            total = responseJson.optLong("count");

        } catch (IOException e) {
            logger.error("ES getCount response error", e);
        }

        return total;

    }


}
