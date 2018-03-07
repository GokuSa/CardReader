package com.shine.printer_module;

public class PrinterConstants {
    public static class BarcodeType {
        public static final byte UPC_A = 0;
        public static final byte UPC_E = 1;
        public static final byte JAN13 = 2;
        public static final byte JAN8 = 3;
        public static final byte CODE39 = 4;
        public static final byte ITF = 5;
        public static final byte CODABAR = 6;
        public static final byte CODE93 = 72;
        public static final byte CODE128 = 73;
        public static final byte PDF417 = 100;
        public static final byte DATAMATRIX = 101;
        public static final byte QRCODE = 102;
    }

    public static class Connect {
        public static final int SUCCESS = 101;
        public static final int FAILED = 102;
        public static final int CLOSED = 103;
        public static final int NODEVICE = 104;
        public static final int RECONNECT = 105;
    }

    public static class Device {
        public static final int FOUND = 1;
        public static final int FINISHED = 2;
    }

    public static class Paper {
        public static final int CONNECT_EXCEPTION = -1;
        public static final int NORMAL = 0;
        public static final int OUT_OF_PAPER = 1;
        public static final int PAPER_WILL_BE_OUT = 2;
        public static final int COVER_OPEN = 3;
    }
    public static class Command {
        public static final int INIT_PRINTER = 0;
        public static final int WAKE_PRINTER = 1;
        public static final int PRINT_AND_RETURN_STANDARD = 2;
        public static final int PRINT_AND_NEWLINE = 3;
        public static final int PRINT_AND_ENTER = 4;
        public static final int MOVE_NEXT_TAB_POSITION = 5;
        public static final int DEF_LINE_SPACING = 6;
        public static final int PRINT_AND_WAKE_PAPER_BY_LNCH = 0;
        public static final int PRINT_AND_WAKE_PAPER_BY_LINE = 1;
        public static final int CLOCKWISE_ROTATE_90 = 4;
        public static final int ALIGN = 13;
        public static final int ALIGN_LEFT = 0;
        public static final int ALIGN_CENTER = 1;
        public static final int ALIGN_RIGHT = 2;
        public static final int LINE_HEIGHT = 10;
        public static final int CHARACTER_RIGHT_MARGIN = 11;

        public static final int FONT_MODE = 0x10;
        public static final int FONT_SIZE = 0x11;
    }
}
