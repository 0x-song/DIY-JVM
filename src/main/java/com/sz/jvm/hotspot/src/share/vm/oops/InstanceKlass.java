package com.sz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

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

    public InstanceKlass() {
        this.constantPool = new ConstantPool();
        constantPool.setKlass(this);
    }
}
