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

    public List<Task> getTachesByStatut(Statut statut) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            // On filtre : le bon statut ET pas de parent
            if (task.getStatut() == statut && task.getParentId() == null) {
                result.add(task);
            }
        }
        return result;
    }

    public Task getTaskById(UUID id) {
        return tasks.get(id);
    }

    private void saveTasks() {
        repository.saveAll(new ArrayList<>(tasks.values()));
    }

    public void ajouterTask(Task t) {
        tasks.put(t.getId(), t);
        notifier();
        saveTasks();
    }

    public void updateTaskStatut(UUID taskId, Statut newStatut) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.changerStatut(newStatut);

            // Si la tâche passe en cours ou terminée, propager aux sous-tâches
            if (newStatut == Statut.EN_COURS || newStatut == Statut.TERMINEE) {
                for (Task sousTask : task.getSousTaches()) {
                    if (sousTask.getStatut() != Statut.TERMINEE || newStatut == Statut.TERMINEE) {
                        sousTask.changerStatut(newStatut);
                    }
                }
            }
            notifier();
            saveTasks();
        }
    }

    public void supprimerTask(Task t) {
        for (Task sousTache : t.getSousTaches()) {
            tasks.remove(sousTache.getId());
        }
        tasks.remove(t.getId());
        notifier();
        saveTasks();
    }

    public void ajouterSousTask(UUID parentId, Task sousTask) {
        // Récupère le parent
        Task parent = tasks.get(parentId);

        if (parent != null) {
            parent.addSousTask(sousTask);
            tasks.put(sousTask.getId(), sousTask);

            // Hériter du statut du parent si nécessaire
            if (parent.getStatut() == Statut.EN_COURS || parent.getStatut() == Statut.TERMINEE) {
                sousTask.changerStatut(parent.getStatut());
            }
            notifier();
            saveTasks();
        }
    }

    private void updateParentStatus(Task parent) {
        if (parent.getSousTaches().isEmpty()) {
            return;
        }

        boolean toutesTerminees = true;
        boolean auMoinsUneEnCours = false;

        for (Task sousT : parent.getSousTaches()) {
            if (sousT.getStatut() != Statut.TERMINEE) {
                toutesTerminees = false;
            }
            if (sousT.getStatut() == Statut.EN_COURS) {
                auMoinsUneEnCours = true;
            }
        }

        if (toutesTerminees) {
            parent.changerStatut(Statut.TERMINEE);
        } else if (auMoinsUneEnCours) {
            parent.changerStatut(Statut.EN_COURS);
        } else {
            parent.changerStatut(Statut.A_FAIRE);
        }
    }

    public void updateSousTaskStatut(UUID sousTaskId, Statut newStatut) {
        Task sousTask = tasks.get(sousTaskId);
        if (sousTask != null) {
            sousTask.changerStatut(newStatut);

            // Mettre à jour le statut du parent
            UUID parentId = sousTask.getParentId();
            if (parentId != null) {
                Task parent = tasks.get(parentId);
                if (parent != null) {
                    updateParentStatus(parent);
                }
            }
            notifier();
            saveTasks();
        }
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