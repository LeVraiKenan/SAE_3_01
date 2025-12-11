package tribollojfx.demo;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class TxtTaskRepository {
    private static final String FILE_NAME = "tasks.txt";

    public void saveAll(List<Task> tasks) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Task t : tasks) {
                pw.println(t.getId() + ";" + t.getTitre() + ";" + t.getDescription() + ";" + t.getStatut() + ";" + t.getPriorite() + ";" + t.getDateDebut() + ";" + t.getDateFin());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Task> loadAll() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return tasks;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 7) {
                    Task t = new Task(parts[1], parts[2], Priorite.valueOf(parts[4]), LocalDateTime.parse(parts[5]), LocalDateTime.parse(parts[6]));
                    t.changerStatut(Statut.valueOf(parts[3]));
                    tasks.add(t);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}