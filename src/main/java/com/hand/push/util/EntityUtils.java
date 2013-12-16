package com.hand.push.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 9/26/13
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityUtils {
    //TODO 使用引用队列"ReferenceQueue"构建缓存

    private static final ConcurrentMap<Class,ConcurrentMap<Class,Field>> cachedClassFields = new ConcurrentHashMap<Class, ConcurrentMap<Class, Field>>();


    public static <T extends Annotation> Field getAnnotatedField(Class clazz, Class<T> annotation) {
        ConcurrentMap<Class,Field> fieldsMap = null;

        //判断此类型是否被解析过
        if (cachedClassFields.containsKey(clazz)){
            //此类型以前被解析过
            fieldsMap =   cachedClassFields.get(clazz);

        }

        if (fieldsMap==null){
            //未被解析，构造一个缓存对象
            fieldsMap = new ConcurrentHashMap<Class, Field>();
            cachedClassFields.put(clazz,fieldsMap);
        }

        if (fieldsMap.containsKey(annotation)){
            return fieldsMap.get(annotation);
        }


        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getAnnotation(annotation) != null) {

                fieldsMap.putIfAbsent(annotation, field);
                return field;
            }

        }

        throw new IllegalStateException("没有找到配置的逻辑主键字段，请确保在逻辑字段上加入 "+annotation.getClass() +" 注解");





    }

    /**
     * 根据注解，找到被注解的字段的名称
     *
     * @param clazz
     * @param annotation
     * @param <T>
     * @return
     */
    public static <T extends Annotation> String getFiledName(Class clazz, Class<T> annotation) {
        Field field = getAnnotatedField(clazz, annotation);

        return field.getName();
    }

    public static <T extends Annotation> Object getAnnotatedFieldValue(Object object, Class<T> annotation) {
        Field field = getAnnotatedField(object.getClass(), annotation);

        org.springframework.util.ReflectionUtils.makeAccessible(field);

        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("类型 " + object.getClass().getName() + " 属性 " + field.getName() + " 必须要声明get方法");
        }
    }




}
