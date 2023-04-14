import static java.util.Arrays.stream;

public abstract class Command {

	public final String[] names;

	public final String description;

	public Command(String name, String description) {
		this.names = new String[]{name};
		this.description = description;
	}

	public Command(String[] names, String description) {
		this.names = names;
		this.description = description;
	}

	public final boolean matches(String input) {
		return stream(names).anyMatch(input::startsWith);
	}

	public abstract String execute(String input);

}
