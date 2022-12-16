import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(LineLoginPlugin)
public class LineLoginPlugin: CAPPlugin {
    private var implementation: LineLogin?

    override public func load() {
        self.implementation = LineLogin(self)
    }

    @objc func setup(_ call: CAPPluginCall) {
        let channelId = call.getString("channelId") ?? ""

        if channelId.isEmpty {
            call.reject("Must provide a Channel Id")
            return
        }

        implementation?.setup([ "channelId": channelId ])

        call.resolve()
    }

    @objc func login(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.implementation?.login() {result in
                self.returnPluginCall(call, result)
            }
        }
    }

    @objc func logout(_ call: CAPPluginCall) {
        self.implementation?.logout() {result in
            self.returnPluginCall(call, result)
        }
    }

    @objc func getAccessToken(_ call: CAPPluginCall) {
        self.implementation?.getAccessToken() {result in
            self.returnPluginCall(call, result)
        }
    }

    @objc func refreshAccessToken(_ call: CAPPluginCall) {
        self.implementation?.refreshAccessToken() {result in
            self.returnPluginCall(call, result)
        }
    }

    @objc func verifyAccessToken(_ call: CAPPluginCall) {
        self.implementation?.verifyAccessToken() {result in
            self.returnPluginCall(call, result)
        }
    }

    func returnPluginCall(_ call: CAPPluginCall, _ result: [String: Any]) {
        if result["code"] as! String == "SUCCESS" {
            call.resolve(result)
        } else {
            call.reject(result["message"] as! String, result["code"] as? String)
        }
    }
}
