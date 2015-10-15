




import java.io.*;




public class Test  {
	public static void main(String value) throws Throwable {
		// 可自定HTML版本各欄位的onChange 的動作
		// 傳入值 value
		copyFileUsingStream(new File("C:\\123.txt"), new File("D:\\test2.txt"));
	
	}

	
	private static void copyFileUsingStream(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
}
