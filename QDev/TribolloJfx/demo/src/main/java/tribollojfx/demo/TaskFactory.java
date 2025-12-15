package tribollojfx.demo;

import java.time.LocalDateTime;

public class TaskFactory {
    public static Task creerTask(String titre) {
        LocalDateTime now = LocalDateTime.now();
        return new Task(titre, "", null, now, now.plusDays(1));
    }

    public static Task creerTask(String titre, Priorite priorite, LocalDateTime debut, LocalDateTime fin) {
        return new Task(titre, "", priorite, debut, fin);
    }
}