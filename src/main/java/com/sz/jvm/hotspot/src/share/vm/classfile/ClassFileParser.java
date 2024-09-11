package com.sz.jvm.hotspot.src.share.vm.classfile;

import com.sz.jvm.hotspot.src.share.vm.intepreter.BytecodeStream;
import com.sz.jvm.hotspot.src.share.vm.oops.*;
import com.sz.jvm.hotspot.src.share.vm.tools.DataConverter;
import com.sz.jvm.hotspot.src.share.vm.tools.JVMConstant;
import com.sz.jvm.hotspot.src.share.vm.tools.Stream;
import com.sz.jvm.hotspot.src.share.vm.utilities.AccessFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author
 * @Date 2024-09-07 16:56
 * @Version 1.0
 */
public class ClassFileParser {

    private static Logger logger = LoggerFactory.getLogger(ClassFileParser.class);

    //    magic 4字节
//    minor version 2字节
//    major version 2字节
//    constant pool count 2字节
//    constant pool 待定
//    access flag 2字节
//    this_class类名 2字节
//    super_class父类 2字节
//    interfaces_count 2字节
//    interfaces[] 待定
//    fields_count 2字节
//    fields_info 待定
//    methods_count方法数量 2字节
//    methods_info 待定
//    attributes_count 2字节
//    attributes[] 待定
    public static InstanceKlass parseClassFile(byte[] bytes) {
        int index = 0;
        InstanceKlass klass = new InstanceKlass();

        //读取magic
        Stream.readBytes(bytes, index, JVMConstant.MAGIC, klass.getMagic());
        index += JVMConstant.MAGIC;

        //读取次版本号
        Stream.readBytes(bytes, index, JVMConstant.MINOR_VERSION, klass.getMinorVersion());
        index += JVMConstant.MINOR_VERSION;

        //读取主版本号
        Stream.readBytes(bytes, index, JVMConstant.MAJOR_VERSION, klass.getMajorVersion());
        index += JVMConstant.MAJOR_VERSION;

        //读取常量池
        Stream.readBytes(bytes, index, JVMConstant.CONSTANT_POOL_COUNT, klass.getConstantPoolCount());
        index += JVMConstant.CONSTANT_POOL_COUNT;

        //常量池的长度不固定
        klass.getConstantPool().setLength(DataConverter.byteToInt(klass.getConstantPoolCount()));

        klass.getConstantPool().init();

        //解析常量池
       // index ++;
        index = parseConstantPool(bytes, klass, index);

        byte[] b2arr;
        // 类的访问权限 2B
        b2arr = Stream.readBytes(bytes, index, 2);
        index += 2;

        klass.setAccessFlag(DataConverter.byteToInt(b2arr));

        // 类名 2B
        b2arr = Stream.readBytes(bytes, index, 2);
        index += 2;
        klass.setThisClass(DataConverter.byteToInt(b2arr));

        // 父类名 2B
        b2arr = Stream.readBytes(bytes, index, 2);
        index += 2;
        klass.setSuperClass(DataConverter.byteToInt(b2arr));

        // 实现的接口个数 2B
        b2arr = Stream.readBytes(bytes, index, 2);
        index += 2;
        klass.setInterfacesLength(DataConverter.byteToInt(b2arr));

        //实现的接口
        if(0 != klass.getInterfacesLength()) {
            logger.info("开始解析实现的接口信息: ");
            index = parseInterface(bytes, klass, index);
        }

        // 成员变量数量 2B
        b2arr = Stream.readBytes(bytes, index, 2);
        index += 2;
        klass.setFieldsLength(DataConverter.byteToInt(b2arr));

        // 成员变量
        index = parseFields(bytes, klass, index);


        // 方法数量 2B
        b2arr = Stream.readBytes(bytes, index, 2);
        index += 2;
        klass.setMethodLength(DataConverter.byteToInt(b2arr));

        //解析方法
        index = parseMethods(bytes, klass, index);



        return null;
    }

