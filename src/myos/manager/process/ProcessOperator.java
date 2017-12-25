package myos.manager.process;

import myos.constant.OsConstant;
import myos.manager.filesys.FileOperator;
import myos.manager.memory.Memory;
import myos.manager.memory.PCB;
import myos.manager.memory.SubArea;

import java.util.ListIterator;
import java.util.Queue;

/**
 * Created by lindanpeng on 2017/12/24.
 * 进程管理类
 */
public class ProcessOperator {
    private Memory memory;
    private CPU cpu;
    public ProcessOperator(CPU cpu,Memory memory ){
        this.memory=memory;
        this.cpu=cpu;
    }
    /**
     * 为打开的可执行文件创建进程
     * @param program
     */
    public void create(byte[] program) throws Exception {
        /*申请空白进程块*/
        Queue<PCB> freePCBs=memory.getFreePCB();
        if (freePCBs.size()>= OsConstant.PCB_COUNT)
            throw  new Exception("当前运行的进程过多，请关闭其他程序后再试");
        /*申请内存*/
        SubArea subArea=null;
        //首次适配法
        ListIterator<SubArea> it=memory.getSubAreas().listIterator();
        while(it.hasNext()){
            SubArea s=it.next();
            if (s.getStatus()==SubArea.STATUS_FREE&&s.getSize()>=program.length)
                subArea=s;
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
        //初始化进程控制块
        freePCB.setMemStart(subArea.getStartAdd());
        freePCB.setMemEnd(program.length);
        freePCB.setCounter(0);
        freePCB.setStatus(PCB.STATUS_WAIT);
        //判断当前是否有运行进程，没有的将该进程设置为运行进程，否则添加到就绪进程队列
        if (memory.getRunningPCB()==null||memory.getRunningPCB().getStatus()==PCB.STATUS_HANG_OUT)
            memory.setRunningPCB(freePCB);
        else
            memory.getWaitPCB().offer(freePCB);

    }


}
