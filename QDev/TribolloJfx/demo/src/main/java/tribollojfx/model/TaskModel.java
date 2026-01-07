package tribollojfx.model;

import tribollojfx.observer.TaskObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TaskModel {
    private static final int MAX_COLONNES_PERSO = 2;
    private TreeMap<Integer,String> colonnesPersonnalisees;
    private ArrayList<Task> tasks;
    private List<TaskObserver> observers;
    private SerializedTaskRepository repository;
    private NotificationManager notificationManager;

    public TaskModel() {
        this.repository = new SerializedTaskRepository();
        this.tasks = new ArrayList<>();
        this.colonnesPersonnalisees = new TreeMap<>();
        this.observers = new ArrayList<>();
        this.notificationManager = new NotificationManager(this);

        List<Task> loadedTasks = repository.loadAll();
        if (loadedTasks != null) {
            tasks.addAll(loadedTasks);
        }

        notificationManager.verifierNotifications();
    }

    public List<Task> getTaches() {
        return new ArrayList<>(tasks);
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

    public TreeMap<Integer, String> getColonnesPersonnalisees() {
        return new TreeMap<>(colonnesPersonnalisees);
    }

    private void saveTasks() {
        repository.saveAll(tasks);
    }

    public void ajouterTask(Task t) {
        tasks.add(t);
        saveTasks();
        notificationManager.verifierNotifications();
        notifyObservers();
    }

    public void updateTaskStatut(Task task, Statut newStatut) {
        if (task != null) {
            if (newStatut == Statut.TERMINEE) {
                boolean toutesDependancesTerminees = true;
                for (Task dependance : task.getDependances()) {
                    if (dependance.getStatut() != Statut.TERMINEE) {
                        toutesDependancesTerminees = false;
                        break;
                    }
                }

                if (!toutesDependancesTerminees) {
                    return;
                }
            }

            Statut ancienStatut = task.getStatut();
            task.changerStatut(newStatut);

            if (newStatut == Statut.ARCHIVEE) {
                notificationManager.supprimerNotificationsPourTask(task);
            }

            saveTasks();
            notificationManager.verifierNotifications();
            notifyObservers();
        }
    }

    public void supprimerTask(Task t) {
        for (Task autreTask : tasks) {
            autreTask.getDependances().remove(t);
        }

        notificationManager.supprimerNotificationsPourTask(t);
        tasks.remove(t);
        saveTasks();
        notificationManager.verifierNotifications();
        notifyObservers();
    }

    public void ajouterDependance(Task parent, Task dependance) {
        if (parent != null && dependance != null && !parent.getDependances().contains(dependance)) {
            parent.addDependance(dependance);

            if (parent.getStatut() == Statut.EN_COURS || parent.getStatut() == Statut.TERMINEE) {
                dependance.changerStatut(parent.getStatut());
            }

            saveTasks();
            notificationManager.verifierNotifications();
            notifyObservers();
        }
    }

    public void retirerDependance(Task parent, Task dependance) {
        if (parent != null && dependance != null) {
            parent.removeDependance(dependance);
            saveTasks();
            notificationManager.verifierNotifications();
            notifyObservers();
        }
    }

    public void updateDependanceStatut(Task dependance, Statut newStatut) {
        if (dependance != null) {
            dependance.changerStatut(newStatut);

            for (Task parent : tasks) {
                if (parent.getDependances().contains(dependance)) {
                    updateParentStatus(parent);
                    break;
                }
            }

            saveTasks();
            notificationManager.verifierNotifications();
            notifyObservers();
        }
    }

    private void updateParentStatus(Task parent) {
        if (parent.getDependances().isEmpty()) return;

        boolean toutesTerminees = true;
        boolean auMoinsUneEnCours = false;

        for (Task dependance : parent.getDependances()) {
            if (dependance.getStatut() != Statut.TERMINEE) {
                toutesTerminees = false;
            }
            if (dependance.getStatut() == Statut.EN_COURS) {
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

    public void ajouterColonnePersonnalisee(String nomColonne) {
        if (colonnesPersonnalisees.size() >= MAX_COLONNES_PERSO) {
            System.out.println("Impossible : limite de " + MAX_COLONNES_PERSO + " colonnes atteinte.");
            return;
        }

        int nouvelId = 1;
        if (!colonnesPersonnalisees.isEmpty()) {
            nouvelId = colonnesPersonnalisees.lastKey() + 1;
        }

        colonnesPersonnalisees.put(nouvelId, nomColonne);
        notificationManager.verifierNotifications();
        notifyObservers();
    }

    public void addObserver(TaskObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            observer.onTasksChanged(new ArrayList<>(tasks));
        }
    }

    public void removeObserver(TaskObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (TaskObserver observer : observers) {
            observer.onTasksChanged(new ArrayList<>(tasks));
        }
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
}