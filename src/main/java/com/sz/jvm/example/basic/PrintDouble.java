package com.sz.jvm.example.basic;

/**
 * @Author
 * @Date 2024-09-19 20:31
 * @Version 1.0
 */
public class PrintDouble {

    /**
     *  0 ldc2_w #7 <10.0>
     *  3 dstore_1
     *  4 getstatic #9 <java/lang/System.out : Ljava/io/PrintStream;>
     *  7 dload_1
     *  8 invokevirtual #15 <java/io/PrintStream.println : (D)V>
     * 11 return
     * @param args
     */
    public static void main(String[] args) {
        double d = 10.0;
        System.out.println(d);
    }
}
