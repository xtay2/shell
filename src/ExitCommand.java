public abstract class ExitCommand extends Command {

	public ExitCommand() {
		this("exit", "Exits the shell");
	}

	public ExitCommand(String name, String description) {
		super(name, description);
	}

	public ExitCommand(String[] names, String description) {
		super(names, description);
	}

}
