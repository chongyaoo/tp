# User Guide

---

## Introduction

StudyMate is a desktop task management application designed for students to manage their tasks, deadlines, events, reminders, and study timers efficiently. It uses a Command Line Interface (CLI) for fast and efficient task management.

---

## Quick Start

1. Ensure that you have Java 17 or above installed.
2. Download the latest version of `StudyMate` from the releases page.
3. Copy the file to the folder you want to use as the home folder for StudyMate.
4. Open a command terminal, navigate to the folder containing the JAR file, and run `java -jar studymate.jar`.
5. Type commands in the terminal and press Enter to execute them.

---

## Features 

---

## Tasks

StudyMate supports three kinds of tasks: todo, deadline, and event. You can also list, search, edit, mark, unmark, or delete them efficiently with CLI commands. All commands are case-insensitive, but task indices always start at 1 (as displayed in the list view).

---

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
* Use the `unmark` command a mark a todo as incomplete.
* Use the `list` command to view all your tasks.

---

### Adding a deadline: `deadline`

Adds a task with a specific deadline date and time to your task list.

**Format:** `deadline DESCRIPTION /by YYYY-MM-DD HH:mm`

* The `DESCRIPTION` is the name or details of the deadline task.
* The `/by` delimiter is required to separate the description from the deadline.
* The deadline must be in `YYYY-MM-DD HH:mm` format (e.g., 2025-12-31 23:59).
* Both description and deadline are required.

**Examples:**
* `deadline Submit assignment /by 2025-11-15 23:59`
* `deadline Project proposal /by 2025-10-30 17:00`
* `deadline Return library books /by 2025-11-01 09:00`

**Expected output:**
```
Got it! I've added this task:
  [D][ ] Submit assignment (by: 2025-11-15 23:59)
Now you have X tasks in the list.
```

**Notes:**
* Deadline tasks are marked with `[D]` to indicate they have a due date and time.
* The date and time are stored and displayed in YYYY-MM-DD HH:mm format.
* Use `list -s` to view tasks sorted by date and time (earliest deadline first).

---

### Adding an event: `event`

Adds an event with a start date/time and end date/time to your task list.

**Format:** `event DESCRIPTION /from YYYY-MM-DD HH:mm /to YYYY-MM-DD HH:mm`

* The `DESCRIPTION` is the name or details of the event.
* The `/from` delimiter specifies the start date and time of the event.
* The `/to` delimiter specifies the end date and time of the event.
* Both delimiters are required, and `/from` must come before `/to`.
* All date/time values must be in `YYYY-MM-DD HH:mm` format.
* The description, start date/time, and end date/time cannot be empty.
* The end date/time must be on or after the start date/time.

**Examples:**
* `event Project week /from 2025-11-01 09:00 /to 2025-11-07 17:00`
* `event Conference /from 2025-12-10 08:00 /to 2025-12-12 18:00`
* `event Study camp /from 2025-11-20 14:00 /to 2025-11-22 16:00`

**Expected output:**
```
Got it! I've added this task:
  [E][ ] Project week (from: 2025-11-01 09:00, to: 2025-11-07 17:00)
Now you have X tasks in the list.
```

**Notes:**
* Event tasks are marked with `[E]` to indicate they span a time period.
* Both start and end date/times are displayed in YYYY-MM-DD HH:mm format.
* Use `list -s` to view tasks sorted by date (earliest start date/time first).
* The end date/time cannot be before the start date/time.
* If you supply dates/times in the wrong order, or with invalid format, you will get a parsing error.

---

### Listing Tasks: `list`

Shows all tasks in your list, in order of addition or by date.

**Format:**
* Listed as added: `list`
* List sorted by date: `list -s`

**Expected Output:**

All current tasks, each line numbered and showing completion, type, description, and any dates/times.

Normal List
```
Here are the tasks in your task list:
1. [T][ ] Read chapter 5 of textbook
2. [D][ ] Submit assignment (by: 2025-11-15 23:59)
3. [E][ ] Project week (from: 2025-11-01 09:00, to: 2025-11-07 17:00)
```

Sorted List
```
Here are the deadlines and events in your task list, sorted by their deadlines and/or start times:
1. [E][ ] Project week (from: 2025-11-01 09:00, to: 2025-11-07 17:00)
2. [D][ ] Submit assignment (by: 2025-11-15 23:59)
```

**Notes:**
* `list -s` sorts by date (for events/deadlines, earliest first).

---

### Finding Tasks: `find`

Search for tasks by keyword.

**Format:** `find KEYWORD`
* `KEYWORD` is any word or substring.

**Examples:**
* `find assignment`
* `find exam`

**Expected Output:**
```
Here are the tasks with the matching substring found!:
1. [D][ ] Submit assignment (by: 2025-11-15 23:59)
```

**Notes:**
* Search is case-insensitive.
* If no match, you will receive an empty result.

---

### Marking tasks: `mark`

Mark one or more tasks as "done".

**Format:** `mark INDEX[,INDEX...]`
* `INDEX` corresponds to the task number from the list command.
* Can accept multiple indices separated by commas (e.g., `mark 3,5`), or ranges (e.g., `mark 2...4`).

**Examples:**
* `mark 1`
* `mark 2,4,6`
* `mark 3..5`

**Expected Output:**

Shows tasks now marked [X] as done.
```
Nice! I've marked these tasks as done:
[T][X] Read chapter 5 of textbook
[D][X] Submit assignment (by: 2025-11-15 23:59)
[E][X] Project week (from: 2025-11-01 09:00, to: 2025-11-07 17:00)
```

**Notes:**
* Multiple indices and ranges are supported (`mark 2,5...7` marks tasks 2, 5, 6, and 7).
* Indices must be valid, out-of-bounds numbers trigger parsing errors.

---

### Unmarking tasks: `unmark`

Mark one or more tasks as "not done".

**Format:** `unmark INDEX[,INDEX...]`
* `INDEX` corresponds to the task number from the list command.
* Can accept multiple indices separated by commas (e.g., `unmark 3,5`), or ranges (e.g., `unmark 2...4`).

**Expected Output:**

Shows tasks now back to [ ] incomplete.
```
OK, I've marked these tasks as not done yet:
[T][ ] Read chapter 5 of textbook
[D][ ] Submit assignment (by: 2025-11-15 23:59)
[E][ ] Project week (from: 2025-11-01 09:00, to: 2025-11-07 17:00)
```

**Notes:**
* Multiple indices and ranges are supported (`unmark 2,5...7` unmarks tasks 2, 5, 6, and 7).
* Indices must be valid, out-of-bounds numbers trigger parsing errors.

---

### Editing a Task: `edit`

Update a task’s description or the relevant dates/times.

**Format:**
* Edit name/description: `edit INDEX -n NEW_DESCRIPTION`
* Edit deadline (deadline task): `edit INDEX -d NEW_DEADLINE`
* Edit event start (event task): `edit INDEX -f NEW_START`
* Edit event end (event task): `edit INDEX -t NEW_END`
* All date/time values must use format YYYY-MM-DD HH:mm.
* Only update fields that apply to the type of task (e.g., deadlines for deadline tasks, start/end for event tasks).

**Examples:**
* `edit 3 -n Finish reading Act II`
* `edit 2 -d 2025-12-01 20:00`
* `edit 4 -f 2025-11-05 09:00`
* `edit 4 -t 2025-11-06 17:00`

**Expected Output:**

Task updated with new details.

```
OK, I've edited the description of the task to:
[E][ ] Finish reading Act II (from: 2025-11-01 09:00, to: 2025-11-07 17:00)
```

**Notes:**
* Invalid indices or edits to fields not present on the task type (e.g., deadline on a todo) will result in errors.
* All edits use the one-based indices shown in list.

---

### Deleting Tasks: `delete`

Removes one or more tasks from your list

**Format:** `delete INDEX[,INDEX...]`
* `INDEX` corresponds to the task number from the list command.
* Can accept multiple indices separated by commas (e.g., `delete 3,5`), or ranges (e.g., `delete 2...4`).

**Examples:**
* `delete 1`
* `delete 2,4,6`
* `delete 3..5`

**Expected Output:**

Confirmation message and the updated task count.

```
Got it. I've deleted these tasks:
[T][ ] Read chapter 5 of textbook
Now you have 2 tasks in the task list.
```

**Notes:**
* Multiple indices and ranges are supported (`delete 2,5...7` deletes tasks 2, 5, 6, and 7).
* Indices must be valid, out-of-bounds numbers trigger parsing errors.

---

## Reminders

StudyMate's reminder system helps you stay on top of important events and recurring tasks. Set one-time or recurring reminders with customizable intervals, and manage them with simple CLI commands.

---

### Adding a One-Time Reminder: `rem`

Creates a reminder that fires once at a specified date and time.

