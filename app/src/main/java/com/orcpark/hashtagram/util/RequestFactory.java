package com.orcpark.hashtagram.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.volley.Request;
import com.orcpark.hashtagram.io.request.JsonObjectRequester;
import com.orcpark.hashtagram.io.request.RequestQueueManager;
import com.orcpark.hashtagram.io.request.Requester;
import com.orcpark.hashtagram.io.request.ResponseListener;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by tony.park on 14. 11. 4..
 */
public class RequestFactory {

    public static void getNewsfeed(Context context, View progressBar, ResponseListener listener) {
        String accessToken = PreferenceUtils.getAccessToken(context);
        if (TextUtils.isEmpty(accessToken)) {
            Log.e("jsp", "has not access_token");
            return;
        }

        String url = "https://api.instagram.com/v1/users/self/feed"
                        + getEncodedParams(ParamFactory.getAccessTokenParams(accessToken));
        request(context, Request.Method.GET, url, null, progressBar, listener);
    }

    public static void getNextItems(Context context, String url, View progressBar, ResponseListener listener) {
        String accessToken = PreferenceUtils.getAccessToken(context);
        if (TextUtils.isEmpty(accessToken)) {
            Log.e("jsp", "has not access_token");
            return;
        }

        request(context, Request.Method.GET, url, null, progressBar, listener);
    }

    public static void getHashTag(Context context, String hashTag,
                                        View progressBar, ResponseListener listener) {
        String accessToken = PreferenceUtils.getAccessToken(context);
        if (TextUtils.isEmpty(accessToken)) {
            Log.e("jsp", "has not access_token");
            return;
        }

        String url = "https://api.instagram.com/v1/tags/"+ getEncodeString(hashTag) +
                "/media/recent" + getEncodedParams(ParamFactory.getAccessTokenParams(accessToken));

        request(context, Request.Method.GET, url, null, progressBar, listener);
    }

    protected static void request(Context context, int method, String url, HashMap<String, String> params,
                                  View progressBar, ResponseListener listener) {

        boolean get = method == Request.Method.GET;
        if (get) {
            url += getEncodedParams(params);
        }

        JsonObjectRequester requester = new JsonObjectRequester(method, url, listener);
        if (!get && params != null && params.size() >= 0) {
            requester.setParams(params);
        }

        requester.setProgressView(progressBar);

        RequestQueueManager.getInstance(context).getRequestQueue().add(requester);
    }

    /**
     * Get 방식 연동시 BasicNameValuePair 를 이용해 만들어진 HashMap 을
     * Url 파라미터로 만들어 줌(ex. ?access_token=123456&device=Galaxy)
     *
     * @param params BasicNameValuePair 를 이용해서 만들어진 Map
     * @return String
     */
    public static String getEncodedParams(HashMap<String, String> params) {
        if (params == null || params.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            int max = entries.size();
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(URLEncoder.encode(entry.getKey(), Requester.CHARSET));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), Requester.CHARSET));
                if (i != max - 1) {
                    sb.append("?");
                }
                i++;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String result = TextUtils.isEmpty(sb.toString()) ? "" : "?" + sb.toString();
        return result;
    }

    public static String getEncodeString(String string) {
        String encodeString = null;

        try {
            encodeString = URLEncoder.encode(string, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encodeString;
    }

    /**
     * Parameter 만들어주는 class
     */
    public static final class ParamFactory {

        public static HashMap<String, String> getAccessTokenParams(String accessToken) {
            return getParams(
                    new BasicNameValuePair("access_token", accessToken));
        }

        public static HashMap<String, String> getParams(BasicNameValuePair... pairs) {
            HashMap<String, String> params = new HashMap<String, String>();
            for (BasicNameValuePair pair : pairs) {
                params.put(pair.getName(), pair.getValue());
            }
            return params;
        }
    }

}