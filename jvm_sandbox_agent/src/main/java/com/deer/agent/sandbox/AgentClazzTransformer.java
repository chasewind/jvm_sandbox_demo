package com.deer.agent.sandbox;

import com.deer.agent.sandbox.entire.EntireMethodEnhanceVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

public class AgentClazzTransformer implements ClassFileTransformer {

    private boolean isNativeSupported;
    public AgentClazzTransformer( boolean isNativeSupported){
        this.isNativeSupported=isNativeSupported;
    }
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(className.startsWith("com/deer/agent")){
            return classfileBuffer;
        }
        //|| className.startsWith("org/springframework/web/servlet/DispatcherServlet")
       if(className.startsWith("com/deer/base")){
           ClassReader cr = new ClassReader(classfileBuffer);
           ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);
           EntireMethodEnhanceVisitor traceClassVisitor = new EntireMethodEnhanceVisitor(cw);
           cr.accept(traceClassVisitor,ClassReader.EXPAND_FRAMES);


           final File dumpClassFile = new File("./sandbox-class-dump/" + className + ".class");
           final File classPath = new File(dumpClassFile.getParent());
           byte[] data= cw.toByteArray();;
           // 创建类所在的包路径
           if (!classPath.mkdirs()
                   && !classPath.exists()) {
               System.out.println("exception ...."+className);
               return data;
           }

           // 将类字节码写入文件
           try {
               writeByteArrayToFile(dumpClassFile, data);
           } catch (IOException e) {
               e.printStackTrace();
           }

           return data;
       }else {
           return classfileBuffer;
       }


    }
}
