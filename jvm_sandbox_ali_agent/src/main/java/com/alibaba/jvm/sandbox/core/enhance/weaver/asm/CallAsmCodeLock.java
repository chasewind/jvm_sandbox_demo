package com.alibaba.jvm.sandbox.core.enhance.weaver.asm;

/**
 * 用于Call的代码锁
 */

import org.objectweb.asm.commons.AdviceAdapter;

/**
 * 用于Call的代码锁
 */
public class CallAsmCodeLock extends AsmCodeLock {

//    CallAsmCodeLock(AdviceAdapter aa) {
//        super(
//                aa,
//                new int[]{
//                        ICONST_2, POP
//                },
//                new int[]{
//                        ICONST_3, POP
//                }
//        );
//    }

 public   CallAsmCodeLock(AdviceAdapter aa) {
        super(
                aa,
                new int[]{
                        ICONST_2, POP
                },
                new int[]{
                        ICONST_3, POP
                }
        );
    }
}
