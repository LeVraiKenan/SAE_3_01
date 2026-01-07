package tribollojfx.demo;

public class ColonneControleur {
    private TaskModel model;

    public ColonneControleur(TaskModel model) {
        this.model = model;
    }

    public void creerColonne(String nomColonne) {
        if (nomColonne != null && !nomColonne.isEmpty()) {
            model.ajouterColonnePersonnalisee(nomColonne);
        }
    }
}
