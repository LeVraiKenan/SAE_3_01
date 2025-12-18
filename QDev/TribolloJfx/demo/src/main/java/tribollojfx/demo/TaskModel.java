package tribollojfx.demo;

import java.util.ArrayList;
import java.util.List;

public class TaskModel {
    private ArrayList<Task> tasks;
    private List<TaskModelObservateur> observers;
    private SerializedTaskRepository repository;

    public TaskModel() {
        this.repository = new SerializedTaskRepository();
        this.tasks = new ArrayList<>();
        loadTasks();
        this.observers = new ArrayList<>();
    }

    private void loadTasks() {
        List<Task> loadedTasks = repository.loadAll();
        tasks.addAll(loadedTasks);
    }

    public List<Task> getTaches() {
        return tasks;
    }

    public List<Task> getTachesByStatut(Statut statut) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStatut() == statut) {
                result.add(task);
            }
        }
        return result;
    }

    private void saveTasks() {
        repository.saveAll(tasks);
    }

    public void ajouterTask(Task t) {
        tasks.add(t);
        notifier();
        saveTasks();
    }

    public void updateTaskStatut(Task parent, Statut newStatut) {
        if (parent != null) {
            int CountDependance = 0;
            for (Task depend : parent.getDependance()) {
               if (depend.getStatut() == Statut.TERMINEE) {
                   CountDependance++;
               }
            }

            if(parent.getDependance().size() != 0) {
                if (CountDependance != parent.getDependance().size() || parent.getDependance().size() == 0) {
                    parent.changerStatut(Statut.A_FAIRE);
                } else {
                    parent.changerStatut(newStatut);
                }
            } else {
                parent.changerStatut(newStatut);
            }

            for (Task sousTask : parent.getSousTaches()) {
                if (newStatut == Statut.TERMINEE) {
                    sousTask.changerStatut(Statut.TERMINEE);
                }
            }

            notifier();
            saveTasks();
        }
    }

    public void supprimerTask(Task t) {
        tasks.removeAll(t.getSousTaches());
        tasks.remove(t);
        notifier();
        saveTasks();
    }

    public void ajouterSousTask(Task parent, Task sousTask) {
        if (parent != null) {
            parent.addSousTask(sousTask);

            if (parent.getStatut() == Statut.EN_COURS || parent.getStatut() == Statut.TERMINEE) {
                sousTask.changerStatut(parent.getStatut());
            }

            notifier();
            saveTasks();
        }
    }

    public void ajouterDependance(Task encours, Task depend) {
        if (encours != null) {
            encours.addDependance(depend);

            notifier();
            saveTasks();
        }
    }

    private void updateParentStatus(Task parent) {
        if (parent.getSousTaches().isEmpty()) return;

        boolean toutesTerminees = true;
        int NombreTacheTerminees = 0;
        boolean auMoinsUneEnCours = false;

        for (Task sousT : parent.getSousTaches()) {
            if (sousT.getStatut() != Statut.TERMINEE) {
                toutesTerminees = false;
                NombreTacheTerminees++;
            }
        }

        if (NombreTacheTerminees < parent.getSousTaches().size()) {
            auMoinsUneEnCours = true;
        }

        if (toutesTerminees) {
            parent.changerStatut(Statut.TERMINEE);
        } else if (auMoinsUneEnCours) {
            parent.changerStatut(Statut.EN_COURS);
        } else {
            parent.changerStatut(Statut.A_FAIRE);
        }
    }

    public void updateSousTaskStatut(Task sousTask, Statut newStatut) {
        if (sousTask != null) {
            sousTask.changerStatut(newStatut);

            for (Task parent : tasks) {
                if (parent.getSousTaches().contains(sousTask)) {
                    updateParentStatus(parent);
                    break;
                }
            }

            notifier();
            saveTasks();
        }
    }

    public void notifier() {
        for (TaskModelObservateur obs : observers) {
            obs.notifier(tasks);
        }
    }

    public void addObserver(TaskModelObservateur obs) {
        observers.add(obs);
    }
}