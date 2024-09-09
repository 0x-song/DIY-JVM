package com.sz.jvm.hotspot.src.share.vm.tools;

/**
 * @Author
 * @Date 2024-09-08 9:37
 * @Version 1.0
 */
public class JVMConstant {

    //    magic 4字节
    public static final int MAGIC = 4;
//    minor version 2字节
    public static final int MINOR_VERSION = 2;

    public static final int MAJOR_VERSION = 2;
//    major version 2字节
//    constant pool count 2字节
    public static final int CONSTANT_POOL_COUNT = 2;

//    constant pool 待定
    public static final int CONSTANT_POOL_TAG = 1;
//    access flag 2字节
    public static final int ACCESS_FLAG = 2;
//    this_class类名 2字节
    public static final int THIS_CLASS = 2;
//    super_class父类 2字节
    public static final int SUPER_CLASS = 2;

//    interfaces_count 2字节
    public static final int INTERFACES_COUNT = 2;
//    interfaces[] 待定
//    fields_count 2字节
    public static final int FIELDS_COUNT = 2;
//    fields_info 待定
//    methods_count方法数量 2字节
    public static final int METHODS_COUNT = 2;
//    methods_info 待定
//    attributes_count 2字节
    public static final int ATTRIBUTES_COUNT = 2;
//    attributes[] 待定

}
