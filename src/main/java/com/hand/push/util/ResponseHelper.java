package com.hand.push.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 9/12/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResponseHelper {

    private Map<String, Object> result;

    public ResponseHelper() {
        result = new HashMap<String, Object>();
    }


    public static ResponseHelper success() {
        return success(null, null);
    }

    public static ResponseHelper success(String message) {
        return success(message, null);
    }

    public static ResponseHelper success(String message, Map<String, Object> otherHeaders) {
        return result("ok", message, otherHeaders, new HashMap<String, Object>());
    }

    public static ResponseHelper failure(String message) {
        return result("failure", message, null, null);
    }


    public static ResponseHelper requireLogin(String message) {
        return result("login required", message, null, null);
    }

    /**
     * @param code
     * @param message
     * @param body
     * @return
     */
    public static ResponseHelper result(String code, String message, Map<String, Object> otherHeaders, Object body) {
        ResponseHelper helper = new ResponseHelper();

        Map<String, Object> jsonHead = (otherHeaders == null) ? (new HashMap<String, Object>())
                : (new HashMap<String, Object>(otherHeaders));

        jsonHead.put("code", code);
        jsonHead.put("message", message);

        Map<String, Object> jsonResult = new HashMap<String, Object>();
        jsonResult.put("head", jsonHead);

        jsonResult.put("body", body);

        helper.result = jsonResult;

        return helper;
    }

    public ResponseHelper addBody(String name, Object value) {
        Object bodySet = result.get("body");
        if (!(bodySet instanceof Map))
            throw new RuntimeException("返回数据构造过程出错，body不是map类型");

        Map<String, Object> bodyMap = (Map<String, Object>) bodySet;
        bodyMap.put(name, value);

        return this;
    }

    public ResponseHelper setBody(Object value) {
        result.put("body", value);

        return this;
    }

    public Map<String, Object> result() {
        return result;
    }
}
