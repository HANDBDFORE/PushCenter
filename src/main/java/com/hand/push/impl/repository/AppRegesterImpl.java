package com.hand.push.impl.repository;

import com.hand.push.core.ComponentFinder;
import com.hand.push.core.Pusher;
import com.hand.push.core.service.AppRegester;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 2:13 PM
 */
@Repository
public class AppRegesterImpl implements AppRegester {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ComponentFinder finder;

    @Override
    public List<Pusher> load(String appName, String appSecret) throws IllegalArgumentException {

        List<Pusher> result = new ArrayList<Pusher>();

        try {
            String configJson = readConfigFromFile("classpath:push-resources/DevicePlatforms");

            JSONArray apps = new JSONArray(configJson);
            for (int i = 0; i < apps.length(); i++) {
               JSONObject appObject = apps.getJSONObject(i);
               if (appObject.getString("id").equals(appName) && appObject.getString("pwd").equals(appSecret)){
                   //找到此信息

                   JSONArray platforms = appObject.getJSONArray("platforms");
                   for (int j = 0; j < platforms.length(); j++) {
                       JSONObject platform = platforms.getJSONObject(j);

                       String platformName = platform.getString("name");

                        result.add(ComponentFinder.getInstance(platformName,platform));

                   }

               }

            }

            System.out.println(configJson);
            System.out.println(result);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return result;
    }

    private String readConfigFromFile(String path) throws IOException {
        Resource configResource = resourceLoader.getResource( path);

        BufferedReader input = new BufferedReader (new FileReader(configResource.getFile()));
        StringBuilder buffer = new StringBuilder();
        String text;

        while((text = input.readLine()) != null)
            buffer.append(text.trim());



        return   buffer.toString();

    }

    /*
[
    {
        "id": "aerwr",
        "pwd": "afasdfasdr",
        "name": "ERSS企业信息订阅",
        "platforms": [
            {
                "name": "Android",
                "appid": "Ytk0ePKK6k65cRd8BGRN17",
                "appKey": "tYaDvmhEbj8VTgQYZaEJP4",
                "appSecret": "xFrLUHKBEsAbkxlGKRsTu6",
                "api": "http://sdk.open.api.igexin.com/apiex.htm"
            },
            {
                "name": "iOS",
                "secret": "tYaDvmhEbj8VTgQYZaEJP4",
                "p12FilPath": "/home/emerson/dev",
                "api": "http://sdk.open.api.igexin.com/apiex.htm"
            }
        ]
    }
]
     */




}
