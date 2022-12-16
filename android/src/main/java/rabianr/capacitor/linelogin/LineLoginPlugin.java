package rabianr.capacitor.linelogin;

import androidx.activity.result.ActivityResult;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "LineLogin")
public class LineLoginPlugin extends Plugin {

    private LineLogin implementation;

    @Override
    public void load() {
        implementation = new LineLogin(getActivity().getApplicationContext());
    }

    @PluginMethod
    public void setup(PluginCall call) {
        String channelId = call.getString("channelId", "");

        if (channelId.length() == 0) {
            call.reject("Must provide a Channel Id");
            return;
        }

        JSObject options = new JSObject();
        options.put("channelId", channelId);
        implementation.setup(options);

        call.resolve();
    }

    @PluginMethod
    public void login(PluginCall call) {
        startActivityForResult(call, implementation.getLoginIntent(), "loginResult");
    }

    @ActivityCallback
    private void loginResult(PluginCall call, ActivityResult result) {
        if (call == null) {
            return;
        }

        JSObject ret = implementation.getLoginResultFromIntent(result.getData());

        returnPluginCall(call, ret);
    }

    @PluginMethod
    public void logout(PluginCall call) {
        JSObject ret = implementation.logout();

        returnPluginCall(call, ret);
    }

    @PluginMethod
    public void getAccessToken(PluginCall call) {
        JSObject ret = implementation.getAccessToken();

        returnPluginCall(call, ret);
    }

    @PluginMethod
    public void refreshAccessToken(PluginCall call) {
        JSObject ret = implementation.refreshAccessToken();

        returnPluginCall(call, ret);
    }

    @PluginMethod
    public void verifyAccessToken(PluginCall call) {
        JSObject ret = implementation.verifyAccessToken();

        returnPluginCall(call, ret);
    }

    private void returnPluginCall(PluginCall call, JSObject ret) {
        if (ret.getString("code") == "SUCCESS") {
            call.resolve(ret);
        } else {
            call.reject(ret.getString("message"), ret.getString("code"));
        }
    }
}
