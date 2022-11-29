package galaxy.jsp.encounter.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class Utils {
    /**
     * 字符串转 Unicode 编码
     *
     * @param string   原字符串
     * @param halfWith 是否转换半角字符
     * @return 编码后的字符串
     */
    public static String StrToUnicode(String string, boolean halfWith) {
        if (string == null || string.isEmpty()) {
            // 传入字符串为空返回原内容
            return string;
        }

        StringBuilder value = new StringBuilder(string.length() << 3);
        String prefix = "\\u", zerofix = "0", unicode;
        char c;
        for (int i = 0, j; i < string.length(); i++) {
            c = string.charAt(i);
            if (!halfWith && c > 31 && c < 127) {
                // 不转换半角字符
                value.append(c);
                continue;
            }
            value.append(prefix);

            // 高 8 位
            j = c >>> 8;
            unicode = Integer.toHexString(j);
            if (unicode.length() == 1) {
                value.append(zerofix);
            }
            value.append(unicode);

            // 低 8 位
            j = c & 0xFF;
            unicode = Integer.toHexString(j);
            if (unicode.length() == 1) {
                value.append(zerofix);
            }
            value.append(unicode);
        }

        return value.toString();
    }


    public static String RStrip(String s) {
        int len = s.length();
        int st = 0;
        char[] val = s.toCharArray();

//        while ((st < len) && (val[st] <= ' ')) {
//            st++;
//        }
        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return s.substring(st, len);
    }

    public static Method GetMethod(Class<?> clazz, String name, Class<?>... clazzs) throws NoSuchMethodException {
        Method m = clazz.getDeclaredMethod(name, clazzs);
        m.setAccessible(true);
        return m;
    }

//    public static Object InvokeMethod(Object obj, Method method, Object... args) {
//        return method.invoke(obj, args);
//    }

    public static Type GetFieldType(Object obj, String name) throws NoSuchFieldException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.getGenericType();
    }

    public static void SetFieldValue(Object obj, String name, Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    public static Class<?> GetClass(String name) throws ClassNotFoundException {
        return Utils.class.getClassLoader().loadClass(name);
    }

    public static Object GetFieldValue(String name, String fieldName, Object dst) throws Exception {
        Field field = GetClass(name).getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(dst);
    }


    public static int CountChar(String text,char c) {
        int count=0;
        for (char t : text.toCharArray()) {
            if (c == t) {
                count+=1;
            }
        }
        return count;
    }

    private static int hexEntityHelp = 0;

    public static String ToEntity(int c, int entityType) {

        if (entityType == 10) {
            return "&#" + c + ";";
        }else if(entityType== 16){
            return "&#x" + Integer.toHexString(c) + ";";
        }else{
            hexEntityHelp = (hexEntityHelp+1)%2;
            if (hexEntityHelp == 0) {
                return "&#" + c + ";";
            }else{
                return "&#x" + Integer.toHexString(c) + ";";
            }
        }

    }

    public static String WrapWithCdata(String text, int cap) {
        if (cap< 0 || text.length()<cap){
            return "<![CDATA[" + text + "]]>";
        }

        int turn = (int) Math.ceil((float)text.length() / (float)cap);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < turn; i++) {
            int start = i * cap;
            sb.append("<![CDATA[");
            if (i == turn - 1) {
                System.out.printf("%d %d \n",start,text.length());
                sb.append(text, start, text.length());
            }else{
                System.out.printf("%d %d \n",start,start + cap);
                sb.append(text, start, start + cap);
            }
            sb.append("]]>");
        }
        return sb.toString();
    }

    public static String UnicodeEscape(String text) {
        return StrToUnicode(text, true);
//        int size = text.length();
//        StringBuilder buffer = new StringBuilder();
//        for (int i = 0; i < size; i++) {
//            int c = text.codePointAt(i);
//            String hex = Integer.toHexString(c);
//            if (hex.length() == 1) {
//                hex = "0" + hex;
//            }
//            buffer.append("\\u00").append(hex);
//        }
//        return buffer.toString();
    }


    public static String EscapeText(String text, boolean escapeAll,int entityType) {
//        char quote = format.getAttributeQuoteCharacter();
        StringBuilder buffer = new StringBuilder();
        char[] block = null;
        int i;
        int last = 0;
        int size = text.length();

        for (i = 0; i < size; i++) {
            String entity = null;
            int c = text.codePointAt(i);

            switch (c) {
                // 支持 controller space 逻辑
                case '§':
                    entity = "§";
                    break;
                case '<':
                    entity = "&lt;";
                    break;
                case '>':
                    entity = "&gt;";
                    break;
                case '\'':
                    entity = "&apos;";
                    break;
                case '\"':
                    entity = "&quot;";
                    break;
                case '&':
                    entity = "&amp;";
                    break;
                case '\t':
                case '\n':
                case '\r':
                    // don't encode standard whitespace characters
                    break;
                default:
                    if ((c < 32) || escapeAll) {
                        //entity = "&#" + c + ";";
                        entity= Utils.ToEntity(c,entityType);
                    }
                    break;
            }

            if (entity != null) {
                if (block == null) {
                    block = text.toCharArray();
                }

                buffer.append(block, last, i - last);
                buffer.append(entity);
                last = i + 1;
                if(Character.isSupplementaryCodePoint(c)) {
                    last++;
                }
            }
            if(Character.isSupplementaryCodePoint(c)) {
                i++;
            }
        }

        if (last == 0) {
            return text;
        }

        if (last < size) {
            if (block == null) {
                block = text.toCharArray();
            }

            buffer.append(block, last, i - last);
        }

        String answer = buffer.toString();
        buffer.setLength(0);

        return answer;
    }

}
