package com.sz.jvm.hotspot.src.share.vm.oops;

import com.sz.jvm.hotspot.src.share.vm.utilities.AccessFlags;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author
 * @Date 2024-09-09 22:20
 * @Version 1.0
 */
@Data
public class MethodInfo {

    private InstanceKlass belongKlass;

    private int nameIndex;
    private int descriptorIndex;
    private int attributesCount;
    private String methodName;
    private AccessFlags accessFlags;

    private List<CodeAttributeInfo> attributes = new ArrayList<>();

}
