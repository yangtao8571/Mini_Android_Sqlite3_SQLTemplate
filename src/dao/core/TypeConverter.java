package dao.core;

import android.database.Cursor;

import lh.com.lib_common.utils.SqliteUtil;
import com.orhanobut.logger.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Convert cursor data to bean list.
 *
 * @param <T> bean type
 */
public class TypeConverter<T> {

    private Cursor _cursor;

    public TypeConverter (Cursor cursor) {
        _cursor = cursor;
    }

    /**
     * Convert cursor data to List of bean of type clz.
     *
     * @param clz
     * @return
     */
    public List<T> toBeans(Class clz) {
        List<T> beanList = new ArrayList<>();
        try {
            T bean = (T) clz.newInstance();
            String[] colnames = _cursor.getColumnNames();
            while (_cursor.moveToNext()) {
                for (String colname : colnames) {
                    // find the setter method by column name.
                    //
                    String methodName = "set" + colname.substring(0, 1).toUpperCase() + colname.substring(1);
//                    String methodName = getBeanMethodCamalName("set", colname);
                    Method[] marr = clz.getMethods();
                    Method m = null;
                    for (int i = 0; i < marr.length; i++) {
                        if (marr[i].getName().equals(methodName)) {
                            m = marr[i];
                            break;
                        }
                    }

                    if (m == null)
                        continue;

                    // invoke setter to set value.
                    //
                    Class[] paramTypes = m.getParameterTypes();
                    Class colClass = paramTypes[0];
                    if (colClass == String.class) {
                        String colValue = _cursor.getString(_cursor.getColumnIndex(colname));
                        m.invoke(bean, colValue);
                    } else if (colClass == Integer.class) {
                        Integer colValue = SqliteUtil.getInteger(_cursor, colname);
                        m.invoke(bean, colValue);
                    } else if (colClass == int.class) {
                        Integer colValue = SqliteUtil.getInteger(_cursor, colname);
                        if (colValue == null) colValue = Integer.MAX_VALUE;
                        m.invoke(bean, colValue);
                    } else if (colClass == Float.class) {
                        Float colValue = SqliteUtil.getFloat(_cursor, colname);
                        m.invoke(bean, colValue);
                    } else if (colClass == float.class) {
                        Float colValue = SqliteUtil.getFloat(_cursor, colname);
                        if (colValue == null) colValue = Float.MAX_VALUE;
                        m.invoke(bean, colValue);
                    } else if (colClass == Double.class) {
                        Double colValue = SqliteUtil.getDouble(_cursor, colname);
                        m.invoke(bean, colValue);
                    } else if (colClass == double.class) {
                        Double colValue = SqliteUtil.getDouble(_cursor, colname);
                        if (colValue == null) colValue = Double.MAX_VALUE;
                        m.invoke(bean, colValue);
                    }
                }
                beanList.add(bean);
            }
            return beanList;
        } catch (IllegalAccessException e) {
            Logger.e(e, e.getMessage());
            throw new RuntimeException("实例化错误");
        } catch (InstantiationException e) {
            Logger.e(e, e.getMessage());
            throw new RuntimeException("实例化错误");
        } catch (InvocationTargetException e) {
            Logger.e(e, e.getMessage());
            throw new RuntimeException("赋值错误");
        }
    }

    // 拆分字段，并且组装成驼峰法命名规则
    public static String getBeanMethodCamalName(String prefix, String name) {
        if (name.contains("_")) {
            String[] spName = name.toLowerCase().split("_");
            name = "";
            for (String na : spName) {
                name += na.substring(0, 1).toUpperCase()
                        + na.substring(1).toLowerCase();
            }
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        } else {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return prefix + name;
    }

}
