/*
 * Page.java
 * Author : 박찬형
 * Created Date : 2020-11-10
 */
package UI.page;

import javax.swing.*;

public abstract class Page {
    protected JPanel page;

    public Page(){
        page = new JPanel();
        initPage();
    }

    protected abstract void initPage();
    protected abstract void setView();

    public JPanel getPanel(){
        return page;
    }
}
