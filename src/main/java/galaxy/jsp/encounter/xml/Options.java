package galaxy.jsp.encounter.xml;

import galaxy.jsp.encounter.util.Const;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Options {

    public List<Pair<String,Map<String,String>>> extTags = new ArrayList<>();
    //public boolean isXml = true;
    public String fileType = ".jsp";
    public boolean attributeEscape = true;
    public String namespace = "jsp";
    public String xmlVersion = "1.0";
    public String jspVersion = "1.2";
    public String charsetMagic = null;
    public boolean removeBom = false;
    public boolean charsetDeclareLater = false;
    public String charsetDeclare = "utf-8";

    public String codeEscapeType;

    // jsp:root ，false :declare charset valid, true: namespace can change
    public boolean removeJspRootTag = false;

    //
    public String charsetDeclareAttr = Const.DeclareAttr.pageEncoding.name();


    public String attrEscapeType = Const.EscapeType.entityNormal.name();
    public String txtEscapeType = Const.EscapeType.entityNormal.name();

    // xmlVersion 1.1 && set controllerSpaceReplace
    //public boolean controllerSpaceOn = false;
    public Map<String,String> controllerSpaceReplace = null;
//            new HashMap<String, String>(){{
//        put("(","%s(");
//        put(")",")%s");
//    }};

    // entity | cdata
//    public String escapeType = "entity";
    // 13
    // 10 | 16  |
    // 5  | 8
    public int entityEscapeType = 10;
    public int cdataWrapCap = -1;
    public boolean tagSetPropertyOn = false;
    public boolean tagUseBeanOn = false;


    @Override
    public String toString() {
        return "Options{" +
                "extTags=" + extTags +
                ", fileType='" + fileType + '\'' +
                ", attributeEscape=" + attributeEscape +
                ", namespace='" + namespace + '\'' +
                ", xmlVersion='" + xmlVersion + '\'' +
                ", jspVersion='" + jspVersion + '\'' +
                ", charsetMagic='" + charsetMagic + '\'' +
                ", removeBom=" + removeBom +
                ", charsetDeclareLater=" + charsetDeclareLater +
                ", charsetDeclare='" + charsetDeclare + '\'' +
                ", codeEscapeType='" + codeEscapeType + '\'' +
                ", removeJspRootTag=" + removeJspRootTag +
                ", charsetDeclareAttr='" + charsetDeclareAttr + '\'' +
                ", attrEscapeType='" + attrEscapeType + '\'' +
                ", txtEscapeType='" + txtEscapeType + '\'' +
                ", controllerSpaceReplace=" + controllerSpaceReplace +
                ", entityEscapeType=" + entityEscapeType +
                ", cdataWrapCap=" + cdataWrapCap +
                ", tagSetPropertyOn=" + tagSetPropertyOn +
                ", tagUseBeanOn=" + tagUseBeanOn +
                '}';
    }
}
