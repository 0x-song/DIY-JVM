package com.sz.jvm.hotspot.src.share.vm.classfile;

import com.sz.jvm.hotspot.src.share.vm.oops.DescriptorInfo;
import com.sz.jvm.hotspot.src.share.vm.runtime.JavaFrame;
import com.sz.jvm.hotspot.src.share.vm.runtime.StackValue;
import com.sz.jvm.hotspot.src.share.vm.utilities.BasicType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author
 * @Date 2024-09-16 15:00
 * @Version 1.0
 */
@Data
public class DescriptorStream {

    private String descriptorName;

    private List<DescriptorInfo> parameters = new ArrayList<>();


    private int methodParamCount;

    private DescriptorInfo returnElement;



    public DescriptorStream(String descriptorName) {
        this.descriptorName = descriptorName;
    }

    public void parseMethod() {
        //解析方法参数
        parseMethodParams();
        //解析方法返回值
        parseMethodReturn();
    }

    private void parseMethodReturn() {
        int index = descriptorName.indexOf(")");
        String returnStr = descriptorName.substring(index + 1);
        returnElement = new DescriptorStream2(returnStr).parse();
    }

    //(Ljava/lang/String;)V
    private void parseMethodParams() {
        int paramStartIndex = descriptorName.indexOf("(");
        int paramEndIndex = descriptorName.indexOf(")");
        String paramStr = descriptorName.substring(paramStartIndex + 1, paramEndIndex);
        //利用;来进行分割
        String[] strings = paramStr.split(";");
        if(strings.length > 0){
            for (int i = 0; i < strings.length; i++) {
                String s = strings[i];
                if(s.startsWith("L") || s.startsWith("[")){
                    //如果是引用类型或者数组类型，那么本身后面是需要添加上;才是完整的结构
                    s += ";";

                    parameters.add(new DescriptorStream2(s).parse());
                    continue;
                }
                //解析其他的数据类型
                new DescriptorStream2(s).parseParams(parameters);
            }
        }
        setMethodParamCount(parameters.size());


    }

    public Object[] getParameterValues(JavaFrame javaFrame) {
        Object[] values = new Object[methodParamCount];
        for (int i = 0; i < methodParamCount; i++) {
            DescriptorInfo descriptorInfo = parameters.get(i);
            switch (descriptorInfo.getType()) {
                case BasicType.T_CHAR:
                    values[i] = javaFrame.getStack().pop().getVal();

                    break;
                case BasicType.T_INT:
                    values[i] = javaFrame.getStack().pop().getVal();

                    break;
                case BasicType.T_OBJECT:
                    values[i] = javaFrame.getStack().pop().getObject();

                    break;
                case BasicType.T_LONG:
                    values[i] = javaFrame.getStack().pop().getVal();

                    break;
                case BasicType.T_DOUBLE:
                    values[i] = javaFrame.getStack().popDouble2();

                    break;
                case BasicType.T_ARRAY:
                    throw new Error("数组类型，未作处理");
                default:
                    throw new Error("无法识别的参数类型");
            }
        }
        return values;
    }

    public Class[] getParamsType() {

        Class<?>[] types = new Class[methodParamCount];

        for (int i = 0; i < methodParamCount; i++) {
            DescriptorInfo info = getParameters().get(i);

            switch (info.getType()) {
                case BasicType.T_CHAR:
                    types[i] = char.class;

                    break;
                case BasicType.T_INT:
                    types[i] = int.class;

                    break;
                case BasicType.T_OBJECT:
                    try {
                        types[i] = Class.forName(info.getTypeDesc().replace('/', '.'));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;
                case BasicType.T_LONG:
                    types[i] = long.class;

                    break;
                case BasicType.T_DOUBLE:
                    types[i] = double.class;

                    break;
                case BasicType.T_ARRAY:
                    throw new Error("数组类型，未作处理");
                default:
                    throw new Error("无法识别的参数类型");
            }
        }

        return types;
    }

    public void push(Object o, JavaFrame javaFrame) {
        switch (returnElement.getType()) {
            case BasicType.T_CHAR:
                javaFrame.getStack().push(new StackValue(BasicType.T_CHAR, (char) o));

                break;
            case BasicType.T_INT:
                javaFrame.getStack().push(new StackValue(BasicType.T_INT, (int) o));

                break;
            case BasicType.T_OBJECT:
                javaFrame.getStack().push(new StackValue(BasicType.T_OBJECT, o));

                break;
            case BasicType.T_LONG:
                javaFrame.getStack().push(new StackValue(BasicType.T_LONG, (int) o));

                break;
            case BasicType.T_DOUBLE:
                javaFrame.getStack().push(new StackValue(BasicType.T_DOUBLE, (int) o));

                break;
            case BasicType.T_ARRAY:
                throw new Error("数组类型，未作处理");
            default:
                throw new Error("无法识别的参数类型");
        }
    }
}
