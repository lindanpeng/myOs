package myos.manager.process;

import myos.OS;
import myos.manager.device.DeviceManager;
import myos.manager.memory.Memory;
import myos.manager.memory.SubArea;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lindanpeng on 2017/12/24.
 */
public class CPU implements Runnable {
    static ReentrantLock lock = new ReentrantLock();
    //寄存器组
    private int IR;
    private int AX; //0
    private int BX; //1
    private int CX; //2
    private int DX; //3
    private int PC;

    private int nextIR;
    private int OP;
    private int DR;
    private int SR;
    private String result;
    private int deviceNum;
    private int deviceTime;

    private Memory memory;
    private DeviceManager deviceManager;
    public CPU() {
        this.memory = OS.memory;
        deviceManager=new DeviceManager(this);
        init();
    }

    /**
     * 初始化CPU
     */
    public void init(){
        IR=0;
        AX=0;
        BX=0;
        CX=0;
        DX=0;
        PC=0;
    }
    /**
     * 取值
     */
    public void fetchInstruction() {
        byte[] userArea = memory.getUserArea();
        IR = userArea[PC];
        if(IR !=0)           //NOP不执行
        {
            PC++;
        }
    //    System.out.println("取指完成，开始运行指令"+IR);
    }

    /**
     * 译码
     */
    public void identifyInstruction() {
        //移位
        OP = (IR>>4)&0x0f;
        DR = (IR>>2)&0x03;
        SR = IR & 0x03;

        if(OP == 5)
        {
            byte[] userArea = memory.getUserArea();
            nextIR = userArea[PC];
            PC++;
        }
   //     System.out.println("译码完成");
    }

