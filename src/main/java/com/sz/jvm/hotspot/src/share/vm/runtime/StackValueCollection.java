package com.sz.jvm.hotspot.src.share.vm.runtime;

import com.sz.jvm.hotspot.src.share.vm.tools.DataConverter;
import com.sz.jvm.hotspot.src.share.vm.utilities.BasicType;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Stack;

/**
 * @Author
 * @Date 2024-09-15 15:49
 * @Version 1.0
 */
@Data
public class StackValueCollection {
    private Logger logger = LoggerFactory.getLogger(StackValueCollection.class);

    /*************************************
     * 模拟操作数栈
     */
    private Stack<StackValue> container = new Stack<>();

    public StackValueCollection() {
    }

    public void push(StackValue value) {
        getContainer().push(value);
    }

    public StackValue pop() {
        return getContainer().pop();
    }

    public StackValue peek() {
        return getContainer().peek();
    }

    /****************************************
     *  模拟局部变量表
     */
    private int maxLocals;
    private int index;
    private StackValue[] locals;

    public StackValueCollection(int size) {
        maxLocals = size;

        locals = new StackValue[size];
    }

    public void add(int index, StackValue value) {
        getLocals()[index] = value;
    }

    public StackValue get(int index) {
        return getLocals()[index];
    }

    public void pushDouble(double d) {
        byte[] bytes = DataConverter.doubleToByte(d);
        ByteBuffer buffer = ByteBuffer.wrap(bytes,0,8);
        push(new StackValue(BasicType.T_DOUBLE, buffer.getInt(0)));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        push(new StackValue(BasicType.T_DOUBLE, buffer.getInt(4)));
    }

    public StackValue[] popDouble() {
        StackValue[] result = new StackValue[2];
        result[0] = pop();
        result[1] = pop();
        return result;
    }

    /**
     * 因为一个double占两个单元
     * 所以取的时候要连续取两个，并合并成double
     * @return
     */
    public double popDouble2() {
        ByteBuffer buffer = ByteBuffer.allocate(8);

        StackValue value1 = pop();  // 后4字节
        StackValue value2 = pop();  // 前4字节

        if (value1.getType() != BasicType.T_DOUBLE || value2.getType() != BasicType.T_DOUBLE) {
            throw new Error("类型检查不通过");
        }

        buffer.putInt(value2.getVal(), value1.getVal());

        return buffer.getDouble();
    }
}
