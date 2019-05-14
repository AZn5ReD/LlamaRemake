package com.kebab.Llama.LocLogging;

import java.io.DataOutputStream;

public class LlamaOffLog extends LocationLogBase {
    public byte GetTypeId() {
        return (byte) 1;
    }

    public void LogToBufferInternal(DataOutputStream buffer) {
    }
}
