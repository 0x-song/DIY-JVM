package com.sz.jvm.hotspot.src.share.vm.oops;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author
 * @Date 2024-09-09 21:20
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceInfo {

    private int constantPoolIndex;

    private String interfaceName;

}
