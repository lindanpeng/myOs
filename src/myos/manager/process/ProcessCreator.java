package myos.manager.process;

import myos.OS;
import myos.constant.OsConstant;
import myos.manager.filesys.FileOperator;
import myos.manager.memory.Memory;
import myos.manager.memory.PCB;
import myos.manager.memory.SubArea;

import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

/**
 * Created by lindanpeng on 2017/12/24.
 * 进程创建者
 */
public class ProcessCreator {
    private Memory memory;
    private CPU cpu;
    public ProcessCreator( ){
        this.memory= OS.memory;
        this.cpu=OS.cpu;
    }
    /**
     * 为打开的可执行文件创建进程
     * @param program
     */
    public void create(byte[] program) throws Exception {
        for (int i=0;i<program.length;i++){
            System.out.println("指令"+i+"是"+program[i]);
        }
        /*申请空白进程块*/
        Queue<PCB> freePCBs=memory.getFreePCB();
        if (freePCBs.size()==0)
            throw  new Exception("当前运行的进程过多，请关闭其他程序后再试");
        /*申请内存*/
        SubArea subArea=null;
        //首次适配法
        ListIterator<SubArea> it=memory.getSubAreas().listIterator();
        while(it.hasNext()){
            SubArea s=it.next();
            if (s.getStatus()==SubArea.STATUS_FREE&&s.getSize()>=program.length) {
                subArea = s;
                break;
            }
        }
        if (subArea==null)
            throw new Exception("内存不足");
        PCB freePCB=freePCBs.poll();
        //将可用区域划分出来
       if (subArea.getSize()>program.length){
           int newSubAreaSize=subArea.getSize()-program.length;
           subArea.setSize(program.length);
           subArea.setTaskNo(freePCB.getPID());
           subArea.setStatus(SubArea.STATUS_BUSY);
           SubArea newSubArea=new SubArea();
           newSubArea.setStatus(SubArea.STATUS_FREE);
           newSubArea.setSize(newSubAreaSize);
           newSubArea.setStartAdd(subArea.getStartAdd()+subArea.getSize());
           it.add(newSubArea);
       }
     //  System.out.println("进程首地址："+subArea.getStartAdd());
       //将数据复制到用户区
        byte[] userArea=memory.getUserArea();
       for (int i=subArea.getStartAdd(),j=0;i<subArea.getStartAdd()+subArea.getSize();i++,j++){
           userArea[i]=program[j];
       }
       System.out.println("创建的进程ID"+freePCB.getPID());
        //初始化进程控制块
        freePCB.setMemStart(subArea.getStartAdd());
        freePCB.setMemEnd(program.length);
        freePCB.setCounter(subArea.getStartAdd());
        freePCB.setStatus(PCB.STATUS_WAIT);
        memory.getWaitPCB().offer(freePCB);//进程就绪
        //判断当前是否有实际运行进程，没有的则申请进程调度
        if (memory.getRunningPCB()==null||memory.getRunningPCB().getStatus()==PCB.STATUS_HANG_OUT) {
            cpu.dispatch();
        }


    }

}
