package tribollojfx.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

public class TaskModel {
    private Map<UUID, Task> tasks;
    private List<TaskModelObservateur> observers;
    private SerializedTaskRepository repository;

    public TaskModel() {
        this.repository = new SerializedTaskRepository();
        this.tasks = new HashMap<>();
        loadTasks();
        this.observers = new ArrayList<>();
    }

    private void loadTasks() {
        List<Task> loadedTasks = repository.loadAll();
        for (Task task : loadedTasks) {
            tasks.put(task.getId(), task);
        }
    }

    public List<Task> getTaches() {
        return new ArrayList<>(tasks.values());
    }

    private void saveTasks() {
        repository.saveAll(new ArrayList<>(tasks.values()));
    }

    public void ajouterTask(Task t) {
        tasks.put(t.getId(), t);
        notifier();
        saveTasks();
    }

    public void supprimerTask(Task t) {
        for (Task sousTache : t.getSousTaches()) {
            tasks.remove(sousTache.getId());
        }
        tasks.remove(t.getId());
        notifier();
        saveTasks();
    }

    public void notifier() {
        List<Task> allTasks = getTaches();
        for (TaskModelObservateur obs : observers) {
            obs.notifier(allTasks);
        }
    }

    public void addObserver(TaskModelObservateur obs) {
        observers.add(obs);
    }
}