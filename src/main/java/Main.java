import java.io.IOException;

/**
 * @author JeongHun, Lee
 */
public class Main {
	private static final String USER = "tester";

	public static void main(String[] args) throws IOException {
		Process process = new Process();
		process.doProcess(USER, 0, 4);
	}

}
