package com.zeoflow.numericaltx.swing;

import com.zeoflow.numericaltx.awt.Graphics;
import com.zeoflow.numericaltx.awt.Component;


public interface Icon {
    void paintIcon(Component c, Graphics g, int x, int y);

    int getIconWidth();

    int getIconHeight();
}

