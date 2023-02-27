public class jniAES {
	/* Native method declaration */
	public native void encAES(byte[] Msg, byte[] Key, byte[] IV, byte[] Cryp);
	public native void decAES(byte[] Cryp, byte[] Key, byte[] IV, byte[] Msg);
	public native byte findRes(byte[] Cryp, byte[] Key, byte[] IV);

	/* Use static intializer */
	static {
		System.loadLibrary("AESLib");
    	}
}

