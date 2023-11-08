package com.deer.agent.sandbox.entire;

import com.deer.agent.sandbox.Sender;
import com.deer.agent.sandbox.trace.MethodRecorder;
import com.deer.agent.sandbox.trace.MethodResult;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class EntireMethodHelper {

    public final static Type Recorder=Type.getType(MethodRecorder.class);
    public final static Type Result=Type.getType(MethodResult.class);

   public static Method BeforeMethod = getAsmMethod(
           MethodRecorder.class,
            "before",
            Object[].class, String.class,  String.class, String.class
    );
    public static   Method AfterMethod = getAsmMethod(
            MethodRecorder.class,
            "after",
            Object.class,  Object[].class, String.class,  String.class, String.class
    );
    public static   Method ThrowMethod = getAsmMethod(
            MethodRecorder.class,
            "exception",
            Throwable.class, String.class, String.class,String.class
    );
   private static Method getAsmMethod(final Class<?> clazz,
                        final String methodName,
                        final Class<?>... parameterClassArray) {
        return Method.getMethod(unCaughtGetClassDeclaredJavaMethod(clazz, methodName, parameterClassArray));
    }
    private static   java.lang.reflect.Method unCaughtGetClassDeclaredJavaMethod(final Class<?> clazz,
                                                                         final String name,
                                                                         final Class<?>... parameterClassArray) {
        try {
            return clazz.getDeclaredMethod(name, parameterClassArray);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
