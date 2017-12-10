package myos.manager.filesys;

import java.io.File;

/**
 * Created by lindanpeng on 2017/10/15.
 */
public class MyFile {
    /*文件是否可读*/
    private boolean canRead;
    /*文件是否可写*/
    private boolean canWrite;
    /*文件是否可执行*/
    private boolean canExecute;
    /*是否目录*/
    private boolean isDir;
    /*文件长度*/
    private int length;
    /*文件的物理位置*/
    private int position;
    /*文件内容*/
    private byte[] content;

    public boolean isCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    public boolean isCanExecute() {
        return canExecute;
    }

    public void setCanExecute(boolean canExecute) {
        this.canExecute = canExecute;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

}
