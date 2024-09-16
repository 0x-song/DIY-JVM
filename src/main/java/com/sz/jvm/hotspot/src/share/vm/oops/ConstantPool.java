package com.sz.jvm.hotspot.src.share.vm.oops;


import java.util.HashMap;
import java.util.Map;

/**
 * @Author
 * @Date 2024-09-08 10:52
 * @Version 1.0
 */
//@Data
public class ConstantPool {

    public static final int JVM_CONSTANT_Utf8 = 1;
    public static final int JVM_CONSTANT_Unicode = 2;   /* unused */
    public static final int JVM_CONSTANT_Integer = 3;
    public static final int JVM_CONSTANT_Float = 4;
    public static final int JVM_CONSTANT_Long = 5;
    public static final int JVM_CONSTANT_Double = 6;
    public static final int JVM_CONSTANT_Class = 7;
    public static final int JVM_CONSTANT_String = 8;
    public static final int JVM_CONSTANT_Fieldref = 9;
    public static final int JVM_CONSTANT_Methodref = 10;
    public static final int JVM_CONSTANT_InterfaceMethodref = 11;
    public static final int JVM_CONSTANT_NameAndType = 12;
    public static final int JVM_CONSTANT_MethodHandle = 15; /* JSR 292 */
    public static final int JVM_CONSTANT_MethodType = 16;   /* JSR 292 */
    public static final int JVM_CONSTANT_InvokeDynamic = 18;    /* JSR 292 */
    public static final int JVM_CONSTANT_ExternalMax = 18;  /* Last tag found in classfiles */

    private int length;

    private InstanceKlass klass;

    //用于存储字符串常量池中每个常量的tag
    private byte[] tag;

    //用于存储字符串常量池中tag和字符串的映射关系
    private Map<Integer, Object> dataMap;

    public void init() {
        this.tag = new byte[length];
        this.dataMap = new HashMap<>(length);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public InstanceKlass getKlass() {
        return klass;
    }

    public void setKlass(InstanceKlass klass) {
        this.klass = klass;
    }

    public byte[] getTag() {
        return tag;
    }

    public void setTag(byte[] tag) {
        this.tag = tag;
    }

    public Map<Integer, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<Integer, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public String getInterfaceName(int index) {
        if (0 == index || index > length) {
            return null;
        }
        return (String) getDataMap().get(getDataMap().get(index));
    }

    public String getClassNameByFieldInfo(int index) {
        // 获取Class信息在常量池中的index
        int data = (int) getDataMap().get(index);
        int classIndex = data >> 16;

        // 获取Class全限定名的index
        int classNameIndex = (int) getDataMap().get(classIndex);

        return (String) getDataMap().get(classNameIndex);
    }

    public String getFieldName(int index) {
        // 获取NameAndType在常量池中的index
        int data = (int) getDataMap().get(index);
        int i = data & 0xFF;

        int nameAndType = (int) getDataMap().get(i);
        i = nameAndType >> 16;

        return (String) getDataMap().get(i);

    }

    public String getClassNameByMethodInfo(int operand) {

        return getClassNameByFieldInfo(operand);
    }

    public String getMethodNameByMethodInfo(int operand) {

        // 获取Methodinfo在常量池中的index
        int i = (int) getDataMap().get(operand);
        int nameAndTypeIndex = i & 0xff;

        // 获取NameAndType的值
        int data = (int) getDataMap().get(nameAndTypeIndex);
        i = data >> 16;

        return (String) getDataMap().get(i);
    }

    public String getDescriptorNameByMethodInfo(int operand) {

        // 获取Methodinfo在常量池中的index
        int i = (int) getDataMap().get(operand);
        int nameAndTypeIndex = i & 0xff;

        // 获取NameAndType的值
        int data = (int) getDataMap().get(nameAndTypeIndex);
        i = data & 0xFF;

        return (String) getDataMap().get(i);
    }
}
