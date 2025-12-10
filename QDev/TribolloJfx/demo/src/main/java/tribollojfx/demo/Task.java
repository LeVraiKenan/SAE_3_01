package tribollojfx.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task {
    private UUID id;
    private String titre;
    private String description;
    private Statut statut;
    private Priorite priorite;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private List<Task> sousTaches;

    public Task(String titre, String description, Priorite priorite, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.id = UUID.randomUUID();
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = Statut.A_FAIRE;
        this.sousTaches = new ArrayList<Task>();
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }

    public void changerStatut(Statut statut) {
        this.statut = statut;
    }

    public void ajouterSousTache(Task task) {
        this.sousTaches.add(task);
    }

    public List<Task> getSousTaches() {
        return this.sousTaches;
    }

}
