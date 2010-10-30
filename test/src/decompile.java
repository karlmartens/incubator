
public final class decompile {

	private static final String DATA = "010d95697b7b068622be01fd0ad4a3387ef475c1b7df80f0ed1286ceadfe9283521c0150f02cd80e227200630812cf231c62a8e7b0e200c156cc631920823a387f3eb42d32f585b8db12a2893508721cfb681d78c9d1fdaf0b4a979dbb1d";
	
	public static void main(String[] args) {
		final String s = hexDecode(DATA);
		
		System.out.print(s);
	}
	
	private static String hexDecode(String data) {
		final StringBuffer buf = new StringBuffer();		
		for (int i=0; i<DATA.length(); i+=2) {
			final String v = DATA.substring(i, i+2);
			final Integer ch = Integer.parseInt(v, 16);
			buf.append((char)ch.intValue());
		}
		return buf.toString();
	}
}
