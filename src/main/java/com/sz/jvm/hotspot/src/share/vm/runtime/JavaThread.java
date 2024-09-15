package com.sz.jvm.hotspot.src.share.vm.runtime;

import lombok.Data;

import java.util.Stack;

/**
 * @Author
 * @Date 2024-09-15 12:31
 * @Version 1.0
 */
@Data
public class JavaThread extends Thread{

    private Stack<Frame> stack = new Stack<>();
}
