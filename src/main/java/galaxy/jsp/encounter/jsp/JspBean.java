package galaxy.jsp.encounter.jsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JspBean {

    // jsp:directive.page|jsp:directive.include
    // jsp:scriptlet|jsp:declaration|jsp:expression
    //jsp:directive.tag
    List<Map<String,String>> pages ;
    List<Map<String,String>> include ;

    List<String> declarations;
    List<String> scriptlets;
    List<String> expressions;

    @Override
    public String toString() {
        return "JspBean{" +
                "pages=" + pages +
                ", include=" + include +
                ", declarations=" + declarations +
                ", expressions=" + expressions +
                ", scriptlets=" + scriptlets +
                '}';
    }

    public void setExpressions(List<String> expressions) {
        this.expressions = expressions;
    }

    public List<String> getExpressions() {
        return expressions;
    }

    public void addPage(Map<String,String> page) {
        if (pages == null) {
            pages = new ArrayList<>();
        }
        pages.add(page);
    }

    public List<Map<String, String>> getPages() {
        return pages;
    }


    public List<String> getScriptlets() {
        return scriptlets;
    }

    public void addsScriptlets(String dec) {
        if (scriptlets == null) {
            scriptlets = new ArrayList<>();
        }
        scriptlets.add(dec);
    }

    public void setScriptlets(List<String> scriptlets) {
        this.scriptlets = scriptlets;
    }

    public void setDeclarations(List<String> declarations) {
        this.declarations = declarations;
    }

    public List<String> getDeclarations() {
        return declarations;
    }

    public void addDeclaration(String dec) {
        if (declarations == null) {
            declarations = new ArrayList<>();
        }
        declarations.add(dec);
    }
}
