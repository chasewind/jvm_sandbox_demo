package com.deer.agent.starter;

import com.sun.tools.attach.VirtualMachine;
import sun.jvmstat.monitor.*;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class AgentStarter {
    public static void main(String[] args) throws URISyntaxException, MonitorException {
        String processId = null;
        // 获取监控主机
        MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
        // 取得所有在活动的虚拟机集合
        Set<Integer> vmlist = new HashSet<Integer>(local.activeVms());
        // 遍历集合，找到我们系统启动需要挂载的PID和进程名
        for(Integer process : vmlist) {
            MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + process));
            // 获取类名
            String processName = MonitoredVmUtil.mainClass(vm, true);
            if(processName.equals("com.deer.base.BaseApp")){
                processId = process.toString();
                break;
            }
        }
        if(processId == null){
            return;
        }
        System.out.println("find processId ,and ready to attach..."+processId);
        String agentPath="/Users/dreamworld/workspace/jvm_sandbox_demo/jvm_sandbox_runtime_arthas/target/jvm_sandbox_runtime_arthas-1.0-SNAPSHOT-jar-with-dependencies.jar";
        try {
            VirtualMachine vm = VirtualMachine.attach(processId);
            vm.loadAgent(agentPath);
            vm.detach();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
