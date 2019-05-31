package pw.yumc.MiaoChat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.SneakyThrows;

/**
 * Created on 16-9-8.
 * @author MiaoWoo
 */
public class MiaoMessage {

    public static final String CHANNEL = "MiaoChat:Default".toLowerCase();
    public static final String NORMAL_CHANNEL = "MiaoChat:Normal".toLowerCase();
    private static final int MAX_MESSAGE_LENGTH = 32000;
    private String json;

    private MiaoMessage(String json) {
        this.json = json;
    }

    public static byte[] encode(String in) {
        return new MiaoMessage(in).encode();
    }

    @SneakyThrows
    public static MiaoMessage decode(byte[] in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(new GZIPInputStream(new ByteArrayInputStream(in)), baos);
        return new MiaoMessage(baos.toString("UTF-8"));
    }

    public String getJson() {
        return json;
    }

    @SneakyThrows
    public byte[] encode() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), new GZIPOutputStream(baos));
        if (baos.size() > MAX_MESSAGE_LENGTH) { return null; }
        return baos.toByteArray();
    }

    @SneakyThrows
    private static void copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[1024];
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
        input.close();
        output.close();
    }
}