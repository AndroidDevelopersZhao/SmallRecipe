package cn.com.xxutils.util;
/**
 * .   如果你认为你败了，那你就一败涂地；
 * .   如果你认为你不敢，那你就会退缩；
 * .   如果你想赢但是认为你不能；
 * .   那么毫无疑问你就会失利；
 * .   如果你认为你输了，你就输了；
 * .   我们发现成功是从一个人的意志开始的；
 * .   成功是一种心态。
 * .   生活之战中，
 * .   胜利并不属于那些更强和更快的人，
 * .   胜利者终究是认为自己能行的人。
 * .
 * .   If you think you are beaten,you are;
 * .   If you think you dare not,you don't;
 * .   If you can to win but think you can't;
 * .   It's almost a cinch you won't.
 * .   If you think you'll lose,you're lost;
 * .   For out of the world we find Success begins with a fellow's will;
 * .   It's all in a state of mind.
 * .   Life's battles don't always go to the stronger and faster man,But sooner or later the man who wins Is the man who thinks he can.
 * .
 * .   You can you do.  No can no bb.
 * .
 */


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 项目名称： SoEasy
 * 创建日期： 2015/12/25  14:11
 * 项目作者： 赵文贇
 * 项目包名： com.shanghai.soeasylib.util
 */
public class XXHttpClient {

    private String url = null;
    private AsyncHttpClient client = null;
    private RequestParams params = null;
    private XXHttpResponseListener xxHttpResponseListener = null;

    /**
     *
     */
    public XXHttpClient(String url, boolean isSSL, XXHttpResponseListener xxHttpResponseListener) {
        this.url = url;
        client = isSSL ? new AsyncHttpClient(true, 80, 443) : new AsyncHttpClient();
        this.xxHttpResponseListener = xxHttpResponseListener;
        params = new RequestParams();
    }

    public void put(String key, Object v) {
        params.put(key, v);
    }

    public void doGet(int timeout) {
        client.setTimeout(timeout);
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                xxHttpResponseListener.onSuccess(i, bytes);
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                xxHttpResponseListener.onError(i, throwable);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                xxHttpResponseListener.onProgress(bytesWritten, totalSize);
            }
        });
    }

    public void doPost(int timeout) {
        client.setTimeout(timeout);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                if (xxHttpResponseListener!=null){
                    xxHttpResponseListener.onSuccess(i, bytes);
                }
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                xxHttpResponseListener.onError(i, throwable);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                xxHttpResponseListener.onProgress(bytesWritten, totalSize);
            }
        });
    }

    public RequestParams getAllParams() {
        return this.params;
    }


    public interface XXHttpResponseListener {
        void onSuccess(int i, byte[] bytes);

        void onError(int i, Throwable throwable);

        void onProgress(long bytesWritten, long totalSize);
    }
}
