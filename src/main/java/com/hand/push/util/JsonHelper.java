package com.hand.push.util;


import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 9/12/13
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsonHelper {

    private static ObjectMapper mapper = new ObjectMapper();
    private static JsonFactory factory = new JsonFactory();

    /**
     * @param jsonResult
     * @return
     */
    public static String genJson(Object jsonResult) {
        StringWriter sw = new StringWriter();
        JsonGenerator gen = null;
        try {
            gen = factory.createJsonGenerator(sw);
            mapper.writeValue(gen, jsonResult);
        } catch (IOException e) {
            e.printStackTrace();
            return "";

        } finally {
            try {
                sw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (gen != null) {
                try {
                    gen.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sw.toString();
    }

    /**
     * @param <T>
     * @return
     * @throws java.io.IOException
     */
    public static <T> T stringToJson(String jsonString, Class<T> clazz) {
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> stringToCollection(String jsonString, Class<T> objectClazz) {
        System.out.println(jsonString);

        try {
            jsonString = jsonString.trim();

            if (jsonString.charAt(0) == '[') {
                JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, objectClazz);
                return mapper.readValue(jsonString, type);
            }

            else if (jsonString.charAt(0) == '{')
                return Arrays.asList(JsonHelper.stringToJson(jsonString, objectClazz));

            else
                throw new IllegalArgumentException("数据格式不正确");


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("无法解析json," + e.getMessage());
        }

    }

}