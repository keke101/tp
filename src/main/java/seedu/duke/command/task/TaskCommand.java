package seedu.duke.command.task;

import seedu.duke.model.project.ProjectList;
import seedu.duke.model.sprint.Sprint;
import seedu.duke.ui.Messages;
import seedu.duke.exception.DukeException;
import seedu.duke.model.project.Project;
import seedu.duke.model.task.Task;
import seedu.duke.ui.Ui;

import java.util.ArrayList;
import java.util.Hashtable;

import static seedu.duke.command.CommandSummary.TITLE;
import static seedu.duke.command.CommandSummary.DESCRIPTION;
import static seedu.duke.command.CommandSummary.PRIORITY;
import static seedu.duke.command.CommandSummary.TASK_ID;


public class TaskCommand {
    public void addTaskCommand(Hashtable<String, String> tasks, ProjectList projectListManager)
            throws DukeException {

        String title;
        String description;
        String priority;

        title = tasks.get(TITLE);
        description = tasks.get(DESCRIPTION);
        priority = tasks.get(PRIORITY);

        try {
            Project proj = projectListManager.getProject();
            if (!proj.getProjectBacklog().checkValidPriority(priority)) {
                throw new DukeException("Invalid priority");
            }
            proj.getProjectBacklog().addTask(title, description, priority);
            Task addedTask = proj.getProjectBacklog().getTask(proj.getProjectBacklog().getNextId() - 1);
            Ui.showToUserLn("Task successfully created.");
            Ui.showToUserLn(addedTask.toString());

        } catch (IndexOutOfBoundsException e) {
            Ui.showError("There are no projects! Please create a project first.");
        }

    }

    public void deleteTaskCommand(Hashtable<String,String> taskIdString, ProjectList projectListManager) {
        try {
            Project proj = projectListManager.getProject();
            if (taskIdString.isEmpty()) {
                Ui.showError("Missing parameters.");
            }
            for (int i = 0; i < taskIdString.size(); i++) {
                try {
                    int taskId = Integer.parseInt(taskIdString.get(Integer.toString(i)));
                    if (proj.getProjectBacklog().checkTaskExist(taskId)) {
                        Task task = proj.getProjectBacklog().getTask(taskId);
                        Ui.showToUserLn("The corresponding task "
                                + task.getTitle()
                                + " has been removed from project.");
                        proj.getProjectBacklog().removeTask(i);
                        ArrayList<Sprint> allSprints = proj.getAllSprints().getSprintList();
                        for (Sprint sprint : allSprints) {
                            if (sprint.checkTaskExist(i)) {
                                sprint.removeSprintTask(i);
                            }
                        }
                    } else {
                        Ui.showError(Messages.MESSAGE_INVALID_ID);
                    }
                } catch (NumberFormatException e) {
                    Ui.showError(Messages.MESSAGE_INVALID_IDTYPE);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Ui.showError("There are no projects! Please create a project first.");
        }
    }

    public void viewTaskCommand(Hashtable<String, String> taskId, ProjectList projectListManager) {

        try {
            Project proj = projectListManager.getProject();
            if (taskId.isEmpty()) {
                Ui.showError("Missing parameters.");
            }
            Ui.showToUserLn("The details of the tasks are as follows: ");
            for (int i = 0; i < taskId.size(); i++) {
                Task task;
                try {
                    int backlogId = Integer.parseInt(taskId.get(Integer.toString(i)));
                    if (backlogId <= proj.getProjectBacklog().backlogTasks.size()) {
                        task = proj.getProjectBacklog().getTask(backlogId);
                        Ui.showToUserLn(task.toString());
                        //Ui.showToUserLn("\t Title: " + task.getTitle());
                    } else {
                        Ui.showError(Messages.MESSAGE_INVALID_ID);
                    }
                } catch (NumberFormatException e) {
                    Ui.showError(Messages.MESSAGE_INVALID_IDTYPE);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Ui.showError("There are no projects! Please create a project first.");
        }
    }

    public void changeTaskPriorityCommand(Hashtable<String, String> tasks, ProjectList projectListManager)
            throws DukeException {

        Task task;
        int id;
        String priority;

        id = Integer.parseInt(tasks.get(TASK_ID));
        priority = tasks.get(PRIORITY).trim();

        try {
            Project proj = projectListManager.getProject();
            try {
                task = proj.getProjectBacklog().getTask(id);
                if (!proj.getProjectBacklog().checkValidPriority(priority)) {
                    throw new DukeException("Invalid priority");
                }
                task.setPriority(priority);
                Ui.showToUserLn("The task " + task.getTitle() + " has its priority changed to:");
                Ui.showToUserLn("\t" + task.getPriority());
            } catch (IndexOutOfBoundsException e) {
                throw new DukeException("Task ID entered is out of bounds!");
            } catch (IllegalArgumentException e) {
                throw new DukeException("Priority entered is invalid!");
            }
        } catch (IndexOutOfBoundsException e) {
            Ui.showError("There are no projects! Please create a project first.");
        }
    }

    public void doneTaskCommand(Hashtable<String, String> taskId, ProjectList projectListManager) {

        try {
            Project proj = projectListManager.getProject();
            for (int i = 0; i < taskId.size(); i++) {
                Task task;
                try {
                    int backlogId = Integer.parseInt(taskId.get(Integer.toString(i)));
                    if (backlogId <= proj.getProjectBacklog().backlogTasks.size()) {
                        task = proj.getProjectBacklog().getTask(backlogId);
                        task.setAsDone();
                        Ui.showToUserLn(task.getTitle() + " has been marked as done.");
                    } else {
                        Ui.showError(Messages.MESSAGE_INVALID_ID);
                    }
                } catch (NumberFormatException e) {
                    Ui.showError(Messages.MESSAGE_INVALID_IDTYPE);
                } catch (IndexOutOfBoundsException e) {
                    Ui.showError("There are no projects! Please create a project first.");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Ui.showError("There are no projects! Please create a project first.");
        }
    }
}

