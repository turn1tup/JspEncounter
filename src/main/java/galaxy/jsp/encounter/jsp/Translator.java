package galaxy.jsp.encounter.jsp;

import galaxy.jsp.encounter.cfg.Config;
import galaxy.jsp.encounter.xml.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {

    public static void main(String[] args) throws Exception {


        //[-30, -128, -88]
        byte[] bytes = new byte[3];

        int b = 0xc2;
        byte b2 = (byte) 0xc2;
        Translator.class.getResourceAsStream("/test.txt").read(bytes);

        System.out.println(new String(bytes,StandardCharsets.UTF_8));
//        String control = IOUtils.toString(Translator.class.getResourceAsStream("/test.txt"), StandardCharsets.UTF_8);

        String src = IOUtils.toString(Translator.class.getResourceAsStream("/test3.jsp"), StandardCharsets.UTF_8);
        //System.out.println(src);
        JspBean bean = TranslateToBean(src);
        //InputStream inputStream = Translator.class.getResourceAsStream("/matrix.xlsx");
        InputStream inputStream = new FileInputStream("E:\\turn1tup\\memshell\\JspEncounter\\pkg\\matrix.xlsx");
        Options options = Config.ParseCfg(inputStream);


//        options.charsetDeclare = "utf-8";
//        options.namespace = "asdf";
        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FileOutputStream outputStream = new FileOutputStream("E:\\turn1tup\\program_java\\2022.11" +
                ".04_jsp\\apache-tomcat-10.0.27-windows-x64\\apache-tomcat-10.0.27\\webapps\\ROOT\\result.jsp");
        //outputStream.write(0x85);
      //  outputStream.write(0x20);
        //outputStream.write(0x3f);
//        outputStream.write(0xe2);
//        outputStream.write(0x80);
//        outputStream.write(0xa8);

        JspXML.BeanToXML(outputStream, bean, options);

        //System.out.println(new String(outputStream.toByteArray()));


    }

    public static Pair<Integer,List<String>> LoopMatch(String text, String reg) {
        return LoopMatch(text, reg, 0);
    }

    public static Pair<Integer,List<String>> LoopMatch(String text,String reg,int f) {
        List<String> results = null;
        int end =0;
        for (; ; ) {
            Matcher mTmp = Pattern.compile(reg,f).matcher(text);
            if (!mTmp.find()) {
                break;
            }
            if (results == null) {
                results = new ArrayList<>();
            }

            for (int i = 1; i < mTmp.groupCount()+1; i++) {
                results.add(mTmp.group(i));
            }
            end += mTmp.end();
            text = text.substring( mTmp.end());


        }
        if (results == null) {
            return null;
        }else{
            return new ImmutablePair<>(end, results);
        }

    }


    public static Map<String, String> AttrsToMap(String attrsTxt, String regexAttrs) {
        Pair<Integer, List<String>> attrs = LoopMatch(attrsTxt, regexAttrs);
        if (attrs == null) {
            return null;
        }
        List<String> kvL = attrs.getRight();
        Map<String,String> kvM = new HashMap<>();
        for (int i = 0; i < kvL.size(); i+=2) {
            kvM.put(kvL.get(i), kvL.get(i + 1));
        }
        return kvM;
    }


    public static JspBean TranslateToBean(String src) {
        JspBean bean = new JspBean();
        String regexPage = "<%\\s*@\\s*page\\s*(.*?)%>";
        String regexDeclaration = "<%!\\s*(.*?)%>";
        String regexExpr = "<%=\\s*(.*?)%>";
        String regexScriptlet = "<%\\s*(.*?)%>";
        String regexAttrs = "\\s*(.*?)=[\"](.*?[^\\\\])[\"]";


        Pair<Integer,List<String>> pages = LoopMatch(src, regexPage, Pattern.DOTALL);
        if (pages != null) {
            src = src.substring(pages.getLeft());
            for (String attrsTxt : pages.getRight()) {
                Map<String, String> kvM = AttrsToMap(attrsTxt, regexAttrs);
                if (kvM == null) {
                    continue;
                }
                bean.addPage(kvM);
            }
        }

        Pair<Integer,List<String>> decs = LoopMatch(src, regexDeclaration, Pattern.DOTALL);
        if (decs != null) {
            src = src.substring(decs.getLeft());
            bean.setDeclarations(decs.getRight());
        }

        Pair<Integer,List<String>> exprs = LoopMatch(src, regexExpr, Pattern.DOTALL);
        if (exprs != null) {
            src = src.substring(exprs.getLeft());
            bean.setExpressions(exprs.getRight());
        }

        Pair<Integer,List<String>> scripts = LoopMatch(src, regexScriptlet, Pattern.DOTALL);
        if (scripts != null) {
            //src = src.substring(scripts.getLeft());
            bean.setScriptlets(scripts.getRight());
        }

        return bean;

    }


}
