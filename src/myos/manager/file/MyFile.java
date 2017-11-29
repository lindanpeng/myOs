package myos.manager.file;

import java.io.File;
import java.util.Date;

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
    private long length;
    /*文件的物理位置*/
    private int position;
    /*文件内容*/
    private byte[] content;
    private File file;

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


    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
