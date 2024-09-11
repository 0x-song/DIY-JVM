package com.sz.jvm.hotspot.src.share.vm.oops;

import lombok.Data;

/**
 * @Author: ziya
 * @Date: 2021/3/6 17:55
 */
@Data
public class AttributeInfo {

    private int attrNameIndex;
    private int attrLength;

    // 用于存储klass的attribute
    private byte[] container;

    public void initContainer() {
        container = new byte[attrLength];
    }
}
