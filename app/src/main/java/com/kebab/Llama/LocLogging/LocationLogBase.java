package com.kebab.Llama.LocLogging;

import com.kebab.Tuple;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class LocationLogBase {
    public static final byte LOG_TYPE_CELL_CHANGE = (byte) 2;
    public static final byte LOG_TYPE_LLAMA_OFF = (byte) 1;
    public static final byte LOG_TYPE_LLAMA_ON = (byte) 0;
    public long DateTimeMillis = System.currentTimeMillis();
    public byte Type;

    public abstract byte GetTypeId();

    public abstract void LogToBufferInternal(DataOutputStream dataOutputStream) throws IOException;

    public static Tuple<LocationLogBase, Integer> Create(byte[] data, int offset) {
        switch (data[offset]) {
            case (byte) 0:
                return Create(data, offset);
            case (byte) 1:
                return Create(data, offset);
            case (byte) 2:
                return Create(data, offset);
            default:
                return null;
        }
    }

    public void LogToBuffer(DataOutputStream buffer) throws IOException {
        buffer.write(GetTypeId());
        buffer.writeInt(LocationLogging.GetCurrentMillisInDay());
        LogToBufferInternal(buffer);
    }
}
