package com.sz.jvm.hotspot.src.share.vm.runtime;

/**
 * @Author
 * @Date 2024-09-15 15:49
 * @Version 1.0
 */

import lombok.Data;

@Data
public class StackValue {

    private int type;

    /**
     * 数据
     */
    private int val;

    private Object object;

    public StackValue(int type, int val) {
        this.type = type;
        this.val = val;
    }

    public StackValue(int type, Object val) {
        this.type = type;
        this.object = val;
    }
}
