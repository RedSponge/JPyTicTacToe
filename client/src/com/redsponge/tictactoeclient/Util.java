package com.redsponge.tictactoeclient;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Color;

public class Util {

    public static Border getTitledBorder(String title) {
        return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title);
    }

    public static int signToPlayer(String sign) {
        return sign.equals("X")?Constants.X_MARK:sign.equals("O")?Constants.O_MARK:0;
    }

}
