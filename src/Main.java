import commands.*;
import managers.CollectionManager;
import managers.CommandManager;
import managers.FileManager;
import managers.InputManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        args = new String[]{"data.csv"};
        if (args.length == 0) {
            System.out.println("Please enter file name!\nsyntax: java Main + filename.csv");
            return;
        }

        CollectionManager cm = new CollectionManager();
        FileManager fm = new FileManager(args[0]);
        InputManager im = new InputManager(new Scanner(System.in));
        CommandManager cmdM = new CommandManager();

        fm.load(cm);

        cmdM.register(new HelpCommand(cmdM));
        cmdM.register(new InfoCommand(cm));
        cmdM.register(new ShowCommand(cm));
        cmdM.register(new AddCommand(cm, im));
        cmdM.register(new UpdateCommand(cm, im));
        cmdM.register(new RemoveByIdCommand(cm));
        cmdM.register(new ClearCommand(cm));
        cmdM.register(new SaveCommand(cm, fm));
        cmdM.register(new ExecuteScriptCommand(cmdM));
        cmdM.register(new ExitCommand());
        cmdM.register(new RemoveFirstCommand(cm));
        cmdM.register(new AddIfMinCommand(cm, im));
        cmdM.register(new RemoveLowerCommand(cm, im));
        cmdM.register(new SumTurnoverCommand(cm));
        cmdM.register(new FilterNameCommand(cm));
        cmdM.register(new PrintAddressAscCommand(cm));

        String filepath = args[0];
        System.out.println("Starting application with data file: " + filepath);
        System.out.println("Hello. Enter 'help' for commands.");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!sc.hasNextLine()) break;
            cmdM.execute(sc.nextLine());
        }
    }
}
