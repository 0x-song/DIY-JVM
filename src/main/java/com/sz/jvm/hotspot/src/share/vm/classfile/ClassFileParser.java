package com.sz.jvm.hotspot.src.share.vm.classfile;

import com.sz.jvm.hotspot.src.share.vm.oops.ConstantPool;
import com.sz.jvm.hotspot.src.share.vm.oops.InstanceKlass;
import com.sz.jvm.hotspot.src.share.vm.tools.DataConverter;
import com.sz.jvm.hotspot.src.share.vm.tools.JVMConstant;
import com.sz.jvm.hotspot.src.share.vm.tools.Stream;
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

        return null;
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