    private static int parseMethods(byte[] bytes, InstanceKlass klass, int index) {
        byte[] b2arr;
        byte[] b4arr;
        for (int i = 0; i < klass.getMethodLength(); i++) {
            MethodInfo methodInfo = new MethodInfo();

            methodInfo.setBelongKlass(klass);

            klass.getMethodInfos().add(methodInfo);

            //access flag
            b2arr = Stream.readBytes(bytes, index, 2);
            index += 2;
            methodInfo.setAccessFlags(new AccessFlags(DataConverter.byteToInt(b2arr)));

            // name index
            b2arr = Stream.readBytes(bytes, index, 2);
            index += 2;
            methodInfo.setNameIndex(DataConverter.byteToInt(b2arr));
            methodInfo.setMethodName((String) methodInfo.getBelongKlass().getConstantPool().getDataMap().get(methodInfo.getNameIndex()));

            logger.info("解析方法: " + methodInfo.getMethodName());

            // descriptor index
            b2arr = Stream.readBytes(bytes, index, 2);
            index += 2;

            methodInfo.setDescriptorIndex(DataConverter.byteToInt(b2arr));

            // attribute count
            b2arr = Stream.readBytes(bytes, index, 2);
            index += 2;
            methodInfo.setAttributesCount(DataConverter.byteToInt(b2arr));

            logger.info("\t第 " + i + " 个方法: access flag: " + methodInfo.getAccessFlags()
                    + ", name index: " + methodInfo.getNameIndex()
                    + ", descriptor index: " + methodInfo.getDescriptorIndex()
                    + ", attribute count: " + methodInfo.getAttributesCount()
            );

            // 解析方法属性
            if (1 != methodInfo.getAttributesCount()) {
                throw new Error("方法的属性不止一个");
            }

            for (int j = 0; j < methodInfo.getAttributesCount(); j++) {
                CodeAttributeInfo attributeInfo = new CodeAttributeInfo();

                methodInfo.getAttributes().add(attributeInfo);

                // attr name index
                b2arr = Stream.readBytes(bytes, index, 2);
                index += 2;

                attributeInfo.setAttrNameIndex(DataConverter.byteToInt(b2arr));

                // attr length
                b4arr = Stream.readBytes(bytes, index, 4);
                index += 4;

                attributeInfo.setAttrLength(DataConverter.byteToInt(b4arr));

                // max stack
                b2arr = Stream.readBytes(bytes, index, 2);
                index += 2;

                attributeInfo.setMaxStack(DataConverter.byteToInt(b2arr));

                // max locals
                b2arr = Stream.readBytes(bytes, index, 2);
                index += 2;

                attributeInfo.setMaxLocals(DataConverter.byteToInt(b2arr));

                // code length
                b4arr = Stream.readBytes(bytes, index, 4);
                index += 4;

                attributeInfo.setCodeLength(DataConverter.byteToInt(b4arr));

                // code
                BytecodeStream bytecodeStream = new BytecodeStream(methodInfo, attributeInfo);
                attributeInfo.setCode(bytecodeStream);

                Stream.readBytes(bytes, index, attributeInfo.getCodeLength(), bytecodeStream.getCodes());
                index += attributeInfo.getCodeLength();

                logger.info("\t\t第 " + j + " 个属性: access flag: " + methodInfo.getAccessFlags()
                        + ", name index: " + attributeInfo.getAttrNameIndex()
                        + ", stack: " + attributeInfo.getMaxStack()
                        + ", locals: " + attributeInfo.getMaxLocals()
                        + ", code len: " + attributeInfo.getCodeLength()
                );

                // exception table length
                b2arr = Stream.readBytes(bytes, index, 2);
                index += 2;

                attributeInfo.setExceptionTableLength(DataConverter.byteToInt(b2arr));

                // attributes count
                b2arr = Stream.readBytes(bytes, index, 2);
                index += 2;

                attributeInfo.setAttributesCount(DataConverter.byteToInt(b2arr));

                for (int k = 0; k < attributeInfo.getAttributesCount(); k++) {
                    // attr name index
                    b2arr = Stream.readBytes(bytes, index, 2);

                    String attrName = (String) klass.getConstantPool().getDataMap().get(DataConverter.byteToInt(b2arr));
                    if (attrName.equals("LineNumberTable")) {
                        index = parseLineNumberTable(bytes, index, attrName, attributeInfo);
                    } else if (attrName.equals("LocalVariableTable")) {
                        index = parseLocalVariableTable(bytes, index, attrName, attributeInfo);
                    }
                }
            }
            // 判断是不是main函数
            String methodName = (String) klass.getConstantPool().getDataMap().get(methodInfo.getNameIndex());
            String descriptorName = (String) klass.getConstantPool().getDataMap().get(methodInfo.getDescriptorIndex());
            if (methodName.equals("main") && descriptorName.equals("([Ljava/lang/String;)V")) {
                logger.info("定位到main函数所在类");

                BootClassLoader.setMainKlass(klass);
            }

        }
        return index;
    }

