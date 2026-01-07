package tribollojfx.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationManager {
    private TaskModel taskModel;
    private List<Notification> notifications;

    public NotificationManager(TaskModel taskModel) {
        this.taskModel = taskModel;
        this.notifications = new ArrayList<>();
    }

    public void verifierNotifications() {
        List<Notification> nouvellesNotifications = new ArrayList<>();
        LocalDate aujourdHui = LocalDate.now();

        for (Task task : taskModel.getTaches()) {
            if (task.getStatut() == Statut.ARCHIVEE) continue;

            if (task.getDateDebut() != null && task.getDateFin() != null) {
                LocalDate dateDebut = task.getDateDebut().toLocalDate();
                LocalDate dateFin = task.getDateFin().toLocalDate();

                if (dateDebut.equals(aujourdHui) && task.getStatut() == Statut.A_FAIRE) {
                    String message = "La tâche \"" + task.getTitre() + "\" commence aujourd'hui. Pensez à la démarrer !";
                    nouvellesNotifications.add(new Notification(message, Notification.Type.DEBUT_TACHE, task));
                }

                long joursAvantFin = ChronoUnit.DAYS.between(aujourdHui, dateFin);
                if (joursAvantFin == 2 && task.getStatut() != Statut.TERMINEE) {
                    String message = "La tâche \"" + task.getTitre() + "\" se termine dans 2 jours !";
                    nouvellesNotifications.add(new Notification(message, Notification.Type.FIN_APPROCHE, task));
                }

                if (dateDebut.isBefore(aujourdHui) && task.getStatut() == Statut.A_FAIRE) {
                    long joursRetard = ChronoUnit.DAYS.between(dateDebut, aujourdHui);
                    String message = "URGENT : La tâche \"" + task.getTitre() + "\" devait commencer il y a " +
                            joursRetard + " jour(s). Démarrez-la !";
                    nouvellesNotifications.add(new Notification(message, Notification.Type.RETARD_DEBUT, task));
                }

                if (dateFin.isBefore(aujourdHui) && task.getStatut() != Statut.TERMINEE) {
                    long joursRetard = ChronoUnit.DAYS.between(dateFin, aujourdHui);
                    String message = "URGENT : La tâche \"" + task.getTitre() + "\" est en retard de " +
                            joursRetard + " jour(s). Terminez-la !";
                    nouvellesNotifications.add(new Notification(message, Notification.Type.RETARD_FIN, task));
                }
            }

            if (!task.getDependances().isEmpty() && task.getStatut() != Statut.TERMINEE) {
                List<Task> dependancesNonTerminees = task.getDependances().stream()
                        .filter(d -> d.getStatut() != Statut.TERMINEE)
                        .collect(Collectors.toList());

                if (!dependancesNonTerminees.isEmpty()) {
                    String message = "La tâche \"" + task.getTitre() + "\" a " +
                            dependancesNonTerminees.size() + " dépendance(s) non terminée(s)";
                    nouvellesNotifications.add(new Notification(message, Notification.Type.DEPENDANCE_BLOQUANTE, task));
                }
            }
        }

        for (Notification nouvelle : nouvellesNotifications) {
            if (!notificationExisteDeja(nouvelle)) {
                notifications.add(nouvelle);
            }
        }

        notifications.removeIf(notif ->
                notif.getTaskConcernee().getStatut() == Statut.ARCHIVEE
        );
    }

    private boolean notificationExisteDeja(Notification nouvelle) {
        return notifications.stream()
                .anyMatch(existante ->
                        existante.getMessage().equals(nouvelle.getMessage()) &&
                                existante.getTaskConcernee() == nouvelle.getTaskConcernee() &&
                                existante.getDateCreation().toLocalDate().equals(LocalDate.now())
                );
    }

    public List<Notification> getNotificationsNonLues() {
        return notifications.stream()
                .filter(notif -> !notif.isLue())
                .collect(Collectors.toList());
    }

    public List<Notification> getToutesNotifications() {
        return new ArrayList<>(notifications);
    }

    public int getNombreNotificationsNonLues() {
        return (int) notifications.stream()
                .filter(notif -> !notif.isLue())
                .count();
    }

    public void marquerToutesCommeLues() {
        notifications.forEach(Notification::marquerCommeLue);
    }

    public void marquerCommeLue(Notification notification) {
        notification.marquerCommeLue();
    }

    public void supprimerNotification(Notification notification) {
        notifications.remove(notification);
    }

    public void supprimerToutesNotifications() {
        notifications.clear();
    }

    public void supprimerNotificationsPourTask(Task task) {
        notifications.removeIf(notif -> notif.getTaskConcernee() == task);
    }
}