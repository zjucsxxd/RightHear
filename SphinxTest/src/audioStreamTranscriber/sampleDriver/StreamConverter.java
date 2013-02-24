package audioStreamTranscriber.sampleDriver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class StreamConverter {

	public static InputStream unsignedToSigned(final InputStream unsigned)
			throws IOException {
		PipedInputStream signed = new PipedInputStream();
		final PipedOutputStream out = new PipedOutputStream(signed);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int data = -1;
					while ((data = unsigned.read()) != -1) {
						out.write(data-127);
					}
					out.close();
					unsigned.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}).start();

		return signed;
	}

}
