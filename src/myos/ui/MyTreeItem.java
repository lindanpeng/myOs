package myos.ui;

import javafx.scene.control.TreeItem;
import myos.manager.filesys.Catalog;

/**
 * Created by lindanpeng on 2017/12/29.
 */
public class MyTreeItem extends TreeItem<Catalog>{
    public MyTreeItem(final Catalog catalog){
        super(catalog);
    }
    @Override
    public boolean isLeaf(){
        return !getValue().isDirectory()||getValue().isBlank();
    }
}
