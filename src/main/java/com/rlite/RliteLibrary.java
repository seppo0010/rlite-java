package com.rlite;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface RliteLibrary extends Library {
    public class Reply extends Structure {
        public static final int STRING = 1;
        public static final int ARRAY = 2;
        public static final int INTEGER = 3;
        public static final int NIL = 4;
        public static final int STATUS = 5;
        public static final int ERROR = 6;

        public int type;
        public long integer;
        public int len;
        public Pointer str;
        public long elements;
        public Pointer element;

        public Reply() {
            super();
        }

        public Reply(Pointer pointer) {
            super();
            useMemory(pointer);
            read();
        }

        protected List<?> getFieldOrder() {
            return Arrays.asList("type", "integer", "len", "str", "elements", "element");
        }

        public static class ByReference extends Reply implements Structure.ByReference {};
        public static class ByValue extends Reply implements Structure.ByValue {};
    }
    RliteLibrary INSTANCE = (RliteLibrary)
        Native.loadLibrary("hirlite", RliteLibrary.class);

    Pointer rliteConnect(String ip, int port);
    Reply.ByReference rliteCommandArgv(Pointer c, int argc, Pointer argv, Pointer argvlen);
    void rliteFreeReplyObject(Pointer c);
    void rliteFree(Pointer c);
}