**Format:** `rem MESSAGE @ YYYY-MM-DD HH:mm`

* The `MESSAGE` is the text that will be displayed when the reminder fires.
* The `@` delimiter is required to separate the message from the date/time.
* The date and time must be in `YYYY-MM-DD HH:mm` format.
* The reminder must be set for a future date/time.

**Examples:**
* `rem Submit project report @ 2025-11-15 09:00`
* `rem Doctor appointment @ 2025-11-01 14:30`
* `rem Call mom @ 2025-10-31 18:00`

**Expected output:**
```
Got it! I've added this One-Time reminder:
[RO][O] Submit project report (2025-11-15 09:00)
Now you have X reminders in the reminder list.
```

**Notes:**
* Reminders are marked with `[RO]` to indicate they are one-time reminders.
* `[O]` indicates the reminder is active. It will show `[ ]` when turned off.
* One-time reminders automatically turn off after firing.

---

### Adding a Recurring Reminder: `rem`

Creates a reminder that repeats at regular intervals.

**Format:** `rem MESSAGE @ YYYY-MM-DD HH:mm -r INTERVAL`

* The `MESSAGE` is the text that will be displayed when the reminder fires.
* The `@` delimiter separates the message from the initial date/time.
* The `-r` flag indicates this is a recurring reminder.
* The `INTERVAL` must follow the format: number + unit
    * `m` for minutes (e.g., `30m`)
    * `h` for hours (e.g., `2h`)
    * `d` for days (e.g., `1d`)
    * `w` for weeks (e.g., `1w`)

**Examples:**
* `rem Take medicine @ 2025-10-31 08:00 -r 12h`
* `rem Weekly review @ 2025-11-01 10:00 -r 1w`
* `rem Standup meeting @ 2025-10-31 09:00 -r 1d`

**Expected output:**
```
Got it! I've added this recurring reminder:
[RR][O] Take medicine (interval: 12H)
Next reminder: 2025-10-31 08:00
Now you have X reminders in the reminder list.
```

**Notes:**
* After firing, recurring reminders automatically reschedule to the next interval.
* Recurring reminders remain `[O]` after firing unless manually turned off.

---

### Listing Reminders: `rem ls`

Displays all your reminders with their status and scheduled times.

**Format:** `rem ls`

**Expected output:**
```
Here are your reminders:
1. [RO][O] Submit project report (2025-11-15 09:00)
2. [RO][O] Take medicine (2025-10-31 08:00)
3. [RR][O] Weekly review (interval: 1w)
Next reminder: 2025-10-30 20:00
```

**Notes:**
* Each reminder shows its index number, status, message, and schedule.
* Use these index numbers for other reminder commands.
* For Recurring reminders, the timing of the next reminder will also be shown.

---

### Turning Reminders On: `rem on`

Activates one or more reminders.

**Format:** `rem on INDEX[,INDEX...]`

* `INDEX` corresponds to the reminder number from the `rem ls` command.
* Can accept multiple indices separated by commas (e.g., `rem on 1,3`).

**Examples:**
* `rem on 2`
* `rem on 1,3,5`

**Expected output:**
```
The following reminders have been turned on:
[RO][O] Weekly review (2025-11-01 10:00)

The following reminders have already been turned on:
[RO][O] Go the the toilet (2025-11-01 11:00)
```

**Notes:**
* If a reminder is already on, you'll be notified separately.
* Only `[O]` reminders will fire when their time comes.

---

### Turning Reminders Off: `rem off`

Deactivates one or more reminders without deleting them.

**Format:** `rem off INDEX[,INDEX...]`

* `INDEX` corresponds to the reminder number from the `rem ls` command.
* Can accept multiple indices separated by commas (e.g., `rem off 2,4`).

**Examples:**
* `rem off 3`
* `rem off 1,2`

**Expected output:**
```
The following reminders have been turned off:
[RO][ ] Take medicine (2025-10-31 08:00)
```

**Notes:**
* Turned off reminders won't fire but remain in your list.
* You can turn them back on with `rem on` when needed.

---

### Snoozing a Reminder: `rem snooze`

Postpones a one-time reminder by a specified duration.

**Format:** `rem snooze INDEX INTERVAL`

* `INDEX` is the reminder number from the `rem ls` command.
* `INTERVAL` follows the same format as recurring reminders: number + unit (`m`, `h`, `d`, `w`)
* The new time must be in the future.
* **Only works with one-time reminders.**

