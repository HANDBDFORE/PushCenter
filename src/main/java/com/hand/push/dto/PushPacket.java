package com.hand.push.dto;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 1:58 PM
 */
/*
{
  "app": {
    "key": "iiae7893493",
    "secret": "werdfwserw"
  },
  "data": [
    {
      "platform": "iOS",
      "token": "ikqwer394jskjfksjdf",
      "count": 3,
      "message": "hi"
    },
    {
      "platform": "Android",
      "token": "ikqwer394jskjfksjdf",
      "count": 3,
      "message": "hi"
    }
  ]
}
 */
public class PushPacket {
    private App app;
    private List<PushRequest> data;

    public PushPacket(App app, List<PushRequest> data) {
        this.app = app;
        this.data = data;
    }

    public PushPacket() {

    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public List<PushRequest> getData() {
        return data;
    }

    public void setData(List<PushRequest> data) {
        this.data = data;
    }

    public class PushRequest {
        private String platform;
        private String token;
        private int count;
        private String message;

        public PushRequest() {
        }

        public PushRequest(String platform, String token, int count, String message) {
            this.platform = platform;
            this.token = token;
            this.count = count;
            this.message = message;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    public class App {
        private String key;
        private String secret;

        public App() {
        }

        public App(String key, String secret) {
            this.key = key;
            this.secret = secret;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
