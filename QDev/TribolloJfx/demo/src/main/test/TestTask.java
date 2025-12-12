import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import tribollojfx.demo.*;

class TestTask {

    @Test
    void testConstructeurEtGetters() {
        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = debut.plusDays(2);
        Task t = new Task("Titre", "Desc", Priorite.HAUTE, debut, fin);

        assertNotNull(t.getId());
        assertEquals("Titre", t.getTitre());
        assertEquals("Desc", t.getDescription());
        assertEquals(Priorite.HAUTE, t.getPriorite());
        assertEquals(debut, t.getDateDebut());
        assertEquals(fin, t.getDateFin());
        assertEquals(Statut.A_FAIRE, t.getStatut());
    }

    @Test
    void testSetters() {
        Task t = new Task("T1", "D1", Priorite.BASSE, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        t.setTitre("Nouveau titre");
        t.setDescription("Nouvelle desc");
        t.setPriorite(Priorite.URGENTE);

        assertEquals("Nouveau titre", t.getTitre());
        assertEquals("Nouvelle desc", t.getDescription());
        assertEquals(Priorite.URGENTE, t.getPriorite());
    }

    @Test
    void testChangerStatutEtEstArchivee() {
        Task t = TaskFactory.creerTask("Test", Priorite.NORMALE);

        assertEquals(Statut.A_FAIRE, t.getStatut());
        assertFalse(t.estArchivee());

        t.changerStatut(Statut.ARCHIVEE);

        assertEquals(Statut.ARCHIVEE, t.getStatut());
        assertTrue(t.estArchivee());
    }

    @Test
    void testToStringNonVide() {
        Task t = TaskFactory.creerTask("Test", Priorite.NORMALE);
        String s = t.toString();
        assertNotNull(s);
        assertTrue(s.contains("Test"));
    }
}