**Examples:**
* `rem snooze 1 30m` - Snooze for 30 minutes
* `rem snooze 2 2h` - Snooze for 2 hours
* `rem snooze 3 1d` - Snooze for 1 day

**Expected output:**
```
Snooze duration too short! New reminder time (2025-10-17T10:00) is not in the future.

The following reminder has successfully been snoozed: 
[RO][O] Going to the toilet (2025-11-14 10:00)
```

**Notes:**
* Recurring reminders cannot be snoozed - they automatically reschedule.
* If the snooze duration is too short (new snoozed timing is still in the past), you'll get an error, as shown in the 
first Expected Output.
* Snoozing automatically turns the reminder back on.

---

### Deleting Reminders: `rem rm`

Permanently removes one or more reminders.

**Format:** `rem rm INDEX[,INDEX...]`

* `INDEX` corresponds to the reminder number from the `rem ls` command.
* Can accept multiple indices separated by commas (e.g., `rem rm 2,4`).

**Examples:**
* `rem rm 1`
* `rem rm 2,3,5`

**Expected output:**
```
Got it. I've deleted these reminders:
[RO][O] Running (2025-11-14 10:00)
Now you have 1 reminder in the Reminders list.
```

**Notes:**
* Deletion is permanent and cannot be undone.
* Index numbers will update after deletion.

---

### Reminder Behavior and Rules

* **Background Monitoring**: The system checks for due reminders every 30 seconds in the background while StudyMate is running. This means reminders may fire up to 30 seconds after their scheduled time.


* **Firing Notifications**: When a reminder fires, you'll see a notification message displaying the reminder's index number and message. This appears in your terminal regardless of what command you're currently typing.


* **One-Time Reminder Lifecycle**:
    * One-time reminders fire once at their scheduled time.
    * After firing, they automatically turn `[ ]` to prevent repeat notifications.
    * You can safely delete fired one-time reminders or leave them in your list as a record.


* **Recurring Reminder Lifecycle**:
    * Recurring reminders fire at their scheduled time, then automatically reschedule to the next interval.
    * They remain `[O]` after firing and will continue to fire at each interval.
    * The next scheduled time is calculated by adding the interval to the current time when fired.
    * To stop recurring reminders, use `rem off` or delete them with `rem rm`.


* **Time Precision**:
    * A reminder fires once when the current time equals or exceeds its scheduled time
    * The 30-second check interval means a reminder scheduled for 14:30:00 will fire on the first check after 14:30:00 (between 14:30:00 and 14:30:30), if StudyMate is on during this period.
    * After firing, the reminder either turns off (One-Time) or reschedules (Recurring), preventing duplicate notifications


* **Reminder Status Control**:
    * Only reminders marked `[O]` will fire when their time comes
    * `[ ]` reminders are skipped during background checks
    * Turning a reminder off does not change its scheduled time
    * Snoozed reminders are automatically turned back `[O]`


* **Persistence**: All reminders (including their on/off status) are automatically saved when you exit with `bye` and restored when you restart StudyMate.

---

## Timer

The timer feature supports efficient time management for study sessions using simple CLI commands. Only one timer session can be active at a time, and all commands are case-insensitive.

---

### Starting a Timer: `start`

Begin a study timer, optionally linked to a specific task, or use a custom label and duration.

**Format:** `start [INDEX|NAME] [@MINUTES]`
* `INDEX`: (Optional) Task number from the `list` command
* `NAME`: (Optional) Custom label for the timer session
* `@MINUTES`: (Optional) Duration in minutes (default: 25)
* If no parameters, `start` starts a 25 minute "Focus Session"
* If `INDEX` is supplied (e.g. `start 3 @ 45`), it starts a timer label with task 3's name for 45 minutes
* If `NAME` and duration are supplied (e.g. `start Review Notes @ 30`), it starts a 30 minute timer labeled "Review Notes"
* Only one timer can be active, you will receive an error if another timer's time has not run out

**Examples:**
* `start` (starts default 25 minute "Focus Session")
* `start 2` (starts timer for task 2, default 25 minutes)
* `start 4@40` (starts timer for task 4, 40 minutes)
* `start Research @ 60` (starts timer for "Research", 60 minutes)

**Expected Output:**
```
# TIMER
# RUNNING 25:00 left - Focus session
```

**Notes:**
* Only one timer can run or be paused at a time. Reset or complete before starting another.

---

### Pausing a Timer: `pause`

Temporarily stop the active timer