    private static int parseLocalVariableTable(byte[] content, int index, String attrName, CodeAttributeInfo attributeInfo) {
        byte[] u2Arr = new byte[2];
        byte[] u4Arr = new byte[4];

        LocalVariableTable localVariableTable = new LocalVariableTable();

        attributeInfo.getAttributes().put(attrName, localVariableTable);

        // attr name index
        u2Arr = Stream.readBytes(content, index, 2);
        index += 2;

        localVariableTable.setAttrNameIndex(DataConverter.byteToInt(u2Arr));

        // attr len
        u4Arr = Stream.readBytes(content, index, 4);
        index += 4;

        localVariableTable.setAttrLength(DataConverter.byteArrayToInt(u4Arr));

        // table length
        u2Arr = Stream.readBytes(content, index, 2);
        index += 2;

        localVariableTable.setTableLength(DataConverter.byteToInt(u2Arr));

        localVariableTable.initTable();

        logger.info("\t\t\t localVariableTable: "
                + ", name index: " + localVariableTable.getAttrNameIndex()
                + ", attr len: " + localVariableTable.getAttrLength()
                + ", table len: " + localVariableTable.getTableLength()
        );

        if (0 == localVariableTable.getTableLength()) {
            return index;
        }

        // table
        for (int i = 0; i < localVariableTable.getTableLength(); i++) {
            LocalVariableTable.Item item = localVariableTable.new Item();

            localVariableTable.getTable()[i] = item;

            // start pc
            u2Arr = Stream.readBytes(content, index, 2);
            index += 2;

            item.setStartPc(DataConverter.byteToInt(u2Arr));

            // length
            u2Arr = Stream.readBytes(content, index, 2);
            index += 2;

            item.setLength(DataConverter.byteToInt(u2Arr));

            // name index
            u2Arr = Stream.readBytes(content, index, 2);
            index += 2;

            item.setNameIndex(DataConverter.byteToInt(u2Arr));

            // descriptor index
            u2Arr = Stream.readBytes(content, index, 2);
            index += 2;

            item.setDescriptorIndex(DataConverter.byteToInt(u2Arr));

            //index
            u2Arr = Stream.readBytes(content, index, 2);
            index += 2;

            item.setIndex(DataConverter.byteToInt(u2Arr));

            logger.info("\t\t\t\t第 " + i + " 个属性: "
                    + ", start pc: " + item.getStartPc()
                    + ", length: " + item.getLength()
                    + ", name index: " + item.getNameIndex()
                    + ", descriptor index: " + item.getDescriptorIndex()
                    + ", index: " + item.getIndex()
            );
        }

        return index;
    }

    private static int parseLineNumberTable(byte[] content, int index, String attrName, CodeAttributeInfo attributeInfo) {

        byte[] u2Arr = new byte[2];
        byte[] u4Arr = new byte[4];

        LineNumberTable lineNumberTable = new LineNumberTable();

        attributeInfo.getAttributes().put(attrName, lineNumberTable);

        // attr name index
        u2Arr = Stream.readBytes(content, index, 2);
        index += 2;

        lineNumberTable.setAttrNameIndex(DataConverter.byteToInt(u2Arr));

        // attr len
        u4Arr = Stream.readBytes(content, index, 4);
        index += 4;

        lineNumberTable.setAttrLength(DataConverter.byteArrayToInt(u4Arr));

        // table length
        u2Arr = Stream.readBytes(content, index, 2);
        index += 2;

        lineNumberTable.setTableLength(DataConverter.byteToInt(u2Arr));

        lineNumberTable.initTable();

        logger.info("\t\t\t lineNumberTable: "
                + ", name index: " + lineNumberTable.getAttrNameIndex()
                + ", attr len: " + lineNumberTable.getAttrLength()
                + ", table len: " + lineNumberTable.getTableLength()
        );

        // table
        if (0 != lineNumberTable.getTableLength()) {
            for (int l = 0; l < lineNumberTable.getTableLength(); l++) {
                LineNumberTable.Item item = lineNumberTable.new Item();

                lineNumberTable.getTable()[l] = item;

                // start pc
                u2Arr = Stream.readBytes(content, index, 2);
                index += 2;

                item.setStartPc(DataConverter.byteToInt(u2Arr));

                // line number
                u2Arr = Stream.readBytes(content, index, 2);
                index += 2;

                item.setLineNumber(DataConverter.byteToInt(u2Arr));

                logger.info("\t\t\t\t第 " + l + " 个属性: "
                        + ", start pc: " + item.getStartPc()
                        + ", line number: " + item.getLineNumber()
                );
            }
        }

        return index;
    }

