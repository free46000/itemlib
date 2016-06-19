package free.com.itemlib.item.view.common;


import java.lang.reflect.Field;

/**
 * Created by free46000 on 2015/6/6 0006.
 */
public class ReflectUtil {
    public static final String LogTag = "ReflectUtil";

    public static void setValue(String fieldName, Object value, Object obj) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            if ("int".equals(field.getType().getSimpleName())) {
                field.set(obj, Integer.parseInt((String) value));
            } else {
                field.set(obj, value);
            }
        } catch (Exception e) {
//            CommonMethod.logException(LogTag, e);
        }
    }

    public static String getValue(String fieldName, Object obj) {
        String s = null;
        try {
            s = getValue(obj.getClass().getDeclaredField(fieldName), obj);
        } catch (Exception e) {
//            CommonMethod.logException(LogTag, e);
        }
        return s;
    }

    public static String getValue(Field field, Object obj) {
        String s = null;
        try {
            field.setAccessible(true);
            Object valueObj = field.get(obj);
            if (valueObj != null)
                s = field.get(obj).toString();
        } catch (Exception e) {
//            CommonMethod.logException(LogTag, e);
        }
        return s;
    }

}
