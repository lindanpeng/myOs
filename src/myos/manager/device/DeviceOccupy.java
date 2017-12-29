package myos.manager.device;

import myos.manager.memory.PCB;
import myos.others.DelayItem;

import java.util.concurrent.TimeUnit;

/**
 * Created by lindanpeng on 2017/12/28.
 * 设备占用
 */
public class DeviceOccupy extends DelayItem<PCB> {
    private String deviceName;
    public DeviceOccupy(PCB obj, long workTime, TimeUnit timeUnit) {
        super(obj, workTime, timeUnit);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