    private static int parseFields(byte[] bytes, InstanceKlass klass, int index) {
        logger.info("解析属性:");
        byte[] b2arr;
        for (int i = 0; i < klass.getFieldsLength(); i++) {
            FieldInfo fieldInfo = new FieldInfo();
            klass.getFieldInfos().add(fieldInfo);

            // access flag
            b2arr = Stream.readBytes(bytes, index, 2);
            index += 2;
            fieldInfo.setAccessFlags(DataConverter.byteToInt(b2arr));

            // name index
            b2arr = Stream.readBytes(bytes, index, 2);
            index += 2;
            fieldInfo.setNameIndex(DataConverter.byteToInt(b2arr));

            // descriptor index
            b2arr = Stream.readBytes(bytes, index, 2);
            index += 2;
            fieldInfo.setDescriptorIndex(DataConverter.byteToInt(b2arr));

            // attribute count
            b2arr = Stream.readBytes(bytes, index, 2);
            index += 2;
            fieldInfo.setAttributesCount(DataConverter.byteToInt(b2arr));

            if (0 != fieldInfo.getAttributesCount()) {
                throw new Error("属性的attribute count != 0");
            }

            logger.info("\t第 " + i + " 个属性: access flag: " + fieldInfo.getAccessFlags()
                    + ", name index: " + fieldInfo.getNameIndex()
                    + ", descriptor index: " + fieldInfo.getDescriptorIndex()
                    + ", attribute count: " + fieldInfo.getAttributesCount()
            );

        }
        return index;
    }

    private static int parseInterface(byte[] bytes, InstanceKlass klass, int index) {
        byte[] b2arr;
        for (int i = 0; i < klass.getInterfacesLength(); i++) {
            b2arr = Stream.readBytes(bytes, index, 2);
            index += 2;
            int value = DataConverter.byteToInt(b2arr);
            String name = klass.getConstantPool().getInterfaceName(value);
            InterfaceInfo interfaceInfo = new InterfaceInfo(value, name);
            klass.getInterfaceInfos().add(interfaceInfo);
            logger.info("\t 第 " + (i + 1) + " 个接口: " + name);
        }
        return index;
    }

