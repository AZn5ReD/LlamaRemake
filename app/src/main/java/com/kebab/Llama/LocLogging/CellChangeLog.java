package com.kebab.Llama.LocLogging;

import com.kebab.Llama.Cell;
import java.io.DataOutputStream;
import java.io.IOException;

public class CellChangeLog extends LocationLogBase {
    Cell _Cell;

    public CellChangeLog(Cell cell) {
        this._Cell = cell;
    }

    public byte GetTypeId() {
        return (byte) 2;
    }

    public void LogToBufferInternal(DataOutputStream buffer) throws IOException {
        buffer.writeInt(this._Cell.CellId);
        buffer.writeShort(this._Cell.Mcc);
        buffer.writeShort(this._Cell.Mnc);
    }
}
