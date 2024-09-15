package com.sz.jvm.hotspot.src.share.vm.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author
 * @Date 2024-09-15 12:32
 * @Version 1.0
 */
public class Threads {

    private static List<Thread> threadList;

    private static Thread currentThread;

    static {
        threadList = new ArrayList<Thread>();
    }

    public static List<Thread> getThreadList() {
        return threadList;
    }

    public static void setThreadList(List<Thread> threadList) {
        Threads.threadList = threadList;
    }

    public static JavaThread getCurrentThread() {
        return (JavaThread) currentThread;
    }

    public static void setCurrentThread(Thread currentThread) {
        if(!(currentThread instanceof JavaThread)){
            throw new RuntimeException("Current thread is not a JavaThread");
        }
        Threads.currentThread = currentThread;
    }
}
