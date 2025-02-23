package ir.ie.mizdooni;

import ir.ie.mizdooni.commons.Request;
import ir.ie.mizdooni.controllers.MizDooniController;
import ir.ie.mizdooni.utils.Parser;

import java.util.Scanner;

public class MizDooniApplication {

    public static void main(String[] args) {
     
        MizDooniController controller = MizDooniController.getInstance();
        
        Request request;
        Scanner in = new Scanner(System.in);
        String command = "";
        while (in.hasNext()) {
            command = in.nextLine();
            if (command == null || command.equals("exit"))
                break;
            try {
                request = Parser.parseCommand(command);
                System.out.println(controller.handleRequest(request));
        } catch (Exception e) {
                System.out.println("Error Happened! message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
