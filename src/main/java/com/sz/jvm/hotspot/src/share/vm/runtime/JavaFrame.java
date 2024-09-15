package com.sz.jvm.hotspot.src.share.vm.runtime;

import com.sz.jvm.hotspot.src.share.vm.oops.MethodInfo;

/**
 * @Author
 * @Date 2024-09-15 15:33
 * @Version 1.0
 */
public class JavaFrame extends Frame{


    private StackValueCollection locals;

    private StackValueCollection stack = new StackValueCollection();

    private MethodInfo ownerMethod;

    public JavaFrame(int maxLocals) {
        locals = new StackValueCollection(maxLocals);
    }

    public JavaFrame(int maxLocals, MethodInfo methodInfo) {
        locals = new StackValueCollection(maxLocals);

        ownerMethod = methodInfo;
    }
}