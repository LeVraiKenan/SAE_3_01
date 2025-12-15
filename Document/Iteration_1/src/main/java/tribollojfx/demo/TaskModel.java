package tribollojfx.demo;

import java.util.ArrayList;
import java.util.List;

public class TaskModel {
    private List<Task> tasks;
    private List<TaskModelObservateur> observers;
    private TxtTaskRepository repository;

    public TaskModel() {
        this.repository = new TxtTaskRepository();
        this.tasks = repository.loadAll();
        this.observers = new ArrayList<>();
    }

    public List<Task> getTaches() {
        return this.tasks;
    }

    public void ajouterTask(Task t) {
        this.tasks.add(t);
        notifier();
        repository.saveAll(this.tasks);
    }

    public void supprimerTask(Task t) {
        this.tasks.remove(t);
        notifier();
        repository.saveAll(this.tasks);
    }

    public void notifier() {
        for (TaskModelObservateur obs : observers) {
            obs.notifier(this.tasks);
        }
    }

    public void addObserver(TaskModelObservateur obs) {
        observers.add(obs);
    }
}