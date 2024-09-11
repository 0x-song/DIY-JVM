package com.sz.jvm.hotspot.src.share.vm.tools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Author
 * @Date 2024-09-08 10:56
 * @Version 1.0
 */
public class DataConverter {

    /**
     *  bytes[0] 保存高8位,bytes[1] 保存低8位
     * (high << 8) 将高8位左移8位,目的是将高8位置于short整数的高8位
     * & 0xFF00 与0xFF00做位掩码,目的是清零低8位,保留高8位
     * low & 0xFF 将低8位与0xFF做位掩码,目的是清零高8位,保留低8位
     * | 运算符进行或运算,将高8位和低8位合成一个short整数
     * @param bytes
     * @return
     */
    public static int byteToInt(byte[] bytes) {
        int high = bytes[0];
        int low = bytes[1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }

    public static Object byteToFloat(byte[] b) {

        int l;

        l = b[3];
        l &= 0xff;
        l |= ((long) b[2] << 8);
        l &= 0xffff;
        l |= ((long) b[1] << 16);
        l &= 0xffffff;
        l |= ((long) b[0] << 24);

        return Float.intBitsToFloat(l);
    }

    public static double byteToDouble(byte[] arr, boolean littleEndian) {
        ByteBuffer buffer = ByteBuffer.wrap(arr,0,8);

        if (littleEndian) {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        return buffer.getDouble();
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value=0;

        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(bytes[i] & 0xFF) << shift;
        }

        return value;
    }
}
