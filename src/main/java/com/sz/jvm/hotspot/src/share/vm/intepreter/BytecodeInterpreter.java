package com.sz.jvm.hotspot.src.share.vm.intepreter;

import com.sz.jvm.hotspot.src.share.vm.classfile.BootClassLoader;
import com.sz.jvm.hotspot.src.share.vm.classfile.DescriptorStream;
import com.sz.jvm.hotspot.src.share.vm.oops.ConstantPool;
import com.sz.jvm.hotspot.src.share.vm.oops.InstanceKlass;
import com.sz.jvm.hotspot.src.share.vm.oops.MethodInfo;
import com.sz.jvm.hotspot.src.share.vm.prims.JavaNativeInterface;
import com.sz.jvm.hotspot.src.share.vm.runtime.Frame;
import com.sz.jvm.hotspot.src.share.vm.runtime.JavaFrame;
import com.sz.jvm.hotspot.src.share.vm.runtime.JavaThread;
import com.sz.jvm.hotspot.src.share.vm.runtime.StackValue;
import com.sz.jvm.hotspot.src.share.vm.utilities.BasicType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author
 * @Date 2024-09-15 15:58
 * @Version 1.0
 */
public class BytecodeInterpreter {

    private static Logger logger = LoggerFactory.getLogger(BytecodeInterpreter.class);

