package tribollojfx.model;

import java.time.LocalDateTime;
import java.io.Serializable;

public class Notification implements Serializable {
    public enum Type {
        DEBUT_TACHE("Début de tâche"),
        FIN_APPROCHE("Fin approche"),
        RETARD_DEBUT("Retard de début"),
        RETARD_FIN("Retard de fin"),
        DEPENDANCE_BLOQUANTE("Dépendance bloquante");

        private final String libelle;

        Type(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    private String message;
    private Type type;
    private LocalDateTime dateCreation;
    private boolean lue;
    private Task taskConcernee;

    public Notification(String message, Type type, Task taskConcernee) {
        this.message = message;
        this.type = type;
        this.taskConcernee = taskConcernee;
        this.dateCreation = LocalDateTime.now();
        this.lue = false;
    }

    public String getMessage() { return message; }
    public Type getType() { return type; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public boolean isLue() { return lue; }
    public Task getTaskConcernee() { return taskConcernee; }

    public void marquerCommeLue() { this.lue = true; }

    @Override
    public String toString() {
        return "[" + type.getLibelle() + "] " + message;
    }
}