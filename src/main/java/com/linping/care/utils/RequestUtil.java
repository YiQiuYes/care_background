package com.linping.care.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RequestUtil {
    /**
     * CloseableHttpClient
     *
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient createDefault() {
        return HttpClientBuilder.create().build();
    }

    /**
     * get请求
     *
     * @param url 请求地址
     * @return 返回结果
     */
    public static String get(String url, Map<String, String> params) {
        CloseableHttpClient client;
        try {
            client = RequestUtil.createDefault();
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    uriBuilder.setParameter(key, params.get(key));
                }
            }
            HttpGet request = new HttpGet(uriBuilder.build());
            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * key-value格式的参数
     *
     * @param url    请求地址
     * @param params 参数
     * @return 返回结果
     */
    public static String post(String url, Map<String, Object> params) {
        BufferedReader in = null;
        try {
            HttpClient client = RequestUtil.createDefault();
            HttpPost request = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<>();
            for (String key : params.keySet()) {
                String value = String.valueOf(params.get(key));
                nvps.add(new BasicNameValuePair(key, value));
            }
            request.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                String NL = System.lineSeparator();
                while ((line = in.readLine()) != null) {
                    sb.append(line).append(NL);
                }
                in.close();
                return sb.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 请求json格式的参数
     *
     * @param url    请求地址
     * @param params 参数
     * @return 返回结果
     */
    public static String post(String url, String params) {
        CloseableHttpClient httpclient = RequestUtil.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(params, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity);
            }
            response.close();
            httpclient.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