**Format:** `pause`

**Expected Output:**
```
# TIMER
# PAUSED 23:35 left - Focus session
```

**Notes:**
* Only works if the timer is running
* Trying to pause a timer that is already paused or stopped will result in an error

---

### Resuming a Timer: `resume`

Continue a paused timer.

**Format:** `resume`

**Expected Output:**
```
# TIMER
# RUNNING 23:35 left - Focus session
```

**Notes:**
* Only works if the timer is paused
* Timer resumes with the remaining time that was left when paused

---

### Resetting a Timer: `reset`

Stop and clear the active timer session.

**Format:** `reset`

**Expected Output:**
```
# TIMER
# RESET TIMER
```

**Notes:**
* Only works if a timer is active (running or paused)
* Stops monitoring and clears session, must start a new timer to begin tracking again

---

### Checking Timer Status: `stat`

Displays the current timer status and time remaining.

**Format:** `stat`

**Expected Output:**
```
Timer Status
  State: RUNNING
  Time Left: 0:02
  Label: Focus session
```

**Notes:**
* Only works if a timer is active
* Shows details like timer label, state (RUNNING/PAUSED/IDLE), and exact time left

---

### Timer Behaviour and Rules
* Only a single timer sessions can be active at a time
* The timer can be associated with a task (by index), or a custom label
* Time always counts down to zero. When finished, you are notified and the timer is cleared for a new session
* Time is tracked precisely to seconds, the UI displays minutes:seconds
* If you pause, resume or reset the timer, actions are immediate and confirmed in output
* If you attempt commands with no active timer, or redudant actions (e.g. resume while already running), you get a friendly error

---

## Habits: `habit`

Track recurring habits with streak counting to build consistency. Habits help you maintain regular activities by setting deadlines and rewarding on-time completion with streak increments.

---

### Adding a Habit:`habit DESCRIPTION -t INTERVAL`

Creates a new habit with a specified time interval between completions.

**Format:** `habit DESCRIPTION -t INTERVAL`

* The `DESCRIPTION` is the name of the habit you want to track.
* The `-t` flag is required to specify the interval.
* The `INTERVAL` must follow the format: number + unit
  * `m` for minutes (e.g., `30m`)
  * `h` for hours (e.g., `2h`)
  * `d` for days (e.g., `1d`)
  * `w` for weeks (e.g., `1w`)
* The habit starts with a streak of 1.
* The first deadline is automatically set to current time + interval.

**Examples:**
* `habit Exercise -t 1d` - Daily exercise habit
* `habit Meditation -t 12h` - Twice-daily meditation
* `habit Read book -t 1w` - Weekly reading habit
* `habit Drink water -t 2h` - Hydration reminder every 2 hours

**Expected output:**
```
Got it! I've added this habit:
  [H] Exercise (deadline: 2025-10-27 14:30, streak: 1)
Now you have X habits in the list.
```

**Notes:**
* Habits are marked with `[H]` to indicate habit tracking.
* The deadline shows when you should next complete the habit.
* Your streak tracks consecutive successful completions.

---

### Listing Habits: `habit ls`

Displays all your tracked habits with their deadlines and current streaks.

**Format:** `habit ls`

**Expected output:**
```
Here are your habits:
1. [H] Exercise (deadline: 2025-10-27 14:30, streak: 5)
2. [H] Meditation (deadline: 2025-10-26 20:00, streak: 3)
3. [H] Read book (deadline: 2025-11-02 14:30, streak: 1)
```

**Notes:**
* Each habit shows its index number, name, next deadline, and current streak.
* Use these index numbers for streak increments and deletions.

---

### Incrementing Habit Streak: `habit streak INDEX`

Attempts to increment the streak for a habit when you complete it.

**Format:** `habit streak INDEX`

* The `INDEX` is the number shown in the habit list.
* Timing matters! The system checks if you're completing on time:
  * **Too Early**: You haven't reached the deadline yet
  * **On Time**: You're between the deadline and the grace period
  * **Too Late**: You've exceeded the grace period (streak resets to 1)

**Grace Period:**
The grace period is calculated as: **deadline + (interval ÷ 4) + 1 minute**

For example:
* Daily habit (24h interval): 6 hour grace period
* Weekly habit (7d interval): 1.75 day grace period
* Hourly habit (1h interval): 16 minute grace period

**Examples:**
* `habit streak 1` - Increment the first habit's streak
* `habit streak 3` - Increment the third habit's streak

**Expected outputs:**

*When on time:*
```
Great! You've incremented your streak.
Habit: [H] Exercise (deadline: 2025-10-28 14:30, streak: 6)
```

*When too early:*
```
Too early! You can only increment the streak after the deadline.
Habit: [H] Exercise (deadline: 2025-10-27 14:30, streak: 5)
```

*When too late:*
```
Missed the deadline! Your streak has been reset to 1.
Habit: [H] Exercise (deadline: 2025-10-28 14:30, streak: 1)
```

**Notes:**
* When you successfully increment (on time), the deadline automatically updates to: current time + interval.
* If you're too late, the streak resets to 1 (not 0), giving you credit for the current completion.
* The system compares times truncated to the minute level, so completing at any second within the same minute as the deadline counts as on-time.

---

### Deleting a Habit: `habit rm INDEX`

Removes a habit from your tracking list.

**Format:** `habit rm INDEX`

* The `INDEX` is the number shown in the habit list.

**Examples:**
* `habit rm 2` - Delete the second habit
* `habit rm 1` - Delete the first habit

**Expected output:**
```
Noted! I've removed this habit:
  [H] Meditation (deadline: 2025-10-26 20:00, streak: 3)
Now you have X habits in the list.
```

**Notes:**
* Deleting a habit is permanent and cannot be undone.
* The index numbers will update after deletion.

---

### Exiting the Application: `bye`

Safely closes StudyMate, saving all your data automatically before shutdown.

**Format:** `bye`

**Expected Output:**
```
Bye. Hope to see you again soon!
```

**Notes:**
* All tasks, habits, reminders, and timer states are saved before exiting.
* After this command, you can close your terminal safely.

---

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: Copy the `data/StudyMate.txt` file from your current StudyMate folder to the same location in the new computer's StudyMate folder. This file contains all your tasks, habits, and reminders.

**Q**: Can I add a todo with special characters?

**A**: Yes, the todo description can contain any characters except it cannot be empty.

**Q**: What happens if I miss my habit deadline?

**A**: If you try to increment your streak after the grace period has passed, your streak will reset to 1 (not 0). You still get credit for the current completion, but you'll need to rebuild your streak from there.

**Q**: How is the grace period calculated for habits?

**A**: The grace period is (interval ÷ 4) + 1 minute. For example, a daily habit (24h) has a 6-hour grace period, while a weekly habit (7d) has a 1.75-day grace period. This gives you reasonable flexibility while still encouraging consistency.

**Q**: Can I change a habit's interval after creating it?

**A**: No, you cannot modify a habit's interval. If you need a different interval, delete the old habit and create a new one. Note that this will reset your streak.

**Q**: Why does the system use minutes instead of seconds for timing?

**A**: The system truncates timing to minutes to be more user-friendly. This means if your deadline is at 14:30:59 and you complete at 14:30:00, it still counts as on-time because both are in the same minute.

---

## Command Summary

**Task Management:**
* Add todo: `todo DESCRIPTION`
* Add deadline: `deadline DESCRIPTION /by YYYY-MM-DD HH:mm`
* Add event: `event DESCRIPTION /from YYYY-MM-DD HH:mm /to YYYY-MM-DD HH:mm`
* List all tasks: `list`
* List tasks sorted by date: `list -s`
* Mark task as done: `mark INDEX`
* Unmark task: `unmark INDEX`
* Edit task: `edit INDEX -FLAG VALUE`
* Delete task: `delete INDEX`
* Find tasks: `find KEYWORD`

**Reminders:**
* Add one-time reminder: `rem MESSAGE @ YYYY-MM-DD HH:mm`
* Add recurring reminder: `rem MESSAGE @ YYYY-MM-DD HH:mm -r INTERVAL`
* List reminders: `rem ls`
* Delete reminder: `rem rm INDEX`
* Turn reminder on: `rem on INDEX`
* Turn reminder off: `rem off INDEX`
* Snooze reminder: `rem snooze INDEX INTERVAL`

**Timer:**
* Start timer: `start [INDEX|NAME] [@MINUTES]`
* Pause timer: `pause`
* Resume timer: `resume`
* Reset timer: `reset`
* Show timer status: `stat`

**Habit Tracking:**
* Add habit: `habit DESCRIPTION -t INTERVAL`
* List habits: `habit ls`
* Increment streak: `habit streak INDEX`
* Delete habit: `habit rm INDEX`

**Other:**
* Exit application: `bye`
