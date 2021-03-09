package utils;

import android.content.ContentValues;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtil {

    /**
     * 对象的属性转为Map，命名从驼峰转为大写下划线
     * @param o
     * @return
     */
    public static Map<String, Object> object2Map(Object o) {
        Map<String, Object> map = new HashMap<String, Object>();
        Method[] ms = o.getClass().getMethods();
        for (Method m : ms) {
            if (m.getName().startsWith("get") && ! m.getName().equals("getClass") ) {
                String columnName = m.getName().replaceAll("[A-Z]", "_$0").substring(4).toUpperCase();
                try {
                    m.setAccessible(true);
                    map.put(columnName, m.invoke(o));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    /**
     * 对象的属性转为ContentValues，命名不变
     * @param o
     * @return
     */
    public static ContentValues object2ContentValue(Object o) {
        ContentValues v = new ContentValues();
        Method[] ms = o.getClass().getMethods();
        for (Method m : ms) {
            if (m.getName().startsWith("get") && ! m.getName().equals("getClass") ) {
//                String columnName = m.getName().replaceAll("[A-Z]", "_$0").substring(4).toUpperCase();
                String columnName = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
                try {
                    m.setAccessible(true);
                    putValue(v, columnName, m.invoke(o));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return v;
    }

    public static void putValue(ContentValues cv, String key, Object value) {
        if (value instanceof String) cv.put(key, (String) value);
        else if (value instanceof Integer) cv.put(key, (Integer) value);
        else if (value instanceof Float) cv.put(key, (Float) value);
        else if (value instanceof Double) cv.put(key, (Double) value);
    }
}
