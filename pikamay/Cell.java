/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pikamay;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author Administrator
 */
public class Cell extends JButton {

    public static final int SIZE = 36;

    private String _img;
    public int i;
    public int j;

    public Cell(String img) {
        _img = img;
        setSize(SIZE, SIZE);
        if (!img.isEmpty()) {
            setIcon(new ImageIcon(getClass().getResource("/img/" + img + ".png")));
        }
        setFocusable(false);
    }

    public String getImg() {
        return _img;
    }

    public void setImg(String img) {
        this._img = img;
    }

}
