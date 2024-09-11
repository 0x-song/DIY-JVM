package com.sz.jvm.hotspot.src.share.vm.utilities;

import lombok.Data;

/**
 * @Author
 * @Date 2024-09-11 20:55
 * @Version 1.0
 */
@Data
public class AccessFlags {

    private int flag;

    public AccessFlags(int flag) {
        this.flag = flag;
    }

    public boolean isStatic() {
        return (flag & BasicType.JVM_ACC_STATIC) != 0;
    }
}
