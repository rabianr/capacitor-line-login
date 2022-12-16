import Foundation
import LineSDK

@objc public class LineLogin: NSObject {
    private var plugin: LineLoginPlugin?
    private var channelId: String = ""

    struct ResponseCode {
        static let Success = "SUCCESS"
        static let Cancel = "CANCEL"
        static let UnknownError = "UNKNOWN_ERROR"
    }

    init(_ plugin: LineLoginPlugin) {
        super.init()
        self.plugin = plugin
    }

    @objc public func setup(_ options: [String: Any]) {
        let channelId = options["channelId"] as! String
        if self.channelId != channelId {
            self.channelId = channelId
            LoginManager.shared.setup(channelID: channelId, universalLinkURL: nil)
        }
    }

    @objc public func login(completion: @escaping (_ result: [String: Any]) -> Void) {
        LoginManager.shared.login(permissions: [.profile, .openID, .email], in: self.plugin?.bridge?.viewController) {
            result in
            var ret: [String: Any] = [:]

            switch result {
            case .success(let loginResult):
                var data: [String: Any?] = [:]
                data["accessToken"] = loginResult.accessToken.value
                data["expiresAt"] = loginResult.accessToken.expiresAt.timeIntervalSince1970
                data["email"] = loginResult.accessToken.IDToken?.payload.email

                if let profile = loginResult.userProfile {
                    data["userID"] = profile.userID
                    data["displayName"] = profile.displayName

                    if let pictureUrl = profile.pictureURL {
                        data["pictureUrl"] = pictureUrl
                    }
                }

                ret["code"] = ResponseCode.Success
                ret["data"] = data
            case .failure(let error):
                if error.isUserCancelled {
                    ret["code"] = ResponseCode.Cancel
                } else {
                    ret["code"] = ResponseCode.UnknownError
                }
                ret["message"] = error.localizedDescription
            }

            completion(ret)
        }
    }

    @objc public func logout(completion: @escaping (_ result: [String: Any]) -> Void) {
        LoginManager.shared.logout { result in
            switch result {
            case .success:
                completion([ "code": ResponseCode.Success ])
            case .failure(let error):
                completion([ "code": ResponseCode.UnknownError, "message": error.localizedDescription ])
            }
        }
    }

    @objc public func getAccessToken(completion: @escaping (_ result: [String: Any]) -> Void) {
        if let accessToken = AccessTokenStore.shared.current {
            completion([
                "code": ResponseCode.Success,
                "data": [
                    "accessToken": accessToken.value,
                    "expiresAt": accessToken.expiresAt.timeIntervalSince1970,
                ],
            ])
        } else {
            completion([ "code": ResponseCode.UnknownError, "message": "Unauthenticated" ])
        }
    }

    @objc public func refreshAccessToken(completion: @escaping (_ result: [String: Any]) -> Void) {
        API.Auth.refreshAccessToken { result in
            switch result {
            case .success(let accessToken):
                completion([
                    "code": ResponseCode.Success,
                    "data": [
                        "accessToken": accessToken.value,
                        "expiresAt": accessToken.expiresAt.timeIntervalSince1970,
                    ],
                ])
            case .failure(let error):
                completion([ "code": ResponseCode.UnknownError, "message": error.localizedDescription ])
            }
        }
    }

    @objc public func verifyAccessToken(completion: @escaping (_ result: [String: Any]) -> Void) {
        API.Auth.verifyAccessToken { result in
            switch result {
            case .success:
                completion([ "code": ResponseCode.Success ])
            case .failure(let error):
                completion([ "code": ResponseCode.UnknownError, "message": error.localizedDescription ])
            }
        }
    }
}