    private static int parseConstantPool(byte[] bytes, InstanceKlass klass, int index) {
        logger.info("开始解析常量池");
        //读取一个字节，tag的值，确认是属于何种常量

        byte[] b2arr = new byte[2];
        byte[] b4arr = new byte[4];
        byte[] b8arr = new byte[8];


        for (int i = 1; i < klass.getConstantPool().getLength(); i++) {
            int tag = Stream.readBytes(bytes, index, JVMConstant.CONSTANT_POOL_TAG)[0];
            System.out.println(tag);
            index ++;

            switch (tag){
                case ConstantPool.JVM_CONSTANT_Utf8:
                    //用俩字节标识字符串的长度
                    //随后再向后取指定长度字节便是字符串的数据内容
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Utf8;

                    b2arr = Stream.readBytes(bytes, index, 2);
                    index += 2;
                    int strLength = DataConverter.byteToInt(b2arr);
                    byte[] strContent = Stream.readBytes(bytes, index, strLength);
                    index += strLength;
                    String s = new String(strContent);
                    klass.getConstantPool().getDataMap().put(i, s);
                    logger.info("第" + i + "个常量池为utf-8字符串:" + s);
                    break;

                case ConstantPool.JVM_CONSTANT_Integer:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Integer;

//                    throw new Error("程序未做处理");
                    //break;

                case ConstantPool.JVM_CONSTANT_Float:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Float;

                    b4arr = Stream.readBytes(bytes, index, 4);
                    index += 4;

                    klass.getConstantPool().getDataMap().put(i, DataConverter.byteToFloat(b4arr));

                    logger.info("\t第 " + i+ " 个: 类型: Float，值: " + klass.getConstantPool().getDataMap().get(i));
                    break;

                case ConstantPool.JVM_CONSTANT_Long:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Long;

//                    throw new Error("程序未做处理");
//                    break;

                case ConstantPool.JVM_CONSTANT_Double:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Double;

                    b8arr = Stream.readBytes(bytes, index, 8);
                    index += 8;

                    klass.getConstantPool().getDataMap().put(i, DataConverter.byteToDouble(b8arr, false));
                    logger.info("\t第 " + i+ " 个: 类型: Double，值: " + klass.getConstantPool().getDataMap().get(i));
                    /**
                     *  因为一个double在常量池中需要两个成员项目来存储
                     *  所以需要处理
                     */
                    klass.getConstantPool().getTag()[++i] = ConstantPool.JVM_CONSTANT_Double;

                    klass.getConstantPool().getDataMap().put(i, DataConverter.byteToDouble(b8arr, false));

                    logger.info("\t第 " + i+ " 个: 类型: Double，值: " + klass.getConstantPool().getDataMap().get(i));
                    break;

                case ConstantPool.JVM_CONSTANT_Class:
                    //指向全限定名常量项的索引
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Class;

                    b2arr = Stream.readBytes(bytes, index, 2);
                    index += 2;

                    klass.getConstantPool().getDataMap().put(i, DataConverter.byteToInt(b2arr));

                    logger.info("\t第 " + i+ " 个: 类型: Class，值: " + klass.getConstantPool().getDataMap().get(i));
                    break;

                case ConstantPool.JVM_CONSTANT_String:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_String;

                    b2arr = Stream.readBytes(bytes, index, 2);
                    index += 2;
                    klass.getConstantPool().getDataMap().put(i, DataConverter.byteToInt(b2arr));
                    logger.info("\t第 " + i+ " 个: 类型: String，值无法获取，因为字符串的内容还未解析到");

                    break;

                case ConstantPool.JVM_CONSTANT_Fieldref:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Fieldref;

                    //指向Class_info的索引
                    b2arr = Stream.readBytes(bytes, index, 2);
                    index += 2;
                    int classIndex = DataConverter.byteToInt(b2arr);
                    //指向NameAndType_info索引

                    b2arr = Stream.readBytes(bytes, index, 2);
                    index += 2;
                    int nameAndTypeIndex = DataConverter.byteToInt(b2arr);

                    klass.getConstantPool().getDataMap().put(i, classIndex << 16 | nameAndTypeIndex);

                    logger.info("\t第 " + i+ " 个: 类型: Field，值: 0x" + Integer.toHexString((int) klass.getConstantPool().getDataMap().get(i)));

                    break;

                case ConstantPool.JVM_CONSTANT_Methodref:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Methodref;

                    b2arr = Stream.readBytes(bytes, index, 2);
                    //指向class索引
                    index += 2;
                    classIndex = DataConverter.byteToInt(b2arr);



                    //指向NameAndType索引
                    b2arr = Stream.readBytes(bytes, index, 2);
                    index += 2;
                    nameAndTypeIndex = DataConverter.byteToInt(b2arr);

                    // 将classIndex与nameAndTypeIndex拼成一个，前十六位是classIndex，后十六位是nameAndTypeIndex
                    klass.getConstantPool().getDataMap().put(i, classIndex << 16 | nameAndTypeIndex);

                    logger.info("\t第 " + i+ " 个: 类型: Method，值: 0x" + Integer.toHexString((int) klass.getConstantPool().getDataMap().get(i)));

                    break;

                case ConstantPool.JVM_CONSTANT_InterfaceMethodref:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_InterfaceMethodref;

                    b2arr = Stream.readBytes(bytes, index, 2);
                    //指向class索引
                    index += 2;
                    classIndex = DataConverter.byteToInt(b2arr);



                    //指向NameAndType索引
                    b2arr = Stream.readBytes(bytes, index, 2);
                    index += 2;
                    nameAndTypeIndex = DataConverter.byteToInt(b2arr);
                    klass.getConstantPool().getDataMap().put(i, classIndex << 16 | nameAndTypeIndex);
                    logger.info("\t第 " + i+ " 个: 类型: InterfaceMethodref，值: 0x" + Integer.toHexString((int) klass.getConstantPool().getDataMap().get(i)));

                    break;

                case ConstantPool.JVM_CONSTANT_NameAndType:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_NameAndType;

                    // 方法名
                    b2arr = Stream.readBytes(bytes, index, 2);
                    index += 2;

                    int methodNameIndex = DataConverter.byteToInt(b2arr);

                    // 方法描述符
                    b2arr = Stream.readBytes(bytes, index, 2);
                    index += 2;

                    int methodDescriptorIndex = DataConverter.byteToInt(b2arr);

                    klass.getConstantPool().getDataMap().put(i, methodNameIndex << 16 | methodDescriptorIndex);

                    logger.info("\t第 " + i+ " 个: 类型: NameAndType，值: 0x" + Integer.toHexString((int) klass.getConstantPool().getDataMap().get(i)));


                    break;
                default:
                    throw new Error("无法识别的常量池项");
            }
        }

        return index;
    }

}
