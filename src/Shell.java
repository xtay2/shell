import java.io.*;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class Shell implements AutoCloseable {

	private Scanner input;
	private OutputStream output;
	private String name;
	private Supplier<String> startup;
	private BiFunction<String, Command[], String> unknownCmd;
	private boolean running = false;
	private Command[] commands;

	public Shell(String name, Command... commands) {
		this(
				name,
				() -> "Welcome to " + name + "!\n",
				(in, cms) -> stream(commands)
						.map(c -> c.description)
						.collect(joining("\n", "Unknown command: " + in + "\n", "\n")),
				commands
		);
	}


	/**
	 * Creates a new Shell with the given name, startup message and commands.
	 * The input and output streams are set to {@link System#in} and {@link System#out}.
	 */
	public Shell(
			String name,
			Supplier<String> startup,
			BiFunction<String, Command[], String> unknownCmd,
			Command... commands
	) {
		this(name, System.in, System.out, startup, unknownCmd, commands);
	}

	/**
	 * Creates a new Shell with the given name, input, output, startup message and commands.
	 *
	 * @param name       The name that gets displayed before the input prompt.
	 * @param input      The input stream to read from.
	 * @param output     The output stream to write to.
	 * @param startup    This function gets called when the shell starts up.
	 *                   The return value gets written to the output stream.
	 * @param unknownCmd This function gets called when the user enters an unknown command.
	 *                   The first parameter is the input string,
	 *                   the second parameter is the array of existing commands.
	 * @param commands   The commands that the shell can execute. Must contain an {@link ExitCommand}.
	 */
	public Shell(
			String name,
			InputStream input,
			OutputStream output,
			Supplier<String> startup,
			BiFunction<String, Command[], String> unknownCmd,
			Command... commands
	) {
		this.name = name;
		this.input = new Scanner(input);
		this.output = output;
		this.startup = startup;
		this.unknownCmd = unknownCmd;
		this.commands = commands;
		if (stream(commands).noneMatch(c -> c instanceof ExitCommand))
			throw new IllegalArgumentException("No ExitCommand found.");
		new Thread(this::run).start();
	}

	private void run() {
		if (isRunning())
			throw new IllegalStateException("Shell is already running.");
		running = true;
			write(startup.get());
			do {
				write(name + "> ");
				var output = execute(input.nextLine());
				write(output);
			} while (running);
	}

	private String execute(String input) {
		return stream(commands)
				.filter(c -> c.matches(input))
				.findFirst()
				.map(c -> {
					var out = c.execute(input);
					if (c instanceof ExitCommand)
						close(out);
					return out;
				})
				.orElseGet(() -> unknownCmd.apply(input, commands));
	}


	/**
	 * Writes the given message to the output stream.
	 * If the message is null, nothing happens.
	 */
	private void write(String msg) {
		if (msg == null) return;
		try {
			for (char c : msg.toCharArray())
				output.write(c);
			output.flush();
		} catch (IOException e) {
			close();
		}
	}

	public boolean isClosed() {
		return !running;
	}

	public boolean isRunning() {
		return running;
	}

	/** Closes this shell and writes the given exit message to the output stream. */
	public void close(String exitMessage) {
		write(exitMessage);
		close();
	}

	/** Closes this shell and the corresponding streams. */
	@Override
	public void close() {
		if (isClosed())
			throw new IllegalStateException(name + " was already closed.");
		try {
			input.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			running = false;
		}
	}
}
