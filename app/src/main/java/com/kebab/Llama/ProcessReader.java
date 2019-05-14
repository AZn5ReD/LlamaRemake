package com.kebab.Llama;

import java.io.DataInputStream;
import java.io.IOException;

public class ProcessReader {
    Process _Process;
    Thread _ReadTopCommandThread;
    LlamaService _Service;
    boolean _Stopping;
    Runnable _ThreadMethod = new Runnable() {
        public void run() {
            try {
                ProcessReader.this._Process = Runtime.getRuntime().exec("top -d 2");
                DataInputStream os = new DataInputStream(ProcessReader.this._Process.getInputStream());
                int count = 0;
                int lines = 0;
                while (true) {
                    String s = os.readUTF();
                    count++;
                    lines++;
                    if (count > 0) {
                        Logging.Report("ProcessReader", lines + " -- " + s.length(), ProcessReader.this._Service);
                        count = 0;
                    }
                }
            } catch (IOException e) {
                Logging.Report(e, ProcessReader.this._Service);
            }
        }
    };

    public ProcessReader(LlamaService service) {
        this._Service = service;
        this._ReadTopCommandThread = new Thread(this._ThreadMethod);
        this._ReadTopCommandThread.setPriority(1);
    }

    public void Stop() {
        if (this._Process != null) {
            this._Process.destroy();
        }
    }
}
