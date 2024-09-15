package com.sz.jvm.hotspot.src.share.vm.prims;

import com.sz.jvm.hotspot.src.share.vm.intepreter.BytecodeInterpreter;
import com.sz.jvm.hotspot.src.share.vm.oops.CodeAttributeInfo;
import com.sz.jvm.hotspot.src.share.vm.oops.InstanceKlass;
import com.sz.jvm.hotspot.src.share.vm.oops.MethodInfo;
import com.sz.jvm.hotspot.src.share.vm.runtime.JavaFrame;
import com.sz.jvm.hotspot.src.share.vm.runtime.JavaThread;
import com.sz.jvm.hotspot.src.share.vm.runtime.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author
 * @Date 2024-09-15 12:23
 * @Version 1.0
 */
public class JavaNativeInterface {


    private final static Logger logger = LoggerFactory.getLogger(JavaNativeInterface.class);

    /**
     *
     * @param klass 类元数据封装体
     * @param name 方法名称
     * @param descriptorName 方法描述符
     * @return
     */
    public static MethodInfo getMethodID(InstanceKlass klass, String name, String descriptorName) {
        List<MethodInfo> methodInfos = klass.getMethodInfos();
        for (MethodInfo methodInfo : methodInfos) {
            String tmpName = (String) klass.getConstantPool().getDataMap().get(methodInfo.getNameIndex());
            String tmpDescriptor = (String) klass.getConstantPool().getDataMap().get(methodInfo.getDescriptorIndex());
            if (tmpName.equals(name) && tmpDescriptor.equals(descriptorName)) {
                logger.info("找到了方法: " + name + "#" + descriptorName);
                return methodInfo;
            }
        }
        logger.error("没有找到方法: " + name + "#" + descriptorName);
        return null;
    }

    public static void callStaticMethod(MethodInfo methodInfo) {
        JavaThread currentThread = Threads.getCurrentThread();
        if (!methodInfo.getAccessFlags().isStatic()) {
            throw new Error("只能调用静态方法");
        }
        CodeAttributeInfo codeAttributeInfo = methodInfo.getAttributes().get(0);
        //创建栈帧
        JavaFrame javaFrame = new JavaFrame(codeAttributeInfo.getMaxLocals(), methodInfo);

        currentThread.getStack().push(javaFrame);

        //字节码解释器
        BytecodeInterpreter.run(currentThread, methodInfo);


    }
}
