package galaxy.jsp.encounter.charset;

import galaxy.jsp.encounter.util.Utils;
//import sun.nio.cs.StreamEncoder;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CharsetEncoder {

    public static byte[] Encode(String text,String charset, boolean removeBom) throws Exception {

        if (text == null || text.length()==0) {
            return null;
        }
        Charset charset1 = Charset.forName(charset);
        byte[] result = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //Constructor<?> c = StreamEncoder.class.getDeclaredConstructor(OutputStream.class, Object.class, Charset
        // .class);
        Constructor<?> c =Utils.GetClass("sun.nio.cs.StreamEncoder").getDeclaredConstructor(OutputStream.class,
                Object.class, Charset.class);
        c.setAccessible(true);
        // StreamEncoder streamEncoder = (StreamEncoder) c.newInstance(outputStream, "LOCK",charset1);
        Writer streamEncoder = (Writer) c.newInstance(outputStream, "LOCK",charset1);

        if (!removeBom) {
            if (StandardCharsets.UTF_16BE == charset1) {
                outputStream.write( 0xfe);
                outputStream.write( 0xff);

            } else if (StandardCharsets.UTF_16LE == charset1) {
                outputStream.write( 0xff);
                outputStream.write( 0xfe);

            }else if (StandardCharsets.UTF_8 == charset1) {
                outputStream.write( 0xef);
                outputStream.write( 0xbb);
                outputStream.write( 0xbf);
            }

        }
        streamEncoder.write(text.substring(0,1));
        ByteBuffer bb = (ByteBuffer) Utils.GetFieldValue("sun.nio.cs.StreamEncoder", "bb", streamEncoder);
        int bomSize = bb.position();
        if (removeBom) {
            streamEncoder.write(text);
        } else {
            streamEncoder.write(text.substring(1));
        }

        streamEncoder.close();
        outputStream.close();

        result = outputStream.toByteArray();

        if (removeBom) {
            result = Arrays.copyOfRange(result, bomSize, result.length);
        }



        return result;
    }

}

