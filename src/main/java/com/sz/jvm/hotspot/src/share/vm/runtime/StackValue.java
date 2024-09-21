package com.sz.jvm.hotspot.src.share.vm.runtime;

/**
 * @Author
 * @Date 2024-09-15 15:49
 * @Version 1.0
 */

import com.sz.jvm.hotspot.src.share.vm.tools.DataConverter;
import com.sz.jvm.hotspot.src.share.vm.utilities.BasicType;
import lombok.Data;

@Data
public class StackValue {

    private int type;

    /**
     * 数据存储在这里的情况：
     *  1、float
     */
    private byte[] data;

    /**
     * 数据存储在这里的情况：
     *  1、int
     *  2、short
     *  3、char
     *  4、byte
     *  5、double用两个int
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

    public Object getData() {
        switch (type) {
            case BasicType.T_OBJECT:
                return object;
            case BasicType.T_FLOAT:
                return DataConverter.byteToFloat(data);
            case BasicType.T_LONG:
                return DataConverter.bytesToLong(data);
            case BasicType.T_BYTE:
            case BasicType.T_INT:
                return val;
            case BasicType.T_ADDRESS:
                return DataConverter.bytesToLong(data);
        }

        return null;
    }
}
