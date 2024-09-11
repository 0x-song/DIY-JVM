package com.sz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author
 * @Date 2024-09-07 16:12
 * @Version 1.0
 */
@Data
public class InstanceKlass {

    private byte[] magic = new byte[4];

    private byte[] majorVersion = new byte[2];

    private byte[] minorVersion = new byte[2];

    private byte[] constantPoolCount = new byte[2];

    private ConstantPool constantPool;

    private int accessFlag;

    private int thisClass;

    private int superClass;

    private int interfacesLength;
    private List<InterfaceInfo> interfaceInfos = new ArrayList<>();

    private int fieldsLength;
    private List<FieldInfo> fieldInfos = new ArrayList<>();

    private int methodLength;
    private List<MethodInfo> methodInfos = new ArrayList<>();

    private int attributeLength;

    private List<AttributeInfo> attributeInfos = new ArrayList<>();


    public InstanceKlass() {
        this.constantPool = new ConstantPool();
        constantPool.setKlass(this);
    }
}
