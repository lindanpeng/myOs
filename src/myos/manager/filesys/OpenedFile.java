package myos.manager.filesys;

/**
 * Created by lindanpeng on 2017/12/6.
 *已打开文件
 */
public class OpenedFile {
    public static final int OP_TYPE_READ=0;//读方式打开文件
    public static final int OP_TYPE_WRITE=1;//写方式打开文件
    public static final int OP_TYPE_READ_WRITE=2;//读写模式
    public static final int OP_TYPE_RUN=3;//执行模式
    //文件路径名
    private String filePath;
    //文件目录项
    private Catalog catalog;
    //操作类型
    private int opType;
    //读指针
    private Pointer readPointer;
    //写指针
    private Pointer writePointer;


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }



    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public Pointer getReadPointer() {
        return readPointer;
    }

    public void setReadPointer(Pointer readPointer) {
        this.readPointer = readPointer;
    }

    public Pointer getWritePointer() {
        return writePointer;
    }

    public void setWritePointer(Pointer writePointer) {
        this.writePointer = writePointer;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }
}
class Pointer{
    //块号
    private int blockNo;
    //块内地址
    private int address;

    public int getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(int blockNo) {
        this.blockNo = blockNo;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

}
