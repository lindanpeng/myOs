package myos.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import myos.manager.filesys.Catalog;

import java.io.IOException;

/**
 * Created by lindanpeng on 2017/12/29.
 */
public class MyTreeItem extends TreeItem<Catalog>{
    private boolean notInitialized = true;
    public MyTreeItem(final Catalog catalog){
        super(catalog);
    }
    @Override
    public boolean isLeaf(){
        return !getValue().isDirectory();
    }
    @Override
    public ObservableList<TreeItem<Catalog>> getChildren(){
        if(notInitialized){
            notInitialized = false;
            if(getValue().isDirectory()&&!getValue().isBlank()){
                try {
                    for (Catalog c:getValue().list()){
                        super.getChildren().add(new MyTreeItem(c));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.getChildren();
    }
}
