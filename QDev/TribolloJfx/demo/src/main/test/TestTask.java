import org.junit.jupiter.api.Test;
import tribollojfx.demo.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = debut.plusDays(1);
        Task t = new Task("T1", "D1", Priorite.BASSE, debut, fin);

        t.setTitre("Nouveau titre");
        t.setDescription("Nouvelle desc");
        t.setPriorite(Priorite.URGENTE);

        assertEquals("Nouveau titre", t.getTitre());
        assertEquals("Nouvelle desc", t.getDescription());
        assertEquals(Priorite.URGENTE, t.getPriorite());
    }

    @Test
    void testChangerStatutEtEstArchivee() {
        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = debut.plusDays(1);
        Task t = new Task("Test", "Desc", Priorite.NORMALE, debut, fin);

        assertEquals(Statut.A_FAIRE, t.getStatut());
        assertFalse(t.estArchivee());

        t.changerStatut(Statut.ARCHIVEE);

        assertEquals(Statut.ARCHIVEE, t.getStatut());
        assertTrue(t.estArchivee());
    }

    @Test
    void testToStringNonVide() {
        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = debut.plusDays(1);
        Task t = new Task("Test", "Desc", Priorite.NORMALE, debut, fin);

        String s = t.toString();
        assertNotNull(s);
        assertTrue(s.contains("Test"));
    }

    @Test
    void testSousTaches() {
        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = debut.plusDays(1);
        Task parent = new Task("Parent", "Desc", Priorite.NORMALE, debut, fin);
        Task child = new Task("Enfant");

        assertTrue(parent.getSousTaches().isEmpty());

        parent.addSousTask(child);

        assertEquals(1, parent.getSousTaches().size());
        assertSame(child, parent.getSousTaches().get(0));
    }
}
