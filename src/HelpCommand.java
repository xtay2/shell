public abstract class HelpCommand extends Command {

	public HelpCommand() {
		this("help", "Shows a list of the available commands and their description");
	}

	public HelpCommand(String name, String description) {
		super(name, description);
	}

	public HelpCommand(String[] names, String description) {
		super(names, description);
	}

}
