package com.sz.jvm.hotspot.src.share.vm.intepreter;

import com.sz.jvm.hotspot.src.share.vm.oops.CodeAttributeInfo;
import com.sz.jvm.hotspot.src.share.vm.oops.MethodInfo;
import com.sz.jvm.hotspot.src.share.vm.tools.DataConverter;
import com.sz.jvm.hotspot.src.share.vm.tools.Stream;
import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Author: ziya
 * @Date: 2021/3/26 14:51
 */
@Data
public class BaseBytecodeStream {

    protected MethodInfo belongMethod;
    protected CodeAttributeInfo belongCode;

    protected int length;
    protected int index;
    protected byte[] codes;

    /**
     * 一次取一字节数据，自动累加
     * @return
     */
    public int getU1Code() {
        if (index < 0 || index >= length) {
            throw new Error("字节码指令的索引超过了最大值");
        }

        return Byte.toUnsignedInt(codes[index++]);
    }

    public int getU2Code() {
        if (index < 0 || index >= length) {
            throw new Error("字节码指令的索引超过了最大值");
        }

        byte[] u2Arr = new byte[2];

        u2Arr = Stream.readBytes(codes, index, 2);

        index += 2;

        return DataConverter.byteArrayToInt(u2Arr);
    }

    public short getUnsignedShort() {
        if (index < 0 || index >= length) {
            throw new Error("字节码指令的索引超过了最大值");
        }

        byte[] u2Arr = new byte[2];

        u2Arr = Stream.readBytes(codes, index, 2);

        index += 2;

        return (short) DataConverter.byteToInt(u2Arr);
    }

    public int getU4Code() {
        if (index < 0 || index >= length) {
            throw new Error("字节码指令的索引超过了最大值");
        }

        byte[] arr;

        arr = Stream.readBytes(codes, index, 4);
        index += 4;

        ByteBuffer buffer = ByteBuffer.wrap(arr);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        return buffer.getInt();
    }

    public void reset() {
        index = 0;
    }

    public boolean end() {
        return index >= length;
    }

    public void inc(int step) {
        index +=  step;
    }
}
