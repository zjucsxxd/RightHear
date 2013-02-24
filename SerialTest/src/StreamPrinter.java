import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamPrinter implements Runnable {

	private InputStream in;

	public StreamPrinter(InputStream in) {
		this.in = in;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			in.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
