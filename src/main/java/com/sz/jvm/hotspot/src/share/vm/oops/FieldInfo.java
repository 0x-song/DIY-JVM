package com.sz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

/**
 * @Author
 * @Date 2024-09-09 21:34
 * @Version 1.0
 */
@Data
public class FieldInfo {

    private int accessFlags;
    private int nameIndex;
    private int descriptorIndex;
    private int attributesCount;

}