    /**
     * 执行和写回
     */
    public void execute() {
        result ="NOP";
        if(IR !=0)
        {
            result ="";
            switch (OP) {
                case 1:switch (DR){  //ADD
                    case 0:AX++;result +="INC AX, AX="+AX;break;
                    case 1:BX++;result +="INC BX, BX="+BX;break;
                    case 2:CX++;result +="INC CX, CX=" +CX;break;
                    case 3:DX++;result +="INC DX, DX=" +DX;break;
                    }
                break;
                case 2:switch (DR){ //DEC
                    case 0:AX--;result +="DEC AX, AX="+AX;break;
                    case 1:BX--;result +="DEC BX, BX="+BX;break;
                    case 2:CX--;result +="DEC CX, CX="+ CX;break;
                    case 3:DX--;result +="DEC DX, DX="+ DX;break;
                    }
                    break;
                case 3:deviceNum = DR;  //!??
                        deviceTime =SR;
                        result +="! Device: "+DR+", Time:"+SR;
                    break;
                case 4:result += "END";
                        destroy();    //END
                        dispatch();

                    break;
                case 5:switch (DR){ //MOV
                    case 0:AX = nextIR;result +="MOV AX,"+nextIR+", AX="+AX;break;
                    case 1:BX = nextIR;result +="MOV BX,"+nextIR+", BX="+BX;break;
                    case 2:CX = nextIR;result +="MOV CX,"+nextIR+", CX="+ CX;break;
                    case 3:DX = nextIR;result +="MOV DX,"+nextIR+", DX="+ DX;break;
                }
                    break;
            }
        }
        //TODO 如果是end指令就进程调度
        System.out.println("指令"+IR+"运行完毕");

    }
    /**
     * 进程调度,将进程从就绪态恢复到运行态
     */
    public void dispatch() {
        PCB pcb1= memory.getRunningPCB();//当前运行的进程
        PCB pcb2=memory.getWaitPCB().poll();//要运行的进程
        lock.lock();
        try {

            if (pcb2==memory.getHangOutPCB()){
                pcb2=memory.getHangOutPCB();
            }
             memory.setRunningPCB(pcb2);
            pcb2.setStatus(PCB.STATUS_RUN);
            //保存现场
            if (pcb1 != memory.getHangOutPCB()) {
              saveContext(pcb1);
            }
            //恢复现场
            if (pcb2 != memory.getHangOutPCB()) {
               recoveryContext(pcb2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CPU.lock.unlock();
        }
        System.out.println("进程调度完成，当前运行进程为"+pcb2.getPID());
    }


    /**
     * 进程撤销
     */
    public void destroy(){
        lock.lock();
        PCB pcb=memory.getRunningPCB();
        System.out.println("进程"+pcb.getPID()+"运行结束,撤销进程");
        /*回收进程所占内存*/
        SubArea subArea=null;
        List<SubArea> subAreas=memory.getSubAreas();
        for (SubArea s:subAreas){
            if (s.getTaskNo()==pcb.getPID()){
                subArea=s;
                break;
            }
        }
        int index=subAreas.indexOf(subArea);
        if (index>0){
            SubArea preSubArea=subAreas.get(index-1);
            if(preSubArea.getStatus()==SubArea.STATUS_FREE) {
                preSubArea.setSize(preSubArea.getSize() + subArea.getSize());
                subAreas.remove(subArea);
                subArea=preSubArea;
            }
        }
        if (index<subAreas.size()-1){
            SubArea nextSubArea=subAreas.get(index+1);
            if (nextSubArea.getStatus()==SubArea.STATUS_FREE) {
                nextSubArea.setSize(nextSubArea.getSize() + subArea.getSize());
                subAreas.remove(subArea);
            }
        }
        lock.unlock();
//        SubArea subArea=null;
//        ListIterator<SubArea> it=memory.getSubAreas().listIterator();
//      //找到要撤销的进程所占用的分区块
//        while(it.hasNext()){
//            SubArea s=it.next();
//            if (s.getTaskNo()==pcb.getPID()) {
//                subArea = s;
//                break;
//            }
//        }
//        it.previous();
//        System.out.println("进程占用第"+memory.getSubAreas().indexOf(subArea)+"块");
//        subArea.setStatus(SubArea.STATUS_FREE);
//        //如果有前一个块
//        if (it.hasPrevious()){
//            SubArea pre=it.previous();
//            // 且前一个块是空闲块，则合并
//            if(pre.getStatus()==SubArea.STATUS_FREE){
//                pre.setSize(pre.getSize()+subArea.getSize());
//                //移除掉PCB对应的块
//                it.next();
//                it.remove();
//                it.previous();
//                subArea=pre;
//            }
//        }
//        //如果有后一个块
//        if (it.hasNext()){
//            SubArea next=it.next();
//            //且后一个块是空闲块，则合并
//            if (next.getStatus()==SubArea.STATUS_FREE){
//                subArea.setSize(subArea.getSize()+next.getSize());
//                it.remove();
//            }
//        }

    }

    /**
     * 将运行进程转换为就绪态
     */
    public void toReady(){
        PCB pcb=memory.getRunningPCB();
        if (pcb!=memory.getHangOutPCB()){
            memory.getWaitPCB().offer(pcb);
        }
        pcb.setStatus(PCB.STATUS_WAIT);
    }
    /**
     * 将运行进程转换为阻塞态
     */
    public void block(){
        PCB pcb=memory.getRunningPCB();
        //修改进程状态
        pcb.setStatus(PCB.STATUS_BLOCK);
        //将进程链入对应的阻塞队列，然后转向进程调度
        memory.getBlockPCB().add(pcb);
        dispatch();
    }

    /**
     * 进程唤醒
     */
    public void awake(PCB pcb){
        //将进程从阻塞队列中调入到就绪队列
        pcb.setStatus(PCB.STATUS_WAIT);
        pcb.setEvent(PCB.EVENT_NOTING);
        memory.getBlockPCB().remove(pcb);
        memory.getWaitPCB().add(pcb);
    }

    /**
     * 保存上下文
     * @param pcb
     */
    private void  saveContext(PCB pcb){
        System.out.println("保留现场");
        pcb.setStatus(PCB.STATUS_WAIT);
        pcb.setCounter(PC);
        pcb.setAX(this.AX);
        pcb.setBX(this.BX);
        pcb.setCX(this.CX);
        pcb.setDX(this.DX);
    }

    /**
     * 恢复现场
     */
    private void recoveryContext(PCB pcb){
        System.out.println("恢复现场");
        pcb.setStatus(PCB.STATUS_RUN);
        this.AX=pcb.getAX();
        this.BX=pcb.getBX();
        this.DX=pcb.getDX();
        this.CX=pcb.getCX();
        this.PC=pcb.getCounter();
    }

    @Override
    public void run() {
        while (OS.launched) {
            lock.lock();
            try {
                fetchInstruction();
                identifyInstruction();
                execute();
              //  System.out.println("就绪队列队头进程："+memory.getWaitPCB().peek().getPID());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            try {
                Thread.sleep(Clock.TIMESLICE_UNIT);
            } catch (InterruptedException e) {
                return;
            }


        }
    }

    public  int getDeviceNum()
    {
        return  deviceNum;
    }
    public int getDeviceTime()
    {
        return deviceTime;
    }
    public String getResult()
    {
        String temp;
        lock.lock();
        temp=result;
        lock.unlock();
        return temp;
    }


}

