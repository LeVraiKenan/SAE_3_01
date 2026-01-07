package tribollojfx.view.components;

import tribollojfx.model.*;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public class GanttView {
    private static final double PX_PER_DAY = 12.0;
    private static final double TASK_TITLE_WIDTH = 150.0;
    private static final double TASK_BAR_HEIGHT = 20.0;
    private static final double VERTICAL_GAP = 10.0;

    private VBox view;
    private VBox barsContainer;
    private Pane stack;

    private LocalDate dateMin;
    private LocalDate dateMax;

    public GanttView(List<Task> tasks) {
        createView();
        update(tasks);
    }

    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(20));

        barsContainer = new VBox(VERTICAL_GAP);
        barsContainer.setPadding(new Insets(10));

        stack = new Pane();
        stack.getChildren().add(barsContainer);

        view.getChildren().add(stack);
    }

    public void update(List<Task> tasks) {
        barsContainer.getChildren().clear();
        stack.getChildren().removeIf(n -> n instanceof Line);

        if (tasks == null || tasks.isEmpty()) return;

        List<Task> tasksWithDates = tasks.stream()
                .filter(t -> t.getDateDebut() != null && t.getDateFin() != null)
                .sorted(Comparator.comparing(Task::getDateDebut))
                .toList();

        if (tasksWithDates.isEmpty()) return;

        calculateDateRange(tasksWithDates);

        HBox timeline = buildTimeline();
        view.getChildren().set(0, timeline);

        for (Task t : tasksWithDates) {
            HBox ligne = buildTaskLine(t);
            barsContainer.getChildren().add(ligne);
        }

        addTodayLine();

        view.getChildren().add(1, stack);
    }

    private void calculateDateRange(List<Task> tasks) {
        dateMin = toLocalDate(tasks.get(0).getDateDebut());
        dateMax = toLocalDate(tasks.get(0).getDateFin());

        for (Task t : tasks) {
            LocalDate dDebut = toLocalDate(t.getDateDebut());
            LocalDate dFin = toLocalDate(t.getDateFin());

            if (dDebut.isBefore(dateMin)) dateMin = dDebut;
            if (dFin.isAfter(dateMax)) dateMax = dFin;
        }
    }

    private HBox buildTimeline() {
        HBox timeline = new HBox();
        timeline.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        timeline.setSpacing(PX_PER_DAY);
        timeline.setPadding(new Insets(0, 0, 0, TASK_TITLE_WIDTH));

        LocalDate d = dateMin;
        while (!d.isAfter(dateMax)) {
            Label lbl = new Label(d.toString());
            lbl.setRotate(-45);
            lbl.setStyle("-fx-font-size: 10px;");
            timeline.getChildren().add(lbl);
            d = d.plusDays(1);
        }

        return timeline;
    }

    private HBox buildTaskLine(Task t) {
        LocalDate dateDebut = toLocalDate(t.getDateDebut());
        LocalDate dateFin = toLocalDate(t.getDateFin());

        long offsetDays = daysBetween(dateMin, dateDebut);
        long dureeDays = daysBetweenInclusive(dateDebut, dateFin);

        HBox ligne = new HBox(10);
        ligne.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label titre = new Label(t.getTitre());
        titre.setPrefWidth(TASK_TITLE_WIDTH);

        Region espace = new Region();
        espace.setPrefWidth(offsetDays * PX_PER_DAY);

        Rectangle barre = new Rectangle(dureeDays * PX_PER_DAY, TASK_BAR_HEIGHT);
        barre.setArcHeight(5);
        barre.setArcWidth(5);
        barre.setFill(colorForStatut(t.getStatut()));

        ligne.getChildren().addAll(titre, espace, barre);

        return ligne;
    }

    private void addTodayLine() {
        LocalDate today = LocalDate.now();

        if (today.isBefore(dateMin)) today = dateMin;
        if (today.isAfter(dateMax)) today = dateMax;

        long offsetNow = daysBetween(dateMin, today);
        double x = offsetNow * PX_PER_DAY + TASK_TITLE_WIDTH;

        Line redLine = new Line(x, 0, x, barsContainer.getHeight() + 20);
        redLine.setStroke(Color.RED);
        redLine.setStrokeWidth(2);

        stack.getChildren().add(redLine);
    }

    private LocalDate toLocalDate(LocalDateTime ldt) {
        return ldt.toLocalDate();
    }

    private long daysBetween(LocalDate start, LocalDate end) {
        return Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
    }

    private long daysBetweenInclusive(LocalDate start, LocalDate end) {
        return Duration.between(start.atStartOfDay(), end.plusDays(1).atStartOfDay()).toDays();
    }

    private Color colorForStatut(Statut statut) {
        if (statut == null) return Color.DODGERBLUE;

        return switch (statut) {
            case A_FAIRE -> Color.LIGHTGRAY;
            case EN_COURS -> Color.DODGERBLUE;
            case TERMINEE -> Color.LIGHTGREEN;
            case ARCHIVEE -> Color.DARKGRAY;
            case BLOQUEE -> Color.ORANGE;
            default -> Color.DODGERBLUE;
        };
    }

    public VBox getView() {
        return view;
    }
}