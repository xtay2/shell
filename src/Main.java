public class Main {
	public static void main(String[] args) {
		Shell shell1 = new Shell(
				"MyShell",
				new ExitCommand() {
					@Override
					public String execute(String input) {
						return "Bye!";
					}
				}
		);
		System.out.println("Shell 1 status: " + (shell1.isRunning() ? "running" : "closed"));

	}
}