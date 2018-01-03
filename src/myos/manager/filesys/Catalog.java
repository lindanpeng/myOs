package myos.manager.filesys;

import myos.OS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindanpeng on 2017/12/6.
 *目录/文件登记项
 */
public class Catalog {
    //目录项占用空间
    private byte[] bytes;
    //文件名
    private String name;
//    //文件类型，如果是目录则为空格
//    private String type;
    //文件属性
    private int property;
    //文件内容起始盘号
    private int startBlock;
    //文件长度，单位为盘块，如果是目录则为0
    private int fileLength;
    /*方便操作的附加属性*/
    //是否是可执行文件
    public boolean executable;
    //是否是目录
    private boolean isDirectory;
    //目录所在磁盘块号
    private int catalogBlock;
    //是否为空
    private boolean isBlank;

    public void setBlank(boolean blank) {
        isBlank = blank;
    }

    public Catalog(byte[] bytes){
        this.bytes=bytes;
        this.name=new String(bytes,0,5);
      //  this.type=new String(bytes,3,2);
        setProperty(bytes[5]);
        this.startBlock=bytes[6];
        if (property>>4==1){
            executable=true;
        }
        else if (property>>3==1){
            isDirectory=true;
        }
        if (startBlock==-1){
            isBlank=true;
        }else
            isBlank=false;

        this.fileLength=bytes[7];

    }
    public Catalog(String fileName,int property) throws Exception {
        this.bytes=new byte[8];
        this.setStartBlock(-1);
        this.setDirectory(false);
        this.setFileLength(0);
        this.setName(fileName);
        this.setProperty(property);
        if (property==8){
            isDirectory=true;
        }
        else
            isDirectory=false;
     //   this.setType("  ");
        this.isBlank=true;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) throws Exception {
        this.name = name;
        byte[] nameBytes=name.getBytes();
        if (nameBytes.length>5)
            throw new Exception("文件名过长！");
       for (int i=0;i<nameBytes.length;i++){
           bytes[i]=nameBytes[i];
       }
    }

//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) throws Exception {
//        this.type = type;
//        byte[] typeBytes=type.getBytes();
//        if (type.length()>2)
//            throw new Exception("类型名过长");
//        bytes[3]=typeBytes[0];
//        bytes[4]=typeBytes[1];
//    }

    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
        bytes[5]=(byte)property;
        if (property>>4==1){
            executable=true;
        }else{
            executable=false;
        }
         if (property>>3==1){
            isDirectory=true;
        }else {
             isDirectory=false;
         }
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

    public boolean isBlank() {
        return isBlank;
    }


    public List<Catalog> list() throws IOException {
        List<Catalog> catalogs=new ArrayList<>();
        Catalog catalog= OS.fileOperator.readCatalog(catalogBlock);
        int nextBlock=catalog.getStartBlock();
        while(nextBlock!=-1){
            Catalog c=OS.fileOperator.readCatalog(nextBlock);
            catalogs.add(c);
            nextBlock=OS.fileOperator.getNextBlock(nextBlock);
        }
        return  catalogs;
    }

    public boolean isExecutable() {
        return executable;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Catalog catalog = (Catalog) o;

        if (catalogBlock != catalog.catalogBlock) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + startBlock;
        return result;
    }

}
