package com.sz.jvm;

import com.sz.jvm.hotspot.src.share.vm.classfile.BootClassLoader;
import com.sz.jvm.hotspot.src.share.vm.oops.InstanceKlass;
import com.sz.jvm.hotspot.src.share.vm.oops.MethodInfo;
import com.sz.jvm.hotspot.src.share.vm.prims.JavaNativeInterface;
import com.sz.jvm.hotspot.src.share.vm.runtime.JavaThread;
import com.sz.jvm.hotspot.src.share.vm.runtime.Threads;

/**
 * @Author
 * @Date 2024-09-07 17:34
 * @Version 1.0
 *///TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

//    private static final String className = "com.sz.jvm.example.HelloWorld";
    private static final String className = "com.sz.jvm.example.basic.PrintDouble";

    public static void main(String[] args) {
        createVM();
    }

    private static void createVM() {

        //对class文件进行解析，元数据解析封装到klass对象中
        InstanceKlass instanceKlass = BootClassLoader.loadMainKlass(className);

        //找到main方法
        MethodInfo methodInfo = JavaNativeInterface.getMethodID(instanceKlass, "main", "([Ljava/lang/String;)V");

        //创建线程
        JavaThread javaThread = new JavaThread();

        Threads.getThreadList().add(javaThread);
        Threads.setCurrentThread(javaThread);


        //执行main方法
        JavaNativeInterface.callStaticMethod(methodInfo);
    }
}