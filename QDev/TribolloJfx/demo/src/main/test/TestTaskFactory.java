import org.junit.jupiter.api.Test;
import tribollojfx.demo.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TestTaskFactory {

    @Test
    void testCreerTaskAvecDatesAutomatiques() {
        Task t = TaskFactory.creerTask("Auto");
        t.setPriorite(Priorite.NORMALE);

        assertEquals("Auto", t.getTitre());
        assertEquals(Priorite.NORMALE, t.getPriorite());
        assertNotNull(t.getDateDebut());
        assertNotNull(t.getDateFin());
        assertEquals(Statut.A_FAIRE, t.getStatut());

        Duration diff = Duration.between(t.getDateDebut(), t.getDateFin());
        assertEquals(1, diff.toDays());
    }

    @Test
    void testCreerTaskAvecDatesSpecifiees() {
        LocalDateTime debut = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2024, 1, 2, 10, 0);

        Task t = TaskFactory.creerTask("Manuelle", Priorite.HAUTE, debut, fin);

        assertEquals("Manuelle", t.getTitre());
        assertEquals(Priorite.HAUTE, t.getPriorite());
        assertEquals(debut, t.getDateDebut());
        assertEquals(fin, t.getDateFin());
    }
}
