//package org.apache.jasper.compiler;
//
//import org.apache.jasper.JasperException;
//import org.apache.jasper.JspCompilationContext;
//import org.apache.jasper.compiler.Node;
//import sun.misc.Unsafe;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//
//public class Test {
//    public static void main(String[] args) throws Exception {
//
//
//        String file = Test.class.getResource("/test.jsp").getPath();
//
//
//        Field field = Unsafe.class.getDeclaredField("theUnsafe");
//        field.setAccessible(true);
//        Unsafe unsafe = (Unsafe) field.get(null);
//
//        JspCompilationContext ctx = (JspCompilationContext) unsafe.allocateInstance(JspCompilationContext.class);
//        Compiler compiler = new JDTCompiler();
//        ParserController parserCtl = new ParserController(ctx, compiler);
//
//        // Pass 1 - the directives
//        Node.Nodes directives =
//                parserCtl.parseDirectives(file);
//        //Validator.validateDirectives(this, directives);
//
//        // Pass 2 - the whole translation unit
//        Node.Nodes pageNodes = parserCtl.parse(file);
//
//    }
//}
