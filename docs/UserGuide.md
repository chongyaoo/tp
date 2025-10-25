# User Guide

## Introduction

StudyMate is a desktop task management application designed for students to manage their tasks, deadlines, events, reminders, and study timers efficiently. It uses a Command Line Interface (CLI) for fast and efficient task management.

## Quick Start

1. Ensure that you have Java 17 or above installed.
2. Download the latest version of `StudyMate` from the releases page.
3. Copy the file to the folder you want to use as the home folder for StudyMate.
4. Open a command terminal, navigate to the folder containing the JAR file, and run `java -jar studymate.jar`.
5. Type commands in the terminal and press Enter to execute them.

## Features 

### Adding a todo: `todo`

Adds a simple task to your task list without any specific deadline or time frame.

**Format:** `todo DESCRIPTION`

* The `DESCRIPTION` is the name or details of the todo task.
* The description cannot be empty.

**Examples:**
* `todo Read chapter 5 of textbook`
* `todo Buy groceries`
* `todo Complete math homework`

**Expected output:**
```
Got it! I've added this task:
  [T][ ] Read chapter 5 of textbook
Now you have X tasks in the list.
```

**Notes:**
* Todo tasks are marked with `[T]` to indicate they are simple tasks.
* `[ ]` indicates the task is not yet completed. It will show `[X]` when marked as done.
* Use the `mark` command to mark a todo as completed.
* Use the `list` command to view all your tasks including todos.

### Adding a deadline: `deadline`

Adds a task with a specific deadline date to your task list.

**Format:** `deadline DESCRIPTION /by YYYY-MM-DD`

* The `DESCRIPTION` is the name or details of the deadline task.
* The `/by` delimiter is required to separate the description from the deadline date.
* The deadline date must be in `YYYY-MM-DD` format (e.g., 2025-12-31).
* Both description and deadline cannot be empty.

**Examples:**
* `deadline Submit assignment /by 2025-11-15`
* `deadline Project proposal /by 2025-10-30`
* `deadline Return library books /by 2025-11-01`

**Expected output:**
```
Got it! I've added this task:
  [D][ ] Submit assignment (by: Oct 15 2025)
Now you have X tasks in the list.
```

**Notes:**
* Deadline tasks are marked with `[D]` to indicate they have a due date.
* The date is stored and displayed in a readable format.
* Use `list -s` to view tasks sorted by date (earliest deadline first).

### Adding an event: `event`

Adds an event with a start date and end date to your task list.

**Format:** `event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD`

* The `DESCRIPTION` is the name or details of the event.
* The `/from` delimiter specifies the start date of the event.
* The `/to` delimiter specifies the end date of the event.
* Both delimiters are required, and `/from` must come before `/to`.
* All dates must be in `YYYY-MM-DD` format.
* The description, start date, and end date cannot be empty.
* The end date must be on or after the start date.

**Examples:**
* `event Project week /from 2025-11-01 /to 2025-11-07`
* `event Conference /from 2025-12-10 /to 2025-12-12`
* `event Study camp /from 2025-11-20 /to 2025-11-22`

**Expected output:**
```
Got it! I've added this task:
  [E][ ] Project week (from: Nov 1 2025, to: Nov 7 2025)
Now you have X tasks in the list.
```

**Notes:**
* Event tasks are marked with `[E]` to indicate they span a time period.
* Both start and end dates are displayed.
* Use `list -s` to view tasks sorted by date (earliest start date first).
* The end date cannot be before the start date.

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: Copy the `data/tasks.txt` file from your current StudyMate folder to the same location in the new computer's StudyMate folder.

**Q**: Can I add a todo with special characters?

**A**: Yes, the todo description can contain any characters except it cannot be empty.

## Command Summary

* Add todo: `todo DESCRIPTION`
* Add deadline: `deadline DESCRIPTION /by YYYY-MM-DD`
* Add event: `event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD`
* List all tasks: `list`
* List tasks sorted by date: `list -s`
* Mark task as done: `mark INDEX`
* Delete task: `delete INDEX`
