package myos.manager.device;

import myos.manager.process.CPU;

import java.util.concurrent.DelayQueue;

/**
 * Created by lindanpeng on 2017/12/28.
 */
public class DeviceInterruptManager implements Runnable{
    private CPU cpu;
    private DelayQueue<Device> queue;
    public DeviceInterruptManager(CPU cpu){
        queue=new DelayQueue<>();
        this.cpu=cpu;
    }

    /**
     * 使用设备
     * @param device
     */
    public void useDevice(Device device){
        queue.offer(device);
    }

    /**
     * 设备中断
     */
    private void deviceInterrupt(Device device){
        System.out.println("设备"+device.getName()+"使用结束,恢复阻塞进程"+device.getPID());
        //
    }
    @Override
    public void run() {
        while(true){
            try {
                Device device=queue.take();
                deviceInterrupt(device);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
