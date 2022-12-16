package rabianr.capacitor.linelogin;

import java.util.Arrays;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineAccessToken;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.api.LineApiClientBuilder;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.linecorp.linesdk.auth.LineAuthenticationParams;

public class LineLogin {

    private static final String RESPONSE_CODE_SUCCESS = "SUCCESS";
    private static final String RESPONSE_CODE_CANCEL = "CANCEL";
    private static final String RESPONSE_CODE_UNKNOWN_ERROR = "UNKNOWN_ERROR";

    private Context context;
    private String channelId;
    private LineApiClient lineApiClient;

    LineLogin(Context context) {
        this.context = context;
    }

    public void setup(JSObject options) {
        String channelId = options.getString("channelId");

        if (this.channelId != channelId) {
            this.channelId = channelId;
            lineApiClient = new LineApiClientBuilder(context, channelId).build();
        }
    }

    public Intent getLoginIntent() {
        Intent loginIntent = LineLoginApi.getLoginIntent(
            context,
            channelId,
            new LineAuthenticationParams.Builder()
                .scopes(Arrays.asList(Scope.PROFILE, Scope.OPENID_CONNECT, Scope.OC_EMAIL))
                .build());
        return loginIntent;
    }

    public JSObject getLoginResultFromIntent(Intent data) {
        JSObject ret = new JSObject();
        LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);

        switch (result.getResponseCode()) {
            case SUCCESS:
                JSObject retData = new JSObject();
                LineAccessToken lineAccessToken = result.getLineCredential().getAccessToken();

                retData.put("accessToken", lineAccessToken.getTokenString());
                retData.put("expiresAt", (int) (lineAccessToken.getEstimatedExpirationTimeMillis() / 1000));
                retData.put("email", result.getLineIdToken().getEmail());
                retData.put("userID", result.getLineProfile().getUserId());
                retData.put("displayName", result.getLineProfile().getDisplayName());

                if (result.getLineProfile().getPictureUrl() != null) {
                    retData.put("pictureUrl", result.getLineProfile().getPictureUrl().toString());
                }

                ret.put("code", RESPONSE_CODE_SUCCESS);
                ret.put("data", retData);

                break;
            case CANCEL:
                ret.put("code", RESPONSE_CODE_CANCEL);
                ret.put("message", "LINE Login Canceled by user.");

                break;
            default:
                ret.put("code", RESPONSE_CODE_UNKNOWN_ERROR);
                ret.put("message", result.getErrorData().toString());
        }

        return ret;
    }

    public JSObject logout() {
        JSObject ret = new JSObject();
        LineApiResponse response = lineApiClient.logout();
        lineApiClient = null;

        if (response.isSuccess()) {
            ret.put("code", RESPONSE_CODE_SUCCESS);
        } else {
            ret.put("code", RESPONSE_CODE_UNKNOWN_ERROR);
            ret.put("message", response.getErrorData().toString());
        }

        return ret;
    }

    public JSObject getAccessToken() {
        JSObject ret = new JSObject();
        LineApiResponse<LineAccessToken> response = lineApiClient.getCurrentAccessToken();
        LineAccessToken lineAccessToken = response.getResponseData();

        if (response.isSuccess()) {
            JSObject retData = new JSObject();
            retData.put("accessToken", lineAccessToken.getTokenString());
            retData.put("expiresAt", lineAccessToken.getEstimatedExpirationTimeMillis());
            ret.put("data", retData);
            ret.put("code", RESPONSE_CODE_SUCCESS);
        } else {
            ret.put("code", RESPONSE_CODE_UNKNOWN_ERROR);
            ret.put("message", response.getErrorData().toString());
        }

        return ret;
    }

    public JSObject refreshAccessToken() {
        JSObject ret = new JSObject();
        LineApiResponse<LineAccessToken> response = lineApiClient.refreshAccessToken();
        LineAccessToken lineAccessToken = response.getResponseData();

        if (response.isSuccess()) {
            JSObject retData = new JSObject();
            retData.put("accessToken", lineAccessToken.getTokenString());
            retData.put("expiresAt", lineAccessToken.getEstimatedExpirationTimeMillis());
            ret.put("data", retData);
            ret.put("code", RESPONSE_CODE_SUCCESS);
        } else {
            ret.put("code", RESPONSE_CODE_UNKNOWN_ERROR);
            ret.put("message", response.getErrorData().toString());
        }

        return ret;
    }

    public JSObject verifyAccessToken() {
        JSObject ret = new JSObject();
        LineApiResponse<LineCredential> response = lineApiClient.verifyToken();

        if (response.isSuccess()) {
            ret.put("code", RESPONSE_CODE_SUCCESS);
        } else {
            ret.put("code", RESPONSE_CODE_UNKNOWN_ERROR);
            ret.put("message", response.getErrorData().toString());
        }

        return ret;
    }
}
