package kr.hyosang.webclient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by hyosang on 2016. 10. 26..
 */
public class HsWebClient {
    private String mRequestMethod = "GET";
    private String mDefaultResponseCharset = "UTF-8";

    public void setRequestMethod(String method) {
        mRequestMethod = method;
    }
    public void setDefaultResponseCharset(String charset) {
        mDefaultResponseCharset = charset;
    }

    public String getPage(String url, Map<String, String> param) throws IOException {
        StringBuffer result = new StringBuffer();
        boolean isPost = "POST".equals(mRequestMethod);

        try {
            String paramStr = getParamAsUrlString(param);

            if(!isPost) {
                if(url.contains("?")) {
                    if(url.endsWith("&")) {
                        url = url + paramStr;
                    }else {
                        url = url + "&" + paramStr;
                    }
                }else {
                    url = url + "?" + paramStr;
                }
            }

            URL urlObj = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

            conn.setRequestMethod(mRequestMethod);

            if(isPost) {
                conn.setDoOutput(true);
            }

            conn.setDoInput(true);
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.addRequestProperty("Cache-Control", "no-cache");

            if(isPost) {
                OutputStream os = conn.getOutputStream();
                os.write(paramStr.getBytes("US-ASCII"));
                os.flush();
            }else {
                conn.connect();
            }

            InputStreamReader reader = new InputStreamReader(conn.getInputStream(), Charset.forName(mDefaultResponseCharset));

            char [] buf = new char[1024];
            int readBytes;

            while((readBytes = reader.read(buf)) > 0) {
                result.append(buf, 0, readBytes);
            }

            conn.disconnect();
        }catch(IOException e) {
            throw e;
        }

        return result.toString();
    }

    private String getParamAsUrlString(Map<String, String> param) {
        StringBuffer paramStr = new StringBuffer();

        try {
            for (Map.Entry<String, String> p : param.entrySet()) {
                paramStr.append(p.getKey()).append("=").append(URLEncoder.encode(p.getValue(), "UTF-8")).append("&");
            }
        }catch(UnsupportedEncodingException e) {
            //can not occur.
        }

        return paramStr.toString();
    }
}