    //字节码解释器解析字节码指令:
    public static void run(JavaThread currentThread, MethodInfo methodInfo) {
        BytecodeStream code = methodInfo.getAttributes().get(0).getCode();
        //查看最顶端的栈帧
        JavaFrame javaFrame = (JavaFrame) currentThread.getStack().peek();
        int result;
        while (! code.end()){
            result = code.getU1Code();
            System.out.println(result);
            switch (result) {
                case Bytecodes.LDC: {
/*
                    ldc指令是Java虚拟机指令集中的一条指令,它用于将常量值从常量池推送到操作数栈上。
                    ldc指令的功能和特点:
                    ldc指令可以将int、float、String等基本类型和引用类型的常量从常量池加载到操作数栈上。
                    常量池中存储了类加载过程中解析的各种常量,如int值、字符串、类/方法引用等。
                    在执行ldc时,会根据操作数(即常量池索引)来确定加载哪个常量到操作数栈。
                    操作数栈中的值类型数据(int、float等)直接是常量值本身。引用类型数据(String等)是指向常量池中对象的引用。
                    ldc主要用于在方法内部加载一些预定义但在编译阶段无法计算的常量值。
                    Java类文件中记录着ldc指令操作数的 metadata,JVM在执行时根据此识别需要加载的常量。
                    所以总结来说,ldc指令就是从类加载阶段加载好的常量池中,通过索引读取某个常量,并将其值或引用推送到当前方法的操作数栈上。它起到向运行期提供常量值的重要作用。
*/
                    logger.info("执行指令: LDC");
                    int operand = code.getU1Code();
                    byte tag = methodInfo.getBelongKlass().getConstantPool().getTag()[operand];
                    switch (tag){
                        case ConstantPool.JVM_CONSTANT_Float:{
                            break;
                        }
                        case ConstantPool.JVM_CONSTANT_String:{
                            int index = (int) methodInfo.getBelongKlass().getConstantPool().getDataMap().get(operand);
                            String content = (String) methodInfo.getBelongKlass().getConstantPool().getDataMap().get(index);
                            //BasicType.T_OBJECT表示操作数栈或局部变量中是对象引用类型的数据。
                            javaFrame.getStack().push(new StackValue(BasicType.T_OBJECT, content));
                            break;
                        }
                        case ConstantPool.JVM_CONSTANT_Class: {
                            break;
                        }
                        default: {
                            logger.error("未知类型");

                            break;
                        }

                    }

                    break;
                }
                case Bytecodes.RETURN:{
                    logger.info("执行指令: RETURN");
                    //pop出栈
                    currentThread.getStack().pop();
                    logger.info("\t 剩余栈帧数量: " + currentThread.getStack().size());
                    break;
                }
                case Bytecodes.GETSTATIC:{
                    //GETSTATIC指令是Java虚拟机指令集中的一条指令,它用于从类的静态字段中读取字段值,并将该值推送到操作数栈上
                    logger.info("执行指令: GETSTATIC");
                    int operand = code.getUnsignedShort();
                    String className = methodInfo.getBelongKlass().getConstantPool().getClassNameByFieldInfo(operand);
                    String fieldName = methodInfo.getBelongKlass().getConstantPool().getFieldName(operand);
                    Class<?> clazz = null;
                    try {
                        clazz = Class.forName(className.replace('/', '.'));
                        Field field = clazz.getField(fieldName);

                        javaFrame.getStack().push(new StackValue(BasicType.T_OBJECT, field.get(null)));

                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case Bytecodes.INVOKEVIRTUAL:{
/*
                    INVOKEVIRTUAL指令的主要功能和特点如下:
                    操作数指定了需要调用的方法,包括类名、方法名和描述符。
                    在调用前,INVOKEVIRTUAL会先从操作数栈顶弹出一个对象引用(该对象的方法即将被调用)。
                    根据对象的真实类型,JVM在其类及其父类中查找指定的方法实现。
                    方法调用按照面向对象的语义和访问控制规则进行绑定(例如private方法)。
                    方法执行完成后其返回值(如果有)会被推送至操作数栈顶。
                    INVOKEVIRTUAL用于调用对象的实例方法,需要通过具体对象进行绑定和分派。
                    与调用静态方法不同,它体现了面向对象编程的基本语义和方法分派机制。
                    所以INVOKEVIRTUAL指令实现了Java的基本面向对象机制:通过具体的对象调用其相关方法,并在多态的情况下选择最佳方法实现进行调用。它是Java方法调用的一种重要形式。
*/
                    logger.info("执行指令: INVOKEVIRTUAL");
                    int operand = code.getUnsignedShort();
                    String className = methodInfo.getBelongKlass().getConstantPool().getClassNameByMethodInfo(operand);
                    String methodName = methodInfo.getBelongKlass().getConstantPool().getMethodNameByMethodInfo(operand);
                    String descriptorName = methodInfo.getBelongKlass().getConstantPool().getDescriptorNameByMethodInfo(operand);
//                    java/io/PrintStream:::::println:::::(Ljava/lang/String;)V

                    System.out.println(className + ":::::" + methodName + ":::::" + descriptorName);
                    //判断是系统类还是自定义类，如果是系统类，直接调用反射来运行
                    if(className.startsWith("java")){
//                        使用方法描述符解析参数和返回类型
//                        从操作数栈弹出对象
//                        使用反射获取方法并判断是否有返回值
//                        调用方法,将返回值或无返回值推入栈
                        DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
                        descriptorStream.parseMethod();
                        Object[] params = descriptorStream.getParameterValues(javaFrame);
                        Class[] paramClass = descriptorStream.getParamsType();
                        Object object = javaFrame.getStack().pop().getObject();

                        try {
                            Method method = object.getClass().getMethod(methodName, paramClass);
                            //方法没有返回值
                            if(BasicType.T_VOID == descriptorStream.getReturnElement().getType()){
                                method.invoke(object, params);
                            }else {
                                //方法具有返回值
                            }
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }

                    }else {
                        //如果是自定义类，待实现
                    }
                    break;
                }
                //调用静态方法
                case Bytecodes.INVOKESTATIC:{
                    logger.info("执行指令: INVOKESTATIC");
                    int operand = code.getUnsignedShort();
                    String className = methodInfo.getBelongKlass().getConstantPool().getClassNameByMethodInfo(operand);
                    String methodName = methodInfo.getBelongKlass().getConstantPool().getMethodNameByMethodInfo(operand);
                    String descriptorName = methodInfo.getBelongKlass().getConstantPool().getDescriptorNameByMethodInfo(operand);
                    System.out.println(className + ":::::" + methodName + ":::::" + descriptorName);
                    if(className.startsWith("java")){
                        DescriptorStream descriptorStream = new DescriptorStream(descriptorName);
                        descriptorStream.parseMethod();
                        Object[] params = descriptorStream.getParameterValues(javaFrame);
                        Class[] paramClass = descriptorStream.getParamsType();
                        try {
                            Class clazz = Class.forName(className.replace('/','.'));
                            Method method = clazz.getMethod(methodName, paramClass);
                            if(BasicType.T_VOID == descriptorStream.getReturnElement().getType()){
                                method.invoke(clazz, params);
                            }else {
                                descriptorStream.push(method.invoke(clazz, params), javaFrame);
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }else {
                        //自定义的类
                        InstanceKlass klass = BootClassLoader.findLoadedClass(className.replace('/', '.'));
                        if(null == klass){
                            System.out.println("\t 开始加载未加载的类:" + className);
                            klass = BootClassLoader.loadKlass(className.replace('/','.'));
                        }
                        MethodInfo methodID = JavaNativeInterface.getMethodID(klass, methodName, descriptorName);
                        if(null == methodID){
                            throw new Error("不存在的方法: " + methodName + "#" + descriptorName);
                        }

                        // 方法重复调用会出错。因为程序计数器上次执行完指向的是尾部
                        methodID.getAttributes().get(0).getCode().reset();

                        JavaNativeInterface.callStaticMethod(methodID);
                    }

                    break;
                }
                //将常量池中的long、double类型的数据推送至栈顶
                case Bytecodes.LDC2_W:{
                    logger.info("执行指令: LDC2_W");
                    int operand = code.getUnsignedShort();
                    int tag = methodInfo.getBelongKlass().getConstantPool().getTag()[operand];
                    if(ConstantPool.JVM_CONSTANT_Long == tag){
                        long l = (long) methodInfo.getBelongKlass().getConstantPool().getDataMap().get(operand);
                        javaFrame.getStack().push(new StackValue(BasicType.T_LONG, l));
                    }else if(ConstantPool.JVM_CONSTANT_Double == tag){
                        double d = (double) methodInfo.getBelongKlass().getConstantPool().getDataMap().get(operand);
                        //将double压入栈，有些特殊
                        javaFrame.getStack().pushDouble(d);
                    }else {
                        throw new Error("格式不支持");
                    }
                    break;
                }
                //dstore_1是Java虚拟机指令集中的一条指令,
                // 它用于将double类型的数据从操作栈顶弹出,并存储到局部变量表的第二个slot(下标为1)中
                case Bytecodes.DSTORE_1:{
                    logger.info("执行指令: DSTORE_1");
                    StackValue[] values = javaFrame.getStack().popDouble();

                    //存入布局变量表
                    javaFrame.getLocals().add(1, values[1]);
                    javaFrame.getLocals().add(2, values[0]);

                    break;
                }
                //DLOAD_1是Java虚拟机指令集中的一条指令,它用于从局部变量表中加载double类型的变量到操作数栈顶。
                case Bytecodes.DLOAD_1:{
                    logger.info("执行指令: DLOAD_1");
                    StackValue value1 = javaFrame.getLocals().get(1);
                    StackValue value2 = javaFrame.getLocals().get(2);
                    javaFrame.getStack().push(value1);
                    javaFrame.getStack().push(value2);
                    break;
                }
                case Bytecodes.ICONST_0:{
                    logger.info("执行指令: ICONST_0");
                    javaFrame.getStack().push(new StackValue(BasicType.T_INT, 0));
                    break;
                }
                //将整数常量1加载到操作数栈顶
                case Bytecodes.ICONST_1:{
                    logger.info("执行指令: ICONST_1");
                    javaFrame.getStack().push(new StackValue(BasicType.T_INT, 1));
                    break;
                }
                case Bytecodes.ICONST_2:{
                    logger.info("执行指令: ICONST_2");
                    javaFrame.getStack().push(new StackValue(BasicType.T_INT, 2));
                    break;
                }
                case Bytecodes.ICONST_3:{
                    logger.info("执行指令: ICONST_3");
                    javaFrame.getStack().push(new StackValue(BasicType.T_INT, 3));
                    break;
                }
                case Bytecodes.ICONST_4:{
                    logger.info("执行指令: ICONST_4");
                    javaFrame.getStack().push(new StackValue(BasicType.T_INT, 4));
                    break;
                }
                case Bytecodes.ICONST_5:{
                    logger.info("执行指令: ICONST_5");
                    javaFrame.getStack().push(new StackValue(BasicType.T_INT, 5));
                    break;
                }
                //用于将int类型的值从操作数栈顶弹出,并将其存入局部变量表的第一个位置(下标为0)
                case Bytecodes.ISTORE_0:{
                    logger.info("执行指令：ISTORE_0");
                    //取出栈顶元素
                    StackValue stackValue = javaFrame.getStack().pop();

                    //存入局部变量表
                    javaFrame.getLocals().add(0, stackValue);
                    break;
                }
                //它用于将int类型的数据从操作数栈顶弹出,并存储到局部变量表的第二个slot(下标为1)中
                case Bytecodes.ISTORE_1:{
                    logger.info("执行指令：ISTORE_1");
                    //取出栈顶元素
                    StackValue stackValue = javaFrame.getStack().pop();

                    //存入局部变量表
                    javaFrame.getLocals().add(1, stackValue);
                    break;
                }
                case Bytecodes.ISTORE_2: {
                    logger.info("执行指令: ISTORE_2");

                    // 取出栈顶元素
                    StackValue value = javaFrame.getStack().pop();

                    // 存入局部变量表
                    javaFrame.getLocals().add(2, value);

                    break;
                }
                case Bytecodes.ISTORE_3: {
                    logger.info("执行指令: ISTORE_3");

                    // 取出栈顶元素
                    StackValue value = javaFrame.getStack().pop();

                    // 存入局部变量表
                    javaFrame.getLocals().add(3, value);

                    break;
                }
                //它用于将局部变量表中下标为0的int变量加载到操作数栈顶。
                case Bytecodes.ILOAD_0:{
                    // 取出局部变量表的数据
                    StackValue value = javaFrame.getLocals().get(0);

                    // 压入栈
                    javaFrame.getStack().push(value);

                    break;
                }
                //它用于将局部变量表中下标为1的int变量加载到操作数栈顶。
                case Bytecodes.ILOAD_1:{
                    // 取出局部变量表的数据
                    StackValue value = javaFrame.getLocals().get(1);

                    // 压入栈
                    javaFrame.getStack().push(value);

                    break;
                }
                case Bytecodes.IADD:{

                    StackValue value1 = javaFrame.getStack().pop();
                    StackValue value2 = javaFrame.getStack().pop();
                    if (value1.getType() != BasicType.T_INT || value2.getType() != BasicType.T_INT) {
                        System.out.println("不匹配的数据类型");

                        throw new Error("不匹配的数据类型");
                    }

                    // 运算
                    int ret = (int)value1.getData() + (int)value2.getData();
                    System.out.println("执行指令: IADD，运行结果: " + ret);
                    // 压入栈
                    javaFrame.getStack().push(new StackValue(BasicType.T_INT, ret));


                    break;
                }
                default:{
                    throw new Error("无效指令");
                }
            }
        }
    }
}
