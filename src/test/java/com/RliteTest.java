package com.sun.jna.examples;

import java.io.UnsupportedEncodingException;
import java.lang.System;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rlite.RliteClient;
import com.rlite.exceptions.RliteException;

/** Simple example of JNA interface mapping and usage. */
public class RliteTest {
    RliteClient c;

    @Before
    public void initialize() {
        c = new RliteClient();
    }

    @Test
    public void statusTest() throws UnsupportedEncodingException {
        String argv[] = {"SET", "key", "value"};
        String reply = (String)c.command(argv);
        Assert.assertEquals("OK", reply);
    }

    @Test
    public void stringTest() throws UnsupportedEncodingException {
        String argv[] = {"PING", "test"};
        String reply = (String)c.command(argv);
        Assert.assertEquals("test", reply);
    }

    @Test(expected=RliteException.class)
    public void errorTest() throws UnsupportedEncodingException {
        String argv[] = {"SET", "key"};
        c.command(argv);
    }

    @Test
    public void integerTest() throws UnsupportedEncodingException {
        String argv[] = {"RPUSH", "key", "1", "2"};
        Long reply = (Long)c.command(argv);
        Assert.assertEquals(reply, new Long(2));
    }

    @Test
    public void nilTest() throws UnsupportedEncodingException {
        String argv[] = {"GET", "key"};
        Object reply = c.command(argv);
        Assert.assertEquals(reply, null);
    }

    @Test
    public void arrayTest() throws UnsupportedEncodingException {
        String argv[] = {"MSET", "key", "value", "key2", "value2"};
        c.command(argv);
        String argv2[] = {"MGET", "key", "key2", "key3"};
        Object reply[] = (Object[])c.command(argv2);
        Assert.assertEquals(reply.length, 3);
        Assert.assertEquals(reply[0], "value");
        Assert.assertEquals(reply[1], "value2");
        Assert.assertEquals(reply[2], null);
    }

    @Test
    public void binaryTest() throws UnsupportedEncodingException {
        byte b[] = {'b', 0, 'a'};
        byte argv[][] = {"SET".getBytes(), "key".getBytes(), b};
        c.command(argv);
        String argv2[] = {"GET", "key"};
        byte reply[] = (byte[])c.command(argv2, true);
        Assert.assertEquals(reply.length, b.length);
        Assert.assertEquals(reply[0], b[0]);
        Assert.assertEquals(reply[1], b[1]);
        Assert.assertEquals(reply[2], b[2]);
        Assert.assertEquals(reply[1], 0);
    }

    private void runSetGet() throws UnsupportedEncodingException {
        c = new RliteClient("mydb.rld");
        String argv0[] = {"SET", "key", "value"};
        c.command(argv0);
        c.close();

        c = new RliteClient("mydb.rld");
        String argv[] = {"GET", "key"};
        String reply = (String)c.command(argv);
        Assert.assertEquals("value", reply);
        c.close();
    }

    @Test
    public void persistenceTest() throws UnsupportedEncodingException {
        runSetGet();

        System.gc();
        System.gc();
        System.gc();
        System.gc();

        runSetGet();

        System.gc();
        System.gc();
        System.gc();
        System.gc();

        runSetGet();
    }
}
