package seedu.duke.command.sprint;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.duke.model.member.Member;
import seedu.duke.model.project.Project;
import seedu.duke.model.project.ProjectManager;
import seedu.duke.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewSprintCommandTest {
    private final PrintStream systemOut = System.out;
    private ByteArrayOutputStream testOut;

    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        Ui.setOutStream(new PrintStream(testOut));
    }

    @AfterEach
    public void restoreSystemOutput() throws IOException {
        testOut.close();
        Ui.setOutStream(systemOut);
    }

    private String getOutput() {
        return testOut.toString();
    }

    private ProjectManager generateProject() {
        ProjectManager projectManager = new ProjectManager();
        projectManager.addProject("project1", "project1", 50, 10);
        projectManager.addProject("project2", "project2", 50, 10);
        assert projectManager.getProjectList().size() == 2 : "Dummy projects not added!";
        return projectManager;
    }

    private void generateDummyTask(ProjectManager projectManager) {
        for (Project project : projectManager.getProjectList().values()) {
            project.getBacklog().addTask(project.getTitle() +"task1", "task1", "HIGH");
            project.getBacklog().addTask(project.getTitle() +"task2", "task2", "MEDIUM");
            project.getBacklog().addTask(project.getTitle() +"task3", "task3", "LOW");
            assert project.getBacklog().size() == 3 : "Dummy tasks for " + project.getTitle() + " not added!";
        }
    }

    private void generateDummyMember(ProjectManager projectManager) {
        for (Project project : projectManager.getProjectList().values()) {
            project.getMemberList().addMember(new Member(project.getTitle() + "member1"));
            project.getMemberList().addMember(new Member(project.getTitle() + "member2"));
            project.getMemberList().addMember(new Member(project.getTitle() + "member3"));
            assert project.getMemberList().size() == 3 : "Dummy members for " + project.getTitle() + " not added!";
        }
    }

    private void generateDummySprint(ProjectManager projectManager) {
        for (Project project : projectManager.getProjectList().values()) {
            project.getSprintList().addSprint(project, project.getTitle() + "Sprint1", LocalDate.now(), LocalDate.now().plusDays(9));
            project.getSprintList().addSprint(project, project.getTitle() + "Sprint2", LocalDate.now().plusDays(10), LocalDate.now().plusDays(19));
            project.getSprintList().addSprint(project, project.getTitle() + "Sprint3", LocalDate.now().plusDays(20), LocalDate.now().plusDays(49));
            project.getSprintList().addSprint(project, project.getTitle() + "Sprint4", LocalDate.now().plusDays(30), LocalDate.now().plusDays(49));
            project.getSprintList().addSprint(project, project.getTitle() + "Sprint5", LocalDate.now().plusDays(40), LocalDate.now().plusDays(49));
            assert project.getSprintList().size() == 5 : "Dummy sprints for " + project.getTitle() + " not added!";
        }
    }

    @Test
    void viewSprintTaskCommand_validCommand() {
        ProjectManager projectManager = generateProject();
        generateDummySprint(projectManager);

        Hashtable<String, String> parameters = new Hashtable<>();
        ViewSprintCommand command = new ViewSprintCommand(parameters, projectManager);

        command.execute();

        String expected = "[Project ID: " + projectManager.getSelectedProjectIndex() + "]" + System.lineSeparator() +
                "========================= CURRENT SPRINT ========================" + System.lineSeparator() +
                "[ID: 1]" + System.lineSeparator() +
                "[Goal: project2Sprint1]" + System.lineSeparator() +
                "[Period: " + LocalDate.now() + " - " + LocalDate.now().plusDays(9) + "]" + System.lineSeparator() +
                "[Remaining: 9 days]" + System.lineSeparator() +
                "[No allocated tasks]" + System.lineSeparator() +
                "================================================================="
                + System.lineSeparator();
        assertEquals(expected, getOutput());
    }

    @Test
    void viewSprintTaskCommand_validCommand_specifySprint() {
        ProjectManager projectManager = generateProject();
        generateDummySprint(projectManager);

        Hashtable<String, String> parameters = new Hashtable<>();
        parameters.put("0", "2");
        ViewSprintCommand command = new ViewSprintCommand(parameters, projectManager);

        command.execute();

        String expected = "[Project ID: " + projectManager.getSelectedProjectIndex() + "]" + System.lineSeparator() +
                "============================ SPRINT =============================" + System.lineSeparator() +
                "[ID: 2]" + System.lineSeparator() +
                "[Goal: project2Sprint2]" + System.lineSeparator() +
                "[Period: " + LocalDate.now().plusDays(10) + " - " + LocalDate.now().plusDays(19) + "]" + System.lineSeparator() +
                "[No allocated tasks]" + System.lineSeparator() +
                "================================================================="
                + System.lineSeparator();
        assertEquals(expected, getOutput());
    }

    @Test
    void viewSprintTaskCommand_validCommand_specifyProject() {
        ProjectManager projectManager = generateProject();
        generateDummySprint(projectManager);

        Hashtable<String, String> parameters = new Hashtable<>();
        parameters.put("project", "1");
        parameters.put("sprint", "2");
        ViewSprintCommand command = new ViewSprintCommand(parameters, projectManager);

        command.execute();

        String expected = "[Project ID: 1]" + System.lineSeparator() +
                "============================ SPRINT =============================" + System.lineSeparator() +
                "[ID: 2]" + System.lineSeparator() +
                "[Goal: project1Sprint2]" + System.lineSeparator() +
                "[Period: " + LocalDate.now().plusDays(10) + " - " + LocalDate.now().plusDays(19) + "]" + System.lineSeparator() +
                "[No allocated tasks]" + System.lineSeparator() +
                "================================================================="
                + System.lineSeparator();
        assertEquals(expected, getOutput());
    }

    @Test
    void viewSprintTaskCommand_invalidCommand_lowerBoundSprint() {
        ProjectManager projectManager = generateProject();
        generateDummySprint(projectManager);

        Hashtable<String, String> parameters = new Hashtable<>();
        parameters.put("0", "0");
        ViewSprintCommand command = new ViewSprintCommand(parameters, projectManager);

        command.execute();

        String expected = "Sprint not found: 0";
        assertEquals(expected, getOutput());
    }

    @Test
    void viewSprintTaskCommand_invalidCommand_upperBoundSprint() {
        ProjectManager projectManager = generateProject();
        generateDummySprint(projectManager);

        Hashtable<String, String> parameters = new Hashtable<>();
        parameters.put("0", "6");
        ViewSprintCommand command = new ViewSprintCommand(parameters, projectManager);

        command.execute();

        String expected = "Sprint not found: 6";
        assertEquals(expected, getOutput());
    }
}
