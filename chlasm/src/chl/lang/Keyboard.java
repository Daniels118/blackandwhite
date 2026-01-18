package chl.lang;

public final class Keyboard {
	public static final int KB_NONE = 0x000;
	public static final int KB_ESC = 0x001;
	public static final int KB_1 = 0x002;
	public static final int KB_2 = 0x003;
	public static final int KB_3 = 0x004;
	public static final int KB_4 = 0x005;
	public static final int KB_5 = 0x006;
	public static final int KB_6 = 0x007;
	public static final int KB_7 = 0x008;
	public static final int KB_8 = 0x009;
	public static final int KB_9 = 0x00a;
	public static final int KB_0 = 0x00b;
	public static final int KB_MINUS = 0x00c;
	public static final int KB_EQUAL = 0x00d;
	public static final int KB_BACKSPACE = 0x00e;
	public static final int KB_TAB = 0x00f;
	public static final int KB_Q = 0x010;
	public static final int KB_W = 0x011;
	public static final int KB_E = 0x012;
	public static final int KB_R = 0x013;
	public static final int KB_T = 0x014;
	public static final int KB_Y = 0x015;
	public static final int KB_U = 0x016;
	public static final int KB_I = 0x017;
	public static final int KB_O = 0x018;
	public static final int KB_P = 0x019;
	public static final int KB_LSBRACKET = 0x01a;
	public static final int KB_RSBRACKET = 0x01b;
	public static final int KB_RETURN = 0x01c;
	public static final int KB_LCTRL = 0x01d;

	public static final int KB_A = 0x01e;
	public static final int KB_S = 0x01f;
	public static final int KB_D = 0x020;
	public static final int KB_F = 0x021;
	public static final int KB_G = 0x022;
	public static final int KB_H = 0x023;
	public static final int KB_J = 0x024;
	public static final int KB_K = 0x025;
	public static final int KB_L = 0x026;
	public static final int KB_COLON = 0x027;
	public static final int KB_QUOTE = 0x028;
	public static final int KB_QUOTE2 = 0x029;
	public static final int KB_LSHIFT = 0x02a;
	public static final int KB_HASH = 0x02b;
	public static final int KB_BACKSLASH = 0x056;
	public static final int KB_Z = 0x02c;
	public static final int KB_X = 0x02d;
	public static final int KB_C = 0x02e;
	public static final int KB_V = 0x02f;
	public static final int KB_B = 0x030;
	public static final int KB_N = 0x031;
	public static final int KB_M = 0x032;
	public static final int KB_COMMA = 0x033;
	public static final int KB_DOT = 0x034;
	public static final int KB_SLASH = 0x035;
	public static final int KB_RSHIFT = 0x036;

	public static final int KB_LALT = 0x038;
	public static final int KB_SPACE = 0x039;
	public static final int KB_CAPS = 0x03a;
	public static final int KB_F1 = 0x03b;
	public static final int KB_F2 = 0x03c;
	public static final int KB_F3 = 0x03d;
	public static final int KB_F4 = 0x03e;
	public static final int KB_F5 = 0x03f;
	public static final int KB_F6 = 0x040;
	public static final int KB_F7 = 0x041;
	public static final int KB_F8 = 0x042;
	public static final int KB_F9 = 0x043;
	public static final int KB_F10 = 0x044;
	public static final int KB_F11 = 0x057;
	public static final int KB_F12 = 0x058;
	public static final int KB_SCROLL_LOCK = 0x046;
	public static final int KB_NUM_LOCK = 0x045;
	public static final int KB_PMINUS = 0x04a;
	public static final int KB_PLUS = 0x04e;
	public static final int KB_ASTERISK = 0x037;
	public static final int KB_PDOT = 0x053;
	
	// Extended Keys
	public static final int KB_RALT = 0x038 + 0x80;
	public static final int KB_RCTRL = 0x01d + 0x80;
	public static final int KB_PRINT_SCR = 0x037 + 0x80;

	public static final int KB_HOME = 0x047 + 0x80;
	public static final int KB_UP = 0x048 + 0x80;
	public static final int KB_PGUP = 0x049 + 0x80;
	public static final int KB_LEFT = 0x04b + 0x80;
	public static final int KB_RIGHT = 0x04d + 0x80;
	public static final int KB_END = 0x04f + 0x80;
	public static final int KB_DOWN = 0x050 + 0x80;
	public static final int KB_PGDN = 0x051 + 0x80;
	public static final int KB_INSERT = 0x052 + 0x80;
	public static final int KB_DELETE = 0x053 + 0x80;
	public static final int KB_ENTER = 0x01c + 0x80;

	// Windows Keys
	public static final int KB_LWIN = 0xDB;
	public static final int KB_RWIN = 0xDC;
	public static final int KB_WAPP = 0xDD;
	
	private Keyboard() {}
	
	@Action("KEY_DOWN(code)")
	public static boolean isPressed(int code) {return false;};
}
