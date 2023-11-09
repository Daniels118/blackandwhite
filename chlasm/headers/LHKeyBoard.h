#ifndef __LHKEYBOARD_H__
#define __LHKEYBOARD_H__

//*****************************************************************************
//	LH_KEY contains all the keycodes generated by extended keyboard.
//	InKey() holds these LH_KEY values and these are also the indexes into
//	the KeyDownArray[].
//*****************************************************************************

enum LH_KEY
{
	KB_NONE			= 0x000,
	KB_ESC			= 0x001,
	KB_ESCAPE		= 0x001,						// Duplicate
	KB_1			= 0x002,
	KB_2			= 0x003,
	KB_3			= 0x004,
	KB_4			= 0x005,
	KB_5			= 0x006,
	KB_6			= 0x007,
	KB_7			= 0x008,
	KB_8			= 0x009,
	KB_9			= 0x00a,
	KB_0			= 0x00b,
	KB_MINUS		= 0x00c,
	KB_EQUAL		= 0x00d,
	KB_BACKSPACE	= 0x00e,
	KB_TAB			= 0x00f,
	KB_Q			= 0x010,
	KB_W			= 0x011,
	KB_E			= 0x012,
	KB_R			= 0x013,
	KB_T			= 0x014,
	KB_Y			= 0x015,
	KB_U			= 0x016,
	KB_I			= 0x017,
	KB_O			= 0x018,
	KB_P			= 0x019,
	KB_LSBRACKET	= 0x01a,
	KB_RSBRACKET	= 0x01b,
	KB_RETURN		= 0x01c,
	KB_LCTRL		= 0x01d,

	KB_A			= 0x01e,
	KB_S			= 0x01f,
	KB_D			= 0x020,
	KB_F			= 0x021,
	KB_G			= 0x022,
	KB_H			= 0x023,
	KB_J			= 0x024,
	KB_K			= 0x025,
	KB_L			= 0x026,
	KB_COLON		= 0x027,
	KB_QUOTE		= 0x028,
	KB_QUOTE2		= 0x029,
	KB_LSHIFT		= 0x02a,
	KB_HASH			= 0x02b,
	KB_BACKSLASH	= 0x056,
	KB_Z			= 0x02c,
	KB_X			= 0x02d,
	KB_C			= 0x02e,
	KB_V			= 0x02f,
	KB_B			= 0x030,
	KB_N			= 0x031,
	KB_M			= 0x032,
	KB_COMMA		= 0x033,
	KB_DOT			= 0x034,
	KB_SLASH		= 0x035,
	KB_RSHIFT		= 0x036,

	KB_LALT			= 0x038,
	KB_SPACE		= 0x039,
	KB_CAPS			= 0x03a,
	KB_F1			= 0x03b,
	KB_F2			= 0x03c,
	KB_F3			= 0x03d,
	KB_F4			= 0x03e,
	KB_F5			= 0x03f,
	KB_F6			= 0x040,
	KB_F7			= 0x041,
	KB_F8			= 0x042,
	KB_F9			= 0x043,
	KB_F10			= 0x044,
	KB_F11			= 0x057,
	KB_F12			= 0x058,
	KB_SCROLL_LOCK	= 0x046,
	KB_NUM_LOCK		= 0x045,	
	KB_PMINUS		= 0x04a,		
	KB_PLUS			= 0x04e,
	KB_ASTERISK		= 0x037,	
	KB_PDOT			= 0x053,	
	
	// Extended Keys
	
	KB_RALT			= 0x038 + 0x80,
	KB_RCTRL		= 0x01d + 0x80,
	KB_PRINT_SCR	= 0x037 + 0x80,

	KB_HOME			= 0x047 + 0x80,
	KB_UP			= 0x048 + 0x80,
	KB_PGUP			= 0x049 + 0x80,
	KB_LEFT			= 0x04b + 0x80,
	KB_RIGHT		= 0x04d + 0x80,
	KB_END			= 0x04f + 0x80,
	KB_DOWN			= 0x050 + 0x80,
	KB_PGDN			= 0x051 + 0x80,
	KB_INSERT		= 0x052 + 0x80,
	KB_DELETE		= 0x053 + 0x80,
	KB_ENTER		= 0x01c + 0x80,

	// Windows Keys

	KB_LWIN			= 0xDB,
	KB_RWIN			= 0xDC,
	KB_WAPP			= 0xDD

};

enum	LH_SHIFT
{
	LH_NO_SHIFT		= 0,
	LH_SHIFT		= 128,
};

//*****************************************************************************
//	Some handy ascii values
//*****************************************************************************

enum LH_ASCII_KEY_VALUES
{
	ASC_TAB			= 0x009,	
	ASC_NEWLINE		= 0x00a,
	ASC_SPACE		= 0x020,
};


#endif //__LHKEYBOARD_H__
