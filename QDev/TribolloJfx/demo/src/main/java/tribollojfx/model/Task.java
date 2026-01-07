package tribollojfx.model;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.ArrayList;

public class Task implements Serializable {
    private String titre;
    private String description;
    private Statut statut;
    private Priorite priorite;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private ArrayList<Task> dependances = new ArrayList<>();
    private int colonnePersoId = 0;

    public Task(String titre, String description, Priorite priorite,
                LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = Statut.A_FAIRE;
    }

    public Task(String titre) {
        this.titre = titre;
        this.statut = Statut.A_FAIRE;
    }

    public void addDependance(Task task) {
        this.dependances.add(task);
    }

    public void removeDependance(Task task) {
        this.dependances.remove(task);
    }

    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public Statut getStatut() { return statut; }
    public Priorite getPriorite() { return priorite; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public ArrayList<Task> getDependances() { return dependances; }

    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setPriorite(Priorite priorite) { this.priorite = priorite; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public void changerStatut(Statut statut) { this.statut = statut; }

    public boolean estArchivee() {
        return this.statut == Statut.ARCHIVEE;
    }

    public int getColonnePersoId() { return colonnePersoId; }
    public void setColonnePersoId(int id) { this.colonnePersoId = id; }

    @Override
    public String toString() {
        return titre + " (" + priorite + ") [" + statut + "]";
    }
}