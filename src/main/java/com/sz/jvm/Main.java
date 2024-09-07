package com.sz.jvm;

import com.sz.jvm.hotspot.src.share.vm.classfile.BootClassLoader;
import com.sz.jvm.hotspot.src.share.vm.oops.InstanceKlass;

/**
 * @Author
 * @Date 2024-09-07 17:34
 * @Version 1.0
 *///TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        createVM();
    }

    private static void createVM() {
        InstanceKlass instanceKlass = BootClassLoader.loadMainKlass("com.sz.jvm.example.HelloWorld");
    }
}