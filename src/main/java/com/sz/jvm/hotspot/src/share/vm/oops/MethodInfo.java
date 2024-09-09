package com.sz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

/**
 * @Author
 * @Date 2024-09-09 22:20
 * @Version 1.0
 */
@Data
public class MethodInfo {

    private int nameIndex;
    private int descriptorIndex;
    private int attributesCount;
    private String methodName;

}
