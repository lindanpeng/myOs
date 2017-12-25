package myos.manager.filesys;

/**
 * Created by lindanpeng on 2017/12/6.
 *目录/文件登记项
 */
public class Catalog {
    //目录项占用空间
    private byte[] bytes;
    //文件名
    private String name;
    //文件类型，如果是目录则为空格
    private String type;
    //文件属性
    private int property;
    //文件内容起始盘号
    private int startBlock;
    //文件长度，单位为盘块，如果是目录则为0
    private int fileLength;
    /*方便操作的附加属性*/

    //是否是目录
    private boolean isDirectory;
    //目录所在磁盘块号
    private int catalogBlock;

    public Catalog(byte[] bytes){
        this.bytes=bytes;
        this.name=new String(bytes,0,3);
        this.type=new String(bytes,3,2);
        this.property=bytes[5];
        this.startBlock=bytes[6];
        this.fileLength=bytes[7];
        if (property>>3==1){
            isDirectory=true;
        }
        else
            isDirectory=false;
    }
    public Catalog(String fileName,int property) throws Exception {
        this.bytes=new byte[8];
        this.setStartBlock(-1);
        this.setDirectory(false);
        this.setFileLength(0);
        this.setName(fileName);
        this.setProperty(property);
        this.setType("  ");
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) throws Exception {
        this.name = name;
        byte[] nameBytes=name.getBytes();
        if (nameBytes.length>3)
            throw new Exception("文件名过长！");
       for (int i=0;i<nameBytes.length;i++){
           bytes[i]=nameBytes[i];
       }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) throws Exception {
        this.type = type;
        byte[] typeBytes=type.getBytes();
        if (type.length()>2)
            throw new Exception("类型名过长");
        bytes[3]=typeBytes[0];
        bytes[4]=typeBytes[1];
    }

    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
        bytes[5]=(byte)property;
    }

    public int getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(int startBlock) {
        this.startBlock = startBlock;
        bytes[6]=(byte)startBlock;
    }

    public int getFileLength() {
        return fileLength;
    }

    public void setFileLength(int fileLength) {
        this.fileLength = fileLength;
        bytes[7]=(byte) fileLength;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getCatalogBlock() {
        return catalogBlock;
    }

    public void setCatalogBlock(int catalogBlock) {
        this.catalogBlock = catalogBlock;
    }


}
