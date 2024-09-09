package com.sz.jvm.hotspot.src.share.vm.tools;

/**
 * @Author
 * @Date 2024-09-08 9:34
 * @Version 1.0
 */
public class Stream {


    public static void readBytes(byte[] source, int index, int length, byte[] dest) {
        System.arraycopy(source, index, dest, 0, length);
    }

    public static byte[] readBytes(byte[] bytes, int index, int length) {
        byte[] dest = new byte[length];
        System.arraycopy(bytes, index, dest, 0, length);
        return dest;
    }
}
