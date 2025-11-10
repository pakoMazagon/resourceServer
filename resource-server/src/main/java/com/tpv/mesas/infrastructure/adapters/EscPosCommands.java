package com.tpv.mesas.infrastructure.adapters;

public class EscPosCommands {

    // Comandos ESC/POS comunes
    public static final byte ESC = 0x1B;
    public static final byte GS = 0x1D;

    public static final byte[] INIT = new byte[]{ESC, '@'};             // Reset impresora
    public static final byte[] BOLD_ON = new byte[]{ESC, 'E', 1};
    public static final byte[] BOLD_OFF = new byte[]{ESC, 'E', 0};
    public static final byte[] CENTER = new byte[]{ESC, 'a', 1};
    public static final byte[] LEFT = new byte[]{ESC, 'a', 0};
    public static final byte[] RIGHT = new byte[]{ESC, 'a', 2};
    public static final byte[] CUT = new byte[]{GS, 'V', 1};            // Corte de papel

    // Selección de tabla de caracteres (CP858)
    public static final byte[] SELECT_CP858 = new byte[]{0x1B, 't', 19};

    public static final byte[] DOUBLE_ON = new byte[]{0x1B, 0x21, 0x30}; // doble ancho + alto
    public static final byte[] DOUBLE_OFF = new byte[]{0x1B, 0x21, 0x00};

    public static final byte[] TEXT_MEDIUM = new byte[]{0x1D, 0x21, 0x01}; // un poco más alto
    public static final byte[] TEXT_WIDE = new byte[]{0x1D, 0x21, 0x10};   // un poco más ancho
    public static final byte[] TEXT_LARGE = new byte[]{0x1D, 0x21, 0x11};  // doble ancho y alto (actual)

}
