package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.Frames.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StompEncoderDecoderImpl implements 
                MessageEncoderDecoder<StompFrame> {
    
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private FrameUtil util = new FrameUtil();

    @Override
    public StompFrame decodeNextByte(byte nextByte) {
        if (nextByte == '\u0000') {
            return popString();
        }

        pushByte(nextByte);
        return null;
    }

    @Override
    public byte[] encode(StompFrame message) {
        String strMessage = message.toString();
        byte[] messageInBytes = strMessage.getBytes(StandardCharsets.UTF_8);
        return messageInBytes;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private StompFrame popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return util.buildStompFrame(result);    
    }
    
}
