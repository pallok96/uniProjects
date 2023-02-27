public class jniRSA {
	/* Native method declaration */
	public native String genKeyRSA();
	public native String encRSA(byte[] M, String str_n, String str_e);
	public native byte[] decRSA(String C, String str_n, String str_e, String str_p, String str_q, String str_d);

	/* Use static intializer */
	static {
		System.loadLibrary("RSALib");
    	}
}

