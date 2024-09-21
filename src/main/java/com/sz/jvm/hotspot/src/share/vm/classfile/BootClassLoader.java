package com.sz.jvm.hotspot.src.share.vm.classfile;

import cn.hutool.core.io.FileUtil;
import com.sz.jvm.hotspot.src.share.vm.oops.InstanceKlass;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author
 * @Date 2024-09-07 16:10
 * @Version 1.0
 */
public class BootClassLoader {

    private static InstanceKlass mainKlass = null;

    private static Map<String, InstanceKlass> klassLoaderData = new HashMap<>();

    private static final String searchPath = "E:/WD/diy/JVM2/target/classes/";

    private static final String SUFFIX = ".class";

    public static InstanceKlass loadMainKlass(String className) {
        if(null != mainKlass){
            return mainKlass;
        }
        return loadKlass(className);
    }

    public static void setMainKlass(InstanceKlass mainKlass) {
        BootClassLoader.mainKlass = mainKlass;
    }

    public static InstanceKlass loadKlass(String className) {

        return loadKlass(className, true);
    }


    public static InstanceKlass loadKlass(String name, Boolean resolve) {
        //在缓存空间中去找
        InstanceKlass klass = findLoadedKlass(name);
        if(null != klass){
            return klass;
        }
        klass = readAndParser(name);
        return klass;
    }

    private static InstanceKlass readAndParser(String name) {
        //将全限定类名的.换成硬盘上面的/
        String filename = name.replace('.', '/');
        String filePath = searchPath + filename + SUFFIX;
        byte[] bytes = FileUtil.readBytes(new File(filePath));

        InstanceKlass klass = ClassFileParser.parseClassFile(bytes);

        klassLoaderData.put(name, klass);

        return klass;
    }

    private static InstanceKlass findLoadedKlass(String name) {

        return klassLoaderData.get(name);
    }

    public static InstanceKlass findLoadedClass(String className) {
        return klassLoaderData.get(className);
    }
}
