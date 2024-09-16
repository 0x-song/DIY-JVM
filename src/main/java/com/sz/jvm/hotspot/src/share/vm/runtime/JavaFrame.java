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

    public StackValueCollection getLocals() {
        return locals;
    }

    public void setLocals(StackValueCollection locals) {
        this.locals = locals;
    }

    public StackValueCollection getStack() {
        return stack;
    }

    public void setStack(StackValueCollection stack) {
        this.stack = stack;
    }

    public MethodInfo getOwnerMethod() {
        return ownerMethod;
    }

    public void setOwnerMethod(MethodInfo ownerMethod) {
        this.ownerMethod = ownerMethod;
    }
}
