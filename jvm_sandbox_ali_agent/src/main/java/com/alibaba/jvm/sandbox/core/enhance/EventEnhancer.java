package com.alibaba.jvm.sandbox.core.enhance;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver;
import com.alibaba.jvm.sandbox.core.util.AsmUtils;
import com.alibaba.jvm.sandbox.core.util.ObjectIDs;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.ASM7;

/**
 * 事件代码增强器
 *
 * @author luanjia@taobao.com
 */
public class EventEnhancer implements Enhancer {


    /**
     * 创建ClassWriter for asm
     *
     * @param cr ClassReader
     * @return ClassWriter
     */
    private ClassWriter createClassWriter(final ClassLoader targetClassLoader,
                                          final ClassReader cr) {
        return new ClassWriter(cr, COMPUTE_FRAMES | COMPUTE_MAXS) {


            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                return AsmUtils.getCommonSuperClass(type1, type2, targetClassLoader);
            }

        };
    }

    private static final boolean isDumpClass = true;

    /*
     * dump class to file
     * 用于代码调试
     */
    private static byte[] dumpClassIfNecessary(String className, byte[] data) {
        if (!isDumpClass) {
            return data;
        }
        final File dumpClassFile = new File("./sandbox-class-dump/" + className + ".class");
        final File classPath = new File(dumpClassFile.getParent());

        // 创建类所在的包路径
        if (!classPath.mkdirs()
                && !classPath.exists()) {
            return data;
        }

        // 将类字节码写入文件
        try {
            writeByteArrayToFile(dumpClassFile, data);
        } catch (IOException e) {
        }

        return data;
    }

    @Override
    public byte[] toByteCodeArray(final ClassLoader targetClassLoader,
                                  final byte[] byteCodeArray,
                                  final String namespace,
                                  final int listenerId,
                                  final Event.Type[] eventTypeArray) {
        // 返回增强后字节码
        final ClassReader cr = new ClassReader(byteCodeArray);
        final ClassWriter cw = createClassWriter(targetClassLoader, cr);
        final int targetClassLoaderObjectID = ObjectIDs.instance.identity(targetClassLoader);
        cr.accept(
                new EventWeaver(
                        ASM7, cw, namespace, listenerId,
                        targetClassLoaderObjectID,
                        cr.getClassName(),
                        eventTypeArray
                ),
                EXPAND_FRAMES
        );
        return dumpClassIfNecessary(cr.getClassName(), cw.toByteArray());
    }

}
