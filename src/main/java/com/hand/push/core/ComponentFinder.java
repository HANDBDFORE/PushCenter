package com.hand.push.core;

import com.hand.push.core.annotation.Platform;
import com.hand.push.impl.AndroidPusher;
import com.hand.push.impl.iOSPusher;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/13/13
 * Time: 1:50 PM
 */
public class ComponentFinder {


    private static List<Class<Pusher>> pusherClasses;

    public ComponentFinder(List<Class<Pusher>> pusherClasses) {
        this.pusherClasses = pusherClasses;
    }

    private static Class findByName(String name) {

        for (Class<Pusher> pusherClass : pusherClasses) {
            System.out.println(pusherClass.getName());


            Platform annotation = pusherClass.getAnnotation(Platform.class);
            System.out.println(annotation.platformName());

            if (name.equals(annotation.platformName()))
                return pusherClass;

        }


        throw new PusherNotfoundException("需要的" + name + " 组件未找到");
    }

    public static Pusher getInstance(String name, JSONObject data) {
       Class clazz = findByName(name);
        try {
            Constructor constructor = clazz.getConstructor(JSONObject.class);
           return  (Pusher)constructor.newInstance(data);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException("未找到" + name);

    }


}
