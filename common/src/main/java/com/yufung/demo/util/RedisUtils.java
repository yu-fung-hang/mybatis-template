package com.yufung.demo.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http工具
 * Created by kingapex on 2022/3/13.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2022/3/13
 */
public final class RedisUtils {

    public static final int HTTP_CONN_TIMEOUT = 100000;
    public static final int HTTP_SOCKET_TIMEOUT = 100000;

    private static String getContent(HttpURLConnection urlConn, String encoding) {
        try {
            String responseContent = null;
            InputStream in = urlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, encoding));
            String tempLine = rd.readLine();
            StringBuffer tempStr = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            while (tempLine != null) {
                tempStr.append(tempLine);
                tempStr.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = tempStr.toString();
            rd.close();
            in.close();
            return responseContent;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param link
     * @param encoding
     * @return
     */
    public static String doGet(String link, String encoding, int connectTimeout, int readTimeout) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(link);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            // conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedInputStream in = new BufferedInputStream(
                    conn.getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for (int i = 0; (i = in.read(buf)) > 0;) {
                out.write(buf, 0, i);
            }
            out.flush();
            String s = new String(out.toByteArray(), encoding);
            return s;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
    }

    /**
     * UTF-8编码
     *
     * @param link
     * @return
     */
    public static String doGet(String link) {
        return doGet(link, "UTF-8", HTTP_CONN_TIMEOUT, HTTP_SOCKET_TIMEOUT);
    }


}