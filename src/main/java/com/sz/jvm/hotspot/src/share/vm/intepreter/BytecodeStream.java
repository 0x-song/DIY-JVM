package com.sz.jvm.hotspot.src.share.vm.intepreter;


import com.sz.jvm.hotspot.src.share.vm.oops.CodeAttributeInfo;
import com.sz.jvm.hotspot.src.share.vm.oops.MethodInfo;

/**
 * @Author: ziya
 * @Date: 2021/3/26 14:55
 */
public class BytecodeStream extends BaseBytecodeStream {

    public BytecodeStream(MethodInfo belongMethod, CodeAttributeInfo belongCode) {
        this.belongMethod = belongMethod;
        this.belongCode = belongCode;
        this.length = belongCode.getCodeLength();
        this.index = 0;
        this.codes = new byte[this.length];
    }

}
