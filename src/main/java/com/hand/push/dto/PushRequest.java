package com.hand.push.dto;

import java.util.List;

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
public class PushRequest {
    private PushApp app;
    private List<PushEntry> data;


    public PushApp getApp() {
        return app;
    }

    public void setApp(PushApp app) {
        this.app = app;
    }

    public List<PushEntry> getData() {
        return data;
    }

    public void setData(List<PushEntry> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PushRequest{" +
                "app=" + app +
                ", data=" + data +
                '}';
    }





}
