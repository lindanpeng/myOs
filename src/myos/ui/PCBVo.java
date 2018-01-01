package myos.ui;

import javafx.beans.property.SimpleStringProperty;
import myos.manager.process.PCB;

/**
 * Created by lindanpeng on 2017/12/30.
 */
public class PCBVo {
    private SimpleStringProperty PID;
    private SimpleStringProperty status;
    private SimpleStringProperty priority;
    public PCBVo(PCB pcb){
        this.PID=new SimpleStringProperty(pcb.getPID()+"");
        this.status=new SimpleStringProperty(pcb.getStatus());
        this.priority=new SimpleStringProperty(pcb.getPriority()+"");
    }
    public String getPID() {
        return PID.get();
    }

    public SimpleStringProperty PIDProperty() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID.set(PID);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }



    public String getPriority() {
        return priority.get();
    }

    public SimpleStringProperty priorityProperty() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority.set(priority);
    }
}
