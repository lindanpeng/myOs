package myos.manager.process;

import myos.OS;
import myos.constant.OsConstant;
import myos.manager.memory.Memory;
import myos.manager.memory.SubArea;

import java.util.ListIterator;

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

        /*申请空白进程块*/
        int pcbSize=memory.getAllPCB().size();
        if (pcbSize>=OsConstant.PROCESS_MAX)
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
        PCB newPCB=new PCB();

        //如果区域过大，分出一块新的空闲区成两块
       if (subArea.getSize()>program.length){
           int newSubAreaSize=subArea.getSize()-program.length;
           subArea.setSize(program.length);
           subArea.setTaskNo(newPCB.getPID());
           subArea.setStatus(SubArea.STATUS_BUSY);
           SubArea newSubArea=new SubArea();
           //新的空闲区域
           newSubArea.setStatus(SubArea.STATUS_FREE);
           newSubArea.setSize(newSubAreaSize);
           newSubArea.setStartAdd(subArea.getStartAdd()+subArea.getSize());
           it.add(newSubArea);
       }else {
           subArea.setSize(program.length);
           subArea.setTaskNo(newPCB.getPID());
           subArea.setStatus(SubArea.STATUS_BUSY);
       }
     //  System.out.println("进程首地址："+subArea.getStartAdd());
       //将数据复制到用户区
        byte[] userArea=memory.getUserArea();
       for (int i=subArea.getStartAdd(),j=0;i<subArea.getStartAdd()+subArea.getSize();i++,j++){
           userArea[i]=program[j];
       }
       System.out.println("创建的进程ID"+newPCB.getPID());
        //初始化进程控制块
        newPCB.setMemStart(subArea.getStartAdd());
        newPCB.setMemEnd(program.length);
        newPCB.setCounter(subArea.getStartAdd());
        newPCB.setStatus(PCB.STATUS_WAIT);
        memory.getWaitPCB().offer(newPCB);//进程就绪
        //判断当前是否有实际运行进程，没有的则申请进程调度
        if (memory.getRunningPCB()==null||memory.getRunningPCB()==memory.getHangOutPCB()) {
            System.out.println("申请进程调度");
            cpu.lock.lock();
            cpu.toReady();
            cpu.dispatch();
            cpu.lock.unlock();
        }


    }

}
