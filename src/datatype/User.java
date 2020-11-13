/*
 * User.java
 * Author : 박찬형
 * Created Date : 2020-11-11
 */
package datatype;

import javax.swing.*;

public class User {
    private String name;
    private ImageIcon characterIcon;

    public User(String name, ImageIcon characterIcon){
        this.name = name;
        this.characterIcon = characterIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageIcon getCharacterIcon() {
        return characterIcon;
    }

    public void setCharacterIcon(ImageIcon characterIcon) {
        this.characterIcon = characterIcon;
    }
}
