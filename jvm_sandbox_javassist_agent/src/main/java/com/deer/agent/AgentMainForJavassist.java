package com.deer.agent;


import com.deer.agent.sandbox.AgentClassLoader;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.jar.JarFile;

public class AgentMainForJavassist {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain");
    }
    public static void agentmain(String agentArgs, Instrumentation inst){
        System.out.println("load agent...");
        Class[] clazzList = inst.getAllLoadedClasses();
        Arrays.stream(clazzList).forEach(clazz->{
            String filePath ="/Users/yudongwei/Downloads/backup4/git/jvm_sandbox_demo/jvm_sandbox_javassist_core/target/jvm_sandbox_javassist_core-1.0-SNAPSHOT-jar-with-dependencies.jar";
            File agentCoreFile = new File(filePath);
            AgentClassLoader agentClassLoader = null;
            try {
                agentClassLoader = new AgentClassLoader(new URL[] {agentCoreFile.toURI().toURL()});
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if(clazz.getName().startsWith("com.deer.base.service")
                    || clazz.getName().startsWith("com.deer.base.controller")
                    || clazz.getName().equals("org.springframework.web.servlet.FrameworkServlet")
                    || clazz.getName().equals("org.springframework.web.servlet.DispatcherServlet")

            ){

                try {
                    if(agentClassLoader!=null){
                        inst.appendToBootstrapClassLoaderSearch(new JarFile(agentCoreFile));
                        Class<?> agentCoreTransformer =   agentClassLoader.loadClass("com.deer.agent.core.AgentCoreTransformer");
                        Constructor<?> constructor = agentCoreTransformer.getDeclaredConstructor(String.class);
                        inst.addTransformer((ClassFileTransformer) constructor.newInstance(filePath),true);
                        inst.retransformClasses(clazz);
                    }
                }catch (Exception e){
                    //
                    e.printStackTrace();
                }
            }
        });

    }
}
