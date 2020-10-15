package seedu.duke.command.sprint;

import seedu.duke.project.Project;
import seedu.duke.sprint.Sprint;
import seedu.duke.sprint.SprintList;
import seedu.duke.parser.DateTimeParser;
import seedu.duke.ui.Ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Hashtable;

public class ViewSprintCommand extends SprintCommand {
    private SprintList allSprint;
    private final ArrayList<Project> projectList;
    private Project proj;

    public ViewSprintCommand(Hashtable<String, String> parameters, ArrayList<Project> projectList) {
        super(parameters);
        this.projectList = projectList;
    }

    /**
     * Abstract method that execute the command.
     */
    public void execute() {
        if (projectList.isEmpty()) {
            Ui.showError("Please create a project first.");
            return;
        }
        proj = projectList.get(0);
        allSprint = proj.getAllSprints();
        if (allSprint.updateCurrentSprint()) {
            int currentSprintNo = allSprint.getCurrentSprintIndex();
            Sprint currentSprint = allSprint.getSprint(currentSprintNo);
            Ui.showToUser(currentSprint.toString());
        } else {
            checkReason();
        }

    }

    private void checkReason() {
        if (allSprint.size() == 0) {
            Ui.showToUserLn("You have yet to create your sprint.");
            return;
        }

        Sprint latestSprint = allSprint.getSprint(allSprint.size() - 1);
        if (DateTimeParser.diff(LocalDate.now(), proj.getEndDate()) == 0) {
            Ui.showToUserLn("Project already ended on " + proj.getEndDate());
            return;
        } else if (DateTimeParser.diff(LocalDate.now(), proj.getStartDate()) > 0) {
            Ui.showToUserLn("Project will start on " + proj.getStartDate());
            return;
        }

        if (DateTimeParser.diff(latestSprint.getEndDate(), LocalDate.now()) >= 0) {
            Ui.showToUserLn("Latest sprint ended on " + latestSprint.getEndDate());
            Ui.showToUserLn("Please create new sprint.");
            return;
        }

        Sprint current = allSprint.getSprint(0);
        if (DateTimeParser.diff(LocalDate.now(), current.getStartDate()) < 0) {
            Ui.showToUserLn("First sprint will start on " + current.getStartDate());
        }
    }
}
