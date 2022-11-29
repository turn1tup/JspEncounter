package galaxy.jsp.encounter.jsp;


import galaxy.jsp.encounter.util.Const;
import galaxy.jsp.encounter.util.Utils;
import galaxy.jsp.encounter.charset.CharsetEncoder;
import galaxy.jsp.encounter.xml.JspXMLWriter;
import galaxy.jsp.encounter.xml.Options;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JspXML {

    public static void BeanToXML(OutputStream outputStream,JspBean bean, Options options) throws Exception {

        if (options.charsetMagic != null) {
            String headerRaw = String.format("<?xml version=\"%s\" encoding=\"%s\"?>", options.xmlVersion,
                    options.charsetMagic);
            byte[] header = CharsetEncoder.Encode(headerRaw, options.charsetMagic,options.removeBom);
            outputStream.write(header);
        }

        ByteArrayOutputStream bodyOutputStream = new ByteArrayOutputStream();
        BeanToBodyXML(bodyOutputStream, bean, options);


        String bodyStr = bodyOutputStream.toString();
        if (options.removeJspRootTag) {
            String lf = "\n";
            int index1 = bodyStr.indexOf(lf,1);
            int index2 = bodyStr.lastIndexOf(lf);
            bodyStr = bodyStr.substring(index1, index2);
        }


        // 前置declare，则需要与charsetMagic的一致
        String tagWord = "<asdfqwezxcv/>";
        int kIndex = bodyStr.indexOf(tagWord);
        String bodyStrPrefix = bodyStr.substring(0, kIndex);
        bodyStrPrefix = Utils.RStrip(bodyStrPrefix);
        String bodyStrSuffix = bodyStr.substring(kIndex+tagWord.length());
        String charsetTmp = options.charsetMagic != null ? options.charsetMagic : options.charsetDeclare;
//        if (options.charsetMagic== null) {
//            bodyStrPrefix = bodyStrPrefix.trim();
//        }

        byte[] bodyPrefix = CharsetEncoder.Encode(bodyStrPrefix, charsetTmp, true);
        outputStream.write(bodyPrefix);

        if (options.controllerSpaceReplace!=null) {
        //if (options.controllerSpaceOn) {
            byte[] marks = CharsetEncoder.Encode("§", options.charsetDeclare, true);
            byte[] bodySuffix = CharsetEncoder.Encode(bodyStrSuffix, options.charsetDeclare, true);
            Charset charset = Charset.forName(options.charsetDeclare);
            ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
            int needIgnore = 0;
            for (int bodyIndex=0;bodyIndex<bodySuffix.length;bodyIndex++) {
                if (needIgnore>0) {
                    needIgnore -=1;
                    continue;
                }
                // 前面流程替换了标签中text数据的空白字符为§
                //152:-62 153:-89 length725
                byte bodyChar = bodySuffix[bodyIndex];

                if (bodyIndex + marks.length < bodySuffix.length) {
                    boolean matchMarks = true;
                    for (int markIndex=0;markIndex<marks.length; markIndex++) {
                        if (marks[markIndex] != bodySuffix[bodyIndex + markIndex]) {
                            matchMarks = false;
                            break;
                        }
                    }
                    if (matchMarks) {
                        needIgnore = marks.length-1;
                        if (charset == StandardCharsets.UTF_8) {
                            tmpStream.write(-30);
                            tmpStream.write(-128);
                            tmpStream.write(-88);
                        }else if(charset == StandardCharsets.ISO_8859_1){
                            tmpStream.write(0x85);
                            //tmpStream.write(CharsetEncoder.Encode(" ",options.charsetDeclare,true));
                        }else if(charset == StandardCharsets.UTF_16||charset == StandardCharsets.UTF_16BE){
                            tmpStream.write(0x00);
                            tmpStream.write(0x85);
                        }else if(charset == StandardCharsets.UTF_16LE){
                            tmpStream.write(0x85);
                            tmpStream.write(0x00);
                        }else{
                            tmpStream.write(CharsetEncoder.Encode(" ",options.charsetDeclare,true));
                        }
                    }else{
                        tmpStream.write(bodyChar);
                    }
                }else{
                    tmpStream.write(bodyChar);
                }

            }
            outputStream.write(tmpStream.toByteArray());
        }else{
            if (options.charsetDeclareLater) {
                int ltIndex = bodyStrSuffix.lastIndexOf("<");
                String bodyStrSuffix1 = bodyStrSuffix.substring(0, ltIndex);
                String bodyStrSuffix2 = bodyStrSuffix.substring(ltIndex);
                byte[] bodySuffix1 = CharsetEncoder.Encode(bodyStrSuffix1, options.charsetDeclare, true);
                outputStream.write(bodySuffix1);
                byte[] bodySuffix2 = CharsetEncoder.Encode(bodyStrSuffix2, "utf-8", true);
                outputStream.write(bodySuffix2);
            }else{

                byte[] bodySuffix = CharsetEncoder.Encode(bodyStrSuffix, options.charsetDeclare, true);
                outputStream.write(bodySuffix);
            }
        }




    }

//    public static String toControllerSpace(String str, Options options) {
//        String space = null;
//        if (!"1.1".equals(options.xmlVersion)) {
//            return str;
//        }
//        if (Charset.forName(options.charsetDeclare) == StandardCharsets.UTF_8) {
//            space = new String(new byte[]{0xff});
//        }
//        str.replace()
//        return "";
//    }

    private static void addPageEncoding(List<Map<String, String>> pages,Options options) {
        if (Const.DeclareAttr.pageEncoding.name().equals(options.charsetDeclareAttr)) {
            pages.add(new HashMap<String, String>() {{
                put(options.charsetDeclareAttr, options.charsetDeclare);
            }});
        }else{
            pages.add(new HashMap<String, String>() {{
                put(options.charsetDeclareAttr,
                        RandomStringUtils.randomAlphabetic(10)+"charset="+options.charsetDeclare);
            }});
        }
    }

    private static void BeanToBodyXML(OutputStream outputStream, JspBean bean, Options options) throws Exception {
        if (options.namespace!=null && options.namespace.startsWith("random")) {
            int n = Integer.parseInt(options.namespace.split(",")[1]);
            options.namespace = RandomStringUtils.randomAlphabetic(n);
        }
        Namespace jspNs = new Namespace(options.namespace, "http://java.sun.com/JSP/Page");
        Document document = DocumentHelper.createDocument();

        Element root = document.addElement(new QName("root", jspNs));
        root.addAttribute("version", options.jspVersion);

        // filter
        List<Map<String, String>> pages = new ArrayList<>();
        String[] filters = new String[]{Const.DeclareAttr.pageEncoding.name(),
                Const.DeclareAttr.contentType.name()};
        for (Map<String, String> attrs : bean.getPages()) {
            Map<String, String> tmp = null;
            for (String k : attrs.keySet()) {
                if (Arrays.stream(filters).anyMatch(f -> f.equalsIgnoreCase(k))) {
                    continue;
                }
                if (tmp == null) {
                    tmp = new HashMap<>();
                }
                tmp.put(k, attrs.get(k));
            }
            if (tmp != null) {
                if (!options.charsetDeclareLater) {
                    addPageEncoding(pages, options);
                }
                pages.add(tmp);
            }
        }

        if (pages.size() == 0 && !options.charsetDeclareLater) {
            addPageEncoding(pages, options);
        }


        int count = 0;
        for (Map<String, String> attrs : pages) {
            Element ele = root.addElement(new QName("directive.page", jspNs));
            for (String k : attrs.keySet()) {
                ele.addAttribute(k, attrs.get(k));
            }
            if (count == 0) {
                root.addElement("asdfqwezxcv");
            }
            count += 1;
        }


        if (bean.getDeclarations() != null) {
            for (String txt : bean.getDeclarations()) {
                Element ele = root.addElement(new QName("declaration", jspNs));
                //Element ele =addElement(root, new QName("declaration", jspNs));
                txt = codeEscape(txt, options);
                ele.setText(txt);
            }
        }

        if (bean.getExpressions() != null) {
            for (String txt : bean.getExpressions()) {
                Element ele = root.addElement(new QName("expression", jspNs));
                //Element ele = addElement(root, new QName("expression", jspNs));
                txt = codeEscape(txt, options);
                ele.setText(txt);
            }
        }

        boolean isSetTagCode = false;
        // § 标记出现次数
        int markCount = 0;
        for (Pair<String,Map<String, String>> extTag : options.extTags) {
            String name = extTag.getLeft();
            isSetTagCode = true;
            Map<String, String> attrs = extTag.getRight();
            for (String val : attrs.values()) {

                markCount += Utils.CountChar(val, '§');
            }
        }

        if (isSetTagCode) {
            StringBuilder sb = new StringBuilder();
            for (String txt : bean.getScriptlets()) {
                sb.append(txt);
            }
            String text = sb.toString();


            String[] lines = text.split("\n");
            StringBuilder[] items = new StringBuilder[markCount];
            int sumLine = lines.length;
            int cap = (int) Math.ceil((float)sumLine/(float)markCount);
            for (int i = 0; i < lines.length; i++) {
                StringBuilder sbTmp = items[i / cap];
                if (sbTmp == null) {
                    sbTmp = new StringBuilder();
                    items[i / cap] = sbTmp;
                }
                sbTmp.append(lines[i]);
            }
            Pattern p = Pattern.compile("(§\\d+)");
            for (Pair<String,Map<String, String>> extTag : options.extTags) {
                String name = extTag.getLeft();
                Map<String, String> attrs = extTag.getRight();
                Element tag = root.addElement(new QName(name, jspNs));
                for (String k : attrs.keySet()) {
                    String v = attrs.get(k);
                    for (; ; ) {
                        Matcher m = p.matcher(v);
                        if (!m.find()) {
                            break;
                        }
                        String tmp = items[Integer.parseInt(m.group(1).substring(1)) - 1].toString();
                        tmp = tmp.replace("\"", "\\\"");
                        v = v.replace(m.group(1),tmp);
                    }

                    tag.addAttribute(k, v);
                }
            }

        }

        if (bean.getScriptlets() != null && !isSetTagCode) {
            for (String txt : bean.getScriptlets()) {
                Element ele = root.addElement(new QName("scriptlet", jspNs));
                //Element ele = addElement(root, new QName("scriptlet", jspNs));
                txt = codeEscape(txt, options);
                ele.setText(txt);
            }
        }

        if (options.charsetDeclareLater) {
            List<Map<String, String>> pagesTmp = new ArrayList<>();
            addPageEncoding(pagesTmp, options);
            for (Map<String, String> attrs : pagesTmp) {
                Element ele = root.addElement(new QName("directive.page", jspNs));
                for (String k : attrs.keySet()) {
                    ele.addAttribute(k, attrs.get(k));
                }
            }
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setSuppressDeclaration(true);

        format.setEncoding("UTF-8");
        format.setNewLineAfterDeclaration(false);

//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JspXMLWriter writer = new JspXMLWriter(outputStream, format, options);
        writer.setEscapeText(true);
        // 设置是否转义，默认使用转义字符
        writer.setEscapeText(false);
        writer.write(document);
        writer.close();

//        return outputStream;
    }

    private static String codeEscape(String text, Options options) {
        System.out.println(options);
        if (options.codeEscapeType == null) {
            return text;
        }
        if ("unicode".equalsIgnoreCase(options.codeEscapeType)) {
            return Utils.UnicodeEscape(text);
        }
        return text;
    }
}
