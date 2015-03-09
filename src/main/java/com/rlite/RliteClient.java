package com.rlite;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

import com.rlite.exceptions.RliteException;

public class RliteClient {
    String encoding = "UTF-8";
    Pointer c;

    public RliteClient() {
        this(":memory:");
    }

    public RliteClient(String file) {
        c = RliteLibrary.INSTANCE.rliteConnect(file, 0);
    }

    protected Object replyToObject(RliteLibrary.Reply reply, boolean binary) {
        if (reply.type == RliteLibrary.Reply.STATUS || reply.type == RliteLibrary.Reply.STRING || reply.type == RliteLibrary.Reply.ERROR) {
            int len = reply.len;
            byte str[] = new byte[len];
            reply.str.read(0, str, 0, len);
            if (reply.type == RliteLibrary.Reply.ERROR) {
                try {
                    throw new RliteException(new String(str, 0, len, encoding));
                } catch (UnsupportedEncodingException e) {
                    throw new RliteException("Problem formatting error");
                }
            }
            if (binary) {
                return str;
            } else {
                try {
                    return new String(str, 0, len, encoding);
                } catch (UnsupportedEncodingException e) {
                    throw new RliteException(e);
                }
            }
        } else  if (reply.type == RliteLibrary.Reply.INTEGER) {
            return new Long(reply.integer);
        } else  if (reply.type == RliteLibrary.Reply.NIL) {
            return null;
        } else  if (reply.type == RliteLibrary.Reply.ARRAY) {
            ArrayList<Object> objects = new ArrayList<Object>();
            long i;
            for (i = 0; i < reply.elements; i++) {
                Pointer el_p = reply.element.getPointer(i * Pointer.SIZE);
                RliteLibrary.Reply el = new RliteLibrary.Reply(el_p);
                objects.add(replyToObject(el, binary));
            }
            return objects.toArray();
        }
        throw new RliteException(String.format("Unexpected reply type %d", reply.type));
    }

    public Object command(byte argv[][]) {
        return command(argv, false);
    }

    public Object command(byte pargv[][], boolean binary) {
        Memory argv = new Memory(Pointer.SIZE * pargv.length);
        Memory argvlen = new Memory(NativeLong.SIZE * pargv.length);

        int pos = 0;
        for (byte arg[] : pargv) {
            Memory e = new Memory(arg.length);
            e.write(0, arg, 0, arg.length);
            argv.setPointer(pos * Memory.SIZE, e);
            argvlen.setNativeLong(pos * NativeLong.SIZE, new NativeLong(arg.length));
            pos++;
        }

        RliteLibrary.Reply reply = null;
        try {
            reply = RliteLibrary.INSTANCE.rliteCommandArgv(c, pargv.length, argv, argvlen);
            return replyToObject(reply, binary);
        } finally {
            if (reply != null) {
                RliteLibrary.INSTANCE.rliteFreeReplyObject(reply.getPointer());
            }
        }
    }

    public Object command(String str_argv[]) throws UnsupportedEncodingException {
        return command(str_argv, false);
    }

    public Object command(String str_argv[], boolean binary) throws UnsupportedEncodingException {
        ArrayList<byte[]> argv = new ArrayList<byte[]>();
        for (String arg : str_argv) {
            argv.add(arg.getBytes(encoding));
        }
        return command(argv.toArray(new byte[0][0]), binary);
    }

    protected void finalize() {
        RliteLibrary.INSTANCE.rliteFree(c);
    }
}
