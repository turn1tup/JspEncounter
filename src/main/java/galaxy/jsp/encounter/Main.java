package galaxy.jsp.encounter;

import galaxy.jsp.encounter.cfg.Config;
import galaxy.jsp.encounter.charset.CharsetEncoder;
import galaxy.jsp.encounter.jsp.JspBean;
import galaxy.jsp.encounter.jsp.JspXML;
import galaxy.jsp.encounter.jsp.Translator;
import galaxy.jsp.encounter.util.Utils;
import galaxy.jsp.encounter.xml.Options;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {


    public static void main(String[] args) throws Exception{

        try {

            CommandLineParser commandLineParser = new DefaultParser();
            org.apache.commons.cli.Options cmdOption = new org.apache.commons.cli.Options();

            cmdOption.addOption("help","usage help");
            cmdOption.addOption(Option.builder("m").required().hasArg(true).longOpt("mode").type(String.class).desc(
                            "jspfile or encode").build());
            cmdOption.addOption(Option.builder("c").required(false).hasArg(true).longOpt("config").type(String.class).desc(
                    "xlsx config file").build());
//            cmdOption.addOption(Option.builder("cfgE").required(false).hasArg(true).longOpt("configFileEncoding").type(String.class).desc(
//                    "xlsx config file's encoding,default gbk").build());
            cmdOption.addOption(Option.builder("s").required(false).hasArg(true).longOpt("source").type(String.class).desc(
                    "source file").build());
            cmdOption.addOption(Option.builder("o").required(false).hasArg(true).longOpt("output").type(String.class).desc(
                    "output file: default result.jsp/result.txt").build());
            cmdOption.addOption(Option.builder("e").required(false).hasArg(true).longOpt("encode").type(String.class).desc(
                    "encode mode's encoding: charset, cdata, entity").build());
            cmdOption.addOption(Option.builder("charset").required(false).hasArg(true).longOpt("charset").type(String.class).desc(
                    "charset name: utf-8, utf-16be ...").build());
        cmdOption.addOption(Option.builder("removeBom").required(false).hasArg(true).longOpt("removeBom").type(Boolean.class).desc(
                "removeBom : default true").build());
            cmdOption.addOption(Option.builder("entity").required(false).hasArg(true).longOpt("entity").type(String.class).desc(
                    "entity type: 10, 16, 13 ...").build());
            cmdOption.addOption(Option.builder("cdata").required(false).hasArg(true).longOpt("cdata").type(String.class).desc(
                    "cdata cap: -1, 10 ...").build());
            CommandLine cl = null;
            try {

                cl = commandLineParser.parse(cmdOption, args);
            } catch (ParseException e) {
                printLogo();
                System.out.println(getHelpString(cmdOption));
                System.out.println("demo: \r\n     java \"-Dfile.encoding=utf-8\" -jar JspEncounter.jar -m jsp -c " +
                        "matrix" +
                        ".xlsx -s source.jsp -o result.jsp");
                System.out.println("     java -jar JspEncounter.jar -m encode --encode charset --charset utf-16be -s " +
                        "source.txt -o" +
                        " result.txt");
                return;
            }

            String src = cl.getOptionValue("source");
            if (src == null ) {
                throw new Exception("source file not set");
            }
            String o = cl.getOptionValue("o");


        String srcStr = IOUtils.toString(new FileInputStream(src), StandardCharsets.UTF_8);
            if ("jsp".equals(cl.getOptionValue("m"))) {
                String xlsx = cl.getOptionValue("config");
                if (xlsx==null) {
                    throw new Exception("config file not set");
                }
                if (o == null) {
                    o = "result.jsp";
                }
                InputStream inputStreamXlsx = new FileInputStream(xlsx);
                JspBean bean = Translator.TranslateToBean(srcStr);
                Options options = Config.ParseCfg(inputStreamXlsx);

                FileOutputStream outputStream = new FileOutputStream(o);

                JspXML.BeanToXML(outputStream, bean, options);
            }else{
                if (o == null) {
                    o = "result.txt";
                }
                String encode = cl.getOptionValue("encode");
                byte[] bytes = null;
                if ("charset".equals(encode)) {
                    bytes = CharsetEncoder.Encode(srcStr, cl.getOptionValue("charset", "utf-8"),
                            Boolean.parseBoolean(cl.getOptionValue("removeBom","true")));
                }else if ("cdata".equals(encode)){
                    String result = Utils.WrapWithCdata(srcStr, Integer.parseInt(cl.getOptionValue("cdata", "10")));
                    bytes = result.getBytes(StandardCharsets.UTF_8);
                }else if ("entity".equals(encode)){
                    String result = Utils.EscapeText(srcStr, true, Integer.parseInt(cl.getOptionValue("entity","10") ));
                    bytes = result.getBytes(StandardCharsets.UTF_8);
                }
                FileOutputStream fileOutputStream = new FileOutputStream(o);
                assert bytes != null;
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            }


        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    private static void printLogo() {
        System.out.println( "       _           ______                             _            \n" +
                "      | |         |  ____|                           | |           \n" +
                "      | |___ _ __ | |__   _ __   ___ ___  _   _ _ __ | |_ ___ _ __ \n" +
                "  _   | / __| '_ \\|  __| | '_ \\ / __/ _ \\| | | | '_ \\| __/ _ \\ '__|\n" +
                " | |__| \\__ \\ |_) | |____| | | | (_| (_) | |_| | | | | ||  __/ |   \n" +
                "  \\____/|___/ .__/|______|_| |_|\\___\\___/ \\__,_|_| |_|\\__\\___|_|   \n" +
                "            | |                                                    \n" +
                "            |_|                                                    ");
    }

    private static String getHelpString(org.apache.commons.cli.Options options) {


        HelpFormatter helpFormatter = new HelpFormatter();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "java -jar JspEncounter.jar", null,
                options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        printWriter.flush();
        String help = new String(byteArrayOutputStream.toByteArray());
        printWriter.close();

        return help;
    }
}
