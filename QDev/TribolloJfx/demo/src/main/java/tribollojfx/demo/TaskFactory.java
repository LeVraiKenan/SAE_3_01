package tribollojfx.demo;

import java.time.LocalDateTime;

public class TaskFactory {
    public static Task creerTask(String titre, Priorite priorite) {
        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = debut.plusDays(1);
        return new Task(titre, "", priorite, debut, fin);
    }
}

