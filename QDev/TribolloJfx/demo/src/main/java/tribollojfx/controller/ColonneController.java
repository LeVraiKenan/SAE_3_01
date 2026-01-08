package tribollojfx.controller;

import tribollojfx.model.TaskModel;

public class ColonneController {
    private TaskModel model;

    public ColonneController(TaskModel model) {
        this.model = model;
    }

    public void creerColonne(String nomColonne) {
        if (nomColonne != null && !nomColonne.isEmpty()) {
            model.ajouterColonnePersonnalisee(nomColonne);
        }
    }

    public int getNombreColonnesPerso() {
        return model.getColonnesPersonnalisees().size();
    }

    public boolean peutAjouterColonne() {
        return getNombreColonnesPerso() < 2;
    }
}