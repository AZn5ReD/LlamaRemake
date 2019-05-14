package com.kebab.Llama.LocLogging;

import java.io.DataOutputStream;

public class LlamaOnLog extends LocationLogBase {
    public byte GetTypeId() {
        return (byte) 0;
    }

    public void LogToBufferInternal(DataOutputStream buffer) {
    }
}
