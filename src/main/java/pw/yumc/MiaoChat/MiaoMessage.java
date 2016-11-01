package pw.yumc.MiaoChat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * Created on 16-9-8.
 */
public class MiaoMessage {

    public static final String CHANNEL = "MiaoChat";
    private String json;

    private MiaoMessage(String json) {
        this.json = json;
    }

    public static byte[] encode(String in) {
        return new MiaoMessage(in).encode();
    }

    public static MiaoMessage decode(byte[] in) {
        final ByteArrayDataInput input = ByteStreams.newDataInput(in);
        return new MiaoMessage(input.readUTF());
    }

    public String getJson() {
        return json;
    }

    public byte[] encode() {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(json);
        return out.toByteArray();
    }
}