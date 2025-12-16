package tribollojfx.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.io.Serializable;

public class Task implements Serializable {
    private UUID id;
    private UUID parentId;
    private String titre;
    private String description;
    private Statut statut;
    private Priorite priorite;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private ArrayList<Task> sousTaches = new ArrayList<Task>();

    public Task(String titre, String description, Priorite priorite, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.id = UUID.randomUUID();
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = Statut.A_FAIRE;
        this.parentId = null;
    }

    public Task(String titre) {
        this.id = UUID.randomUUID();
        this.titre = titre;
    }

    public void addSousTask(Task task) {
        task.parentId = this.id;
        this.sousTaches.add(task);
    }

    public UUID getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public Statut getStatut() { return statut; }
    public Priorite getPriorite() { return priorite; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public ArrayList<Task> getSousTaches() { return sousTaches; }
    public UUID getParentId() { return parentId; }


    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setPriorite(Priorite priorite) { this.priorite = priorite; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }

    public void changerStatut(Statut statut) { this.statut = statut; }

    public boolean estArchivee() {
        return this.statut == Statut.ARCHIVEE;
    }

    @Override
    public String toString() {
        return titre + " (" + priorite + ") [" + statut + "]";
    }
}