package com.magic.utils;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: haribo
 * Date: 10.05.13
 * Time: 17:51
 */
public class VMRuntimeHack {
    private Method trackAllocation = null;
    private Method trackFree = null;

    private static Object runtime = null;

    public boolean trackAlloc(long size) {
        if (runtime == null)
            return false;
        try {
            Object res = trackAllocation.invoke(runtime, Long.valueOf(size));
            return (res instanceof Boolean) ? (Boolean)res : true;
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean trackFree(long size) {
        if (runtime == null)
            return false;
        try {
            Object res = trackFree.invoke(runtime, Long.valueOf(size));
            return (res instanceof Boolean) ? (Boolean)res : true;
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }
    public VMRuntimeHack() {
        boolean success = false;
        try {
            Class cl = Class.forName("dalvik.system.VMRuntime");
            Method getRt = cl.getMethod("getRuntime", new Class[0]);
            runtime = getRt.invoke(null, new Object[0]);
            trackAllocation = cl.getMethod("trackExternalAllocation", new Class[] {long.class});
            trackFree = cl.getMethod("trackExternalFree", new Class[] {long.class});
            success = true;
        } catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
                | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Log.d("VMHack", "54: VMRuntimeHack exception");
        }
        if (!success) {
            Log.i("VMHack", "VMRuntime hack does not work!");
            runtime = null;
            trackAllocation = null;
            trackFree = null;
        }
    }
}