package myos.manager.process;

import myos.constant.OsConstant;
import myos.manager.filesys.FileOperator;
import myos.manager.filesys.OpenedFile;
import myos.manager.memory.Memory;
import myos.manager.memory.PCB;
import myos.manager.memory.SubArea;

import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Queue;

/**
 * Created by lindanpeng on 2017/12/24.
 * 进程管理类
 */
public class ProcessOperator {
    private Memory memory;
    private FileOperator fileOperator;
    public ProcessOperator(Memory memory,FileOperator fileOperator){
        this.memory=memory;
        this.fileOperator=fileOperator;
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
        //初始化进程控制块
        freePCB.setMemStart(subArea.getStartAdd());
        freePCB.setLength(program.length);
        freePCB.setCounter(0);
        freePCB.setStatus(PCB.STATUS_WAIT);
        memory.getWaitPCB().offer(freePCB);
    }
    public void destory(PCB pcb){
        /*回收进程所占内存*/
        SubArea subArea=null;
        ListIterator<SubArea> it=memory.getSubAreas().listIterator();
        while(it.hasNext()){
            SubArea s=it.next();
            if (s.getTaskNo()==pcb.getPID()) {
                subArea = s;
                break;
            }
        }
        subArea.setStatus(SubArea.STATUS_FREE);
        //如果有前一个块
        if (it.hasPrevious()){
            SubArea pre=it.previous();
           // 且前一个块是空闲块，则合并
            if(pre.getStatus()==SubArea.STATUS_FREE){
                pre.setSize(pre.getSize()+subArea.getSize());
                //移除掉PCB对应的块
                it.next();
                it.remove();
                it.previous();
                subArea=pre;
            }
        }
        //如果有后一个块
        if (it.hasNext()){
            SubArea next=it.next();
            //且后一个块是空闲块，则合并
            if (next.getStatus()==SubArea.STATUS_FREE){
                subArea.setSize(subArea.getSize()+next.getSize());
                it.remove();
            }
        }
        //TODO 进程控制块回收以及显示结果在CPU处理


    }
    public void block(){
        //保存运行进程的CPU现场
        //修改进程状态
        //将进程链入对应的阻塞队列，然后转向进程调度
    }
    public void awake(){
        //将进程从阻塞队列中调入到就绪队列
    }
}
