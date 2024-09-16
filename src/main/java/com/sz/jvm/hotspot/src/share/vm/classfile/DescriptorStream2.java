package com.sz.jvm.hotspot.src.share.vm.classfile;

import com.sz.jvm.hotspot.src.share.vm.oops.DescriptorInfo;
import com.sz.jvm.hotspot.src.share.vm.utilities.BasicType;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @Author
 * @Date 2024-09-16 16:23
 * @Version 1.0
 */
public class DescriptorStream2 {

    private byte[] container;

    private DescriptorInfo descriptor = new DescriptorInfo();

    public DescriptorStream2(String descriptor) {
        try {
            container = descriptor.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public DescriptorInfo parse() {

            byte b = container[0];

            switch (b){
                case '[':{
                    String type = parseArrayType();

                    descriptor.setType(BasicType.T_ARRAY);
                    descriptor.setTypeDesc(type);
                    return descriptor;
                }
                case 'L':{
                    String type = parseReferenceType();
                    descriptor.setType(BasicType.T_OBJECT);
                    descriptor.setTypeDesc(type);
                    return descriptor;
                }
                case 'Z':   // boolean
                    descriptor.setType(BasicType.T_BOOLEAN);

                    return descriptor;
                case 'B':   // byte
                    descriptor.setType(BasicType.T_BYTE);

                    return descriptor;
                case 'C':   // char
                    descriptor.setType(BasicType.T_CHAR);

                    return descriptor;
                case 'I':   // int
                    descriptor.setType(BasicType.T_INT);

                    return descriptor;
                case 'F':   // float
                    descriptor.setType(BasicType.T_FLOAT);

                    return descriptor;
                case 'J':   // long
                    descriptor.setType(BasicType.T_LONG);

                    return descriptor;
                case 'D':   // double
                    descriptor.setType(BasicType.T_DOUBLE);

                    return descriptor;
                case 'V':
                    descriptor.setType(BasicType.T_VOID);

                    return descriptor;
                default:
                    throw new Error("无法识别的类型");
            }
    }

    private String parseReferenceType() {
// 跳过开头的L、结尾的;
        int size = container.length - 2;
        byte[] str = new byte[size];

        // 跳过开头的L、结尾的;
        int j = 0;
        for (int i = 1; i < container.length - 1; i++) {
            str[j++] = container[i];
        }

        return new String(str);
    }

    private String parseArrayType() {
        for (int i = 0; i < container.length; i++) {
            if(container[i] == '['){
                descriptor.incArrayDimension();

                continue;
            }
            if ('L' != container[i]) {
                return null;
            }

            return parseArrayReferenceType();
        }
        return null;
    }

    private String parseArrayReferenceType() {
        int size =container.length -descriptor.getArrayDimension() - 2;
        byte[] str = new byte[size];

        int j = 0;
        for (int i = descriptor.getArrayDimension() + 1; i < container.length - 1; i++) {
            str[j++] = container[i];
        }
        return new String(str);
    }

    public void parseParams(List<DescriptorInfo> infos) {
        for (int i = 0; i < container.length; i++) {
            byte b = container[i];
            switch (b) {
                case 'Z':   // boolean
                    infos.add(new DescriptorInfo(true, BasicType.T_BOOLEAN));

                    break;
                case 'B':   // byte
                    infos.add(new DescriptorInfo(true, BasicType.T_BYTE));

                    break;
                case 'C':   // char
                    infos.add(new DescriptorInfo(true, BasicType.T_CHAR));

                    break;
                case 'I':   // int
                    infos.add(new DescriptorInfo(true, BasicType.T_INT));

                    break;
                case 'F':   // float
                    infos.add(new DescriptorInfo(true, BasicType.T_FLOAT));

                    break;
                case 'J':   // long
                    infos.add(new DescriptorInfo(true, BasicType.T_LONG));

                    break;
                case 'D':   // double
                    infos.add(new DescriptorInfo(true, BasicType.T_DOUBLE));

                    break;
                case 'V':
                    infos.add(new DescriptorInfo(true, BasicType.T_VOID));

                    break;
                default:
                    throw new Error("无法识别的类型");
            }
        }
    }
}
