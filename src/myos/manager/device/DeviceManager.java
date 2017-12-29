package myos.manager.device;

import myos.manager.memory.PCB;
import myos.manager.process.CPU;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by lindanpeng on 2017/12/28.
 */
public class DeviceManager{
    private CPU cpu;
    private A a;
    private B b;
    private C c;
    //使用中的设备
    private DelayQueue<DeviceOccupy> usingDevices;
    //等待使用设备的进程队列
    private BlockingQueue<DeviceRequest> waitForDevice;

    public DeviceManager(CPU cpu){
        a=new A(2);//A设备2个
        b=new B(3);//B设备3个
        c=new C(3);//C设备3个
        usingDevices =new DelayQueue<>();
        waitForDevice=new ArrayBlockingQueue<>(10);
        this.cpu=cpu;
        //释放设备线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        DeviceOccupy deviceOccupy = usingDevices.take();
                        deviceDone(deviceOccupy);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        //处理设备申请请求线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        DeviceRequest deviceRequest=waitForDevice.take();
                        switch (deviceRequest.getDeviceName()){
                            case "A":
                                //如果有设备空闲就使用设备
                                if (a.getCount()>0){
                                    DeviceOccupy deviceOccupy=new DeviceOccupy(deviceRequest.getPcb(),deviceRequest.getWorkTime(), TimeUnit.MILLISECONDS);
                                    usingDevices.put(deviceOccupy);
                                    deviceRequest.getPcb().setEvent(PCB.EVENT_USING_DEVICE);
                                    //可用设备减1
                                    a.decreaseCount();
                                }
                                //否则将设备请求重新放到请求队列中
                                else {
                                    waitForDevice.put(deviceRequest);
                                }
                                break;
                            case "B":

                                break;
                            case "C":
                                break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }

    /**
     * 请求使用设备
     * @param
     */
    public void requestDevice(DeviceRequest deviceRequest){
        try {
            waitForDevice.put(deviceRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设备使用结束，释放资源，请求中断
     */
    private void deviceDone(DeviceOccupy deviceOccupy){
        //释放资源
        switch (deviceOccupy.getDeviceName()){
            case "A":a.increaseCount();break;
            case "B":b.increaseCount();break;
            case "C":c.increaseCount();break;
        }
       //将进程从阻塞队列中移到就绪队列
      cpu.awake(deviceOccupy.getObj());

    }

}
