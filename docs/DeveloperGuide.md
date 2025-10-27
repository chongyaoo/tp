# Developer Guide

## Acknowledgements

Thank you teaching team of CS2113!

## Design & implementation

### Parser Component

**API**: `Parser.java`

The Parser component is responsible for interpreting user input and converting it into executable commands that the application can process.

#### Structure of the Parser Component

The Parser component consists of several key classes that work together to handle command parsing and validation:

![Class Diagram of Parser Component](images/Parser.png)

* `Parser` - The main parser class that interprets raw user input and creates `Command` objects
* `Command` - Encapsulates the parsed command with its type and relevant parameters
* `CommandType` - An enumeration that defines all supported command types in the application
* `DateTimeArg` - Encapsulates date/time arguments for deadline, event, and reminder commands with both date and optional time components
* `StudyMateException` - Exception type thrown when parsing errors occur

The Parser component works with the following workflow:

1. **User Input Processing**: Raw string input from the user is received
2. **Command Parsing**: The `Parser` analyses the input and identifies the command type
3. **Parameter Extraction**: Relevant parameters (descriptions, indices, dates, times, etc.) are extracted and validated
4. **Command Object Creation**: A `Command` object is instantiated with the appropriate `CommandType` and parameters
5. **Error Handling**: If parsing fails, a `StudyMateException` is thrown with a descriptive error message

#### Parser Component Interactions

The diagram below shows how the Parser component interacts with other components in the system:

![Parser Interactions Diagram](images/ParserInteractions.png)

The Parser creates Command objects that are consumed by CommandHandler. It uses DateTimeArg for temporal data and throws StudyMateException for errors.

#### How the Parser Component Works

The sequence diagram below illustrates the interactions within the Parser component, taking `parse("todo read book")` as an example:

![Sequence Diagram for Parsing Todo Command](images/ParserToDo.png)

**Example: Parsing "todo read book"**

When the Parser component is called upon to parse a command, the following steps occur:

1. The `StudyMate` main class creates a `Parser` instance and calls its `parse()` method with the user input string (e.g., "todo read book").

2. The `Parser` normalises the input by collapsing multiple whitespaces into single spaces using `replaceAll("\\s+", " ")`.

3. The `Parser` splits the input into command word and arguments using `split(" ", 2)`, resulting in:
   - `arguments[0]` = "todo"
   - `arguments[1]` = "read book"

4. Based on the identified command type, the `Parser` invokes the appropriate private parsing method:
   * `parseToDo()` - for todo commands
   * `parseDeadline()` - for deadline commands (requires /by delimiter)
   * `parseEvent()` - for event commands (requires /from and /to delimiters)
   * `parseFind()` - for find commands
   * `parseList()` - for list commands (with optional -s sorting flag)
   * `parseEdit()` - for edit commands (supports -n, -d, -f, -t flags)
   * `parseMark()`, `parseUnmark()`, `parseDelete()` - for index-based task operations
   * `parseRem()` - for reminder-related commands, which routes to:
     * `parseRemOn()`, `parseRemOff()` - turns reminders on/off
     * `parseRemRm()` - removes reminders
     * `parseRemLs()` - lists reminders
     * `parseRemAdd()` - adds one-time or recurring reminders
     * `parseRemSnooze()` - snoozes a reminder
   * `parseTimerStart()` - for timer start commands (with optional duration and task index/label)
   * `parseHabit()` - for habit-related commands, which routes to:
     * `parseHabitAdd()` - adds a new habit with interval
     * `parseHabitStreak()` - increments a habit's streak
     * `parseHabitRm()` - removes a habit
     * Returns `HABIT_LIST` command for listing habits

5. Each parsing method:
   * Validates the command syntax and parameters
   * Extracts relevant data (descriptions, dates, times, indices, durations, intervals)
   * Throws `StudyMateException` if the input format is invalid
   * Creates and returns a `Command` object with the appropriate `CommandType` and parameters

6. The resulting `Command` object is returned to `StudyMate`, which passes it to `CommandHandler.executeCommand()`.

7. The `CommandHandler` uses a switch statement on the `CommandType` to route the command to the appropriate handler method:
   * Task commands are handled by methods like `handleToDo()`, `handleDeadline()`, `handleEvent()`, `handleMark()`, `handleEdit()`, `handleDelete()`
   * Reminder commands are handled by methods like `handleRemAddOneTime()`, `handleRemAddRec()`, `handleRemList()`, `handleRemOn()`, `handleRemOff()`, `handleRemSnooze()`
   * Timer commands are handled by methods like `handleTimerStart()`, `handleTimerPause()`, `handleTimerResume()`, `handleTimerReset()`, `handleTimerStat()`
   * Habit commands are handled by methods like `handleHabitAdd()`, `handleHabitList()`, `handleHabitStreak()`, `handleHabitDelete()`

8. Handler methods interact with the Model components (TaskList, ReminderList, HabitList, Timer) to execute the command logic.

9. The `IndexValidator` is used when commands involve list indices to ensure they are within valid ranges before execution.

10. Results are communicated back to the user through the `MessageHandler` UI component.

#### Key Design Considerations

**Aspect: Index handling**

* **Current choice**: Support multiple index formats (single, comma-separated, ranges with "...")
  * Pros: User-friendly; allows batch operations; flexible input; reduces repetitive commands
  * Cons: More complex parsing logic; higher chance of parsing errors; need to handle edge cases (overlapping ranges, invalid ranges)

* **Alternative**: Support only single indices
  * Pros: Simpler parsing; fewer edge cases; easier to debug
  * Cons: Less convenient for users; requires multiple commands for batch operations; reduced productivity

**Aspect: Time handling in reminders**

* **Current choice**: Require time component for all reminders (format: `yyyy-MM-dd HH:mm`)
  * Pros: Precise scheduling; consistent behaviour with recurring reminders; no ambiguity about when reminders fire
  * Cons: Slightly more verbose input; users must specify time even for all-day reminders

* **Alternative**: Make time optional and default to midnight or current time
  * Pros: More convenient for date-only reminders; less typing
  * Cons: Ambiguity about default time; inconsistent reminder behaviour; harder to reason about recurring reminder schedules

#### Supported Command Formats

The Parser component supports the following command formats:

**Task Management:**
* `todo DESCRIPTION` - Creates a todo task
* `deadline DESCRIPTION /by DATE TIME` - Creates a deadline task (DATE TIME format: yyyy-MM-dd HH:mm)
* `event DESCRIPTION /from DATE TIME /to DATE TIME` - Creates an event task (DATE TIME format: yyyy-MM-dd HH:mm)
* `list` - Lists all tasks
* `list -s` - Lists all deadlines and events sorted by date (soonest first)
* `find KEYWORD` - Finds tasks containing the keyword
* `edit INDEX -n DESCRIPTION` - Edits task description
* `edit INDEX -d DATE TIME` - Edits deadline date (for deadline tasks only)
* `edit INDEX -f DATE TIME` - Edits event from date (for event tasks only)
* `edit INDEX -t DATE TIME` - Edits event to date (for event tasks only)
* `mark INDEX[,INDEX...]` - Marks tasks as done
* `unmark INDEX[,INDEX...]` - Marks tasks as not done
* `delete INDEX[,INDEX...]` - Deletes tasks

**Reminder Management:**
* `rem MESSAGE @ DATE TIME` - Creates a one-time reminder (DATE TIME format: yyyy-MM-dd HH:mm)
* `rem MESSAGE @ DATE TIME -r INTERVAL` - Creates a recurring reminder (INTERVAL format: number + unit [m/h/d/w])
* `rem ls` - Lists all reminders
* `rem rm INDEX[,INDEX...]` - Deletes reminders
* `rem snooze INDEX INTERVAL` - Snoozes a one-time reminder by the interval duration (INTERVAL format: number + unit [m/h/d/w])
* `rem on INDEX[,INDEX...]` - Turns reminders on
* `rem off INDEX[,INDEX...]` - Turns reminders off

**Timer Operations:**
* `start [INDEX|NAME] [@MINUTES]` - Starts a timer with optional task index/label and duration (default: 25 minutes)
* `pause` - Pauses the active timer
* `resume` - Resumes the paused timer
* `reset` - Resets and stops the timer
* `stat` - Shows timer statistics

**Habit Tracking:**
* `habit DESCRIPTION -t INTERVAL` - Creates a new habit with specified interval (INTERVAL format: number + unit [m/h/d/w])
* `habit ls` - Lists all habits
* `habit streak INDEX` - Increments the streak for a habit (validates timing with grace period)
* `habit rm INDEX` - Deletes a habit

**Index Formats:**
* Single: `1`
* Multiple: `1,2,3`
* Range: `1...5`
* Combined: `1,3...5,7`

**Special Parsing Features:**

1. **Case Insensitivity**: All command words are case-insensitive (e.g., `TODO`, `todo`, `ToDo` are all valid)

2. **Whitespace Normalisation**: Multiple consecutive spaces are collapsed into single spaces

3. **Index Range Expansion**: Range notation (e.g., `1...5`) is automatically expanded to individual indices

4. **Date-Time Parsing**: Uses Java's `LocalDate.parse()` and `LocalTime.parse()` for robust date/time validation

5. **Duration Interval Parsing**: Custom `parseInterval()` method supports human-readable formats like `30m`, `2h`, `1d`, `1w` for minutes, hours, days, and weeks respectively

### CommandHandler Component

**API**: `CommandHandler.java`

The CommandHandler component is responsible for executing parsed commands by coordinating between the Model components (TaskList, ReminderList, Timer) and the UI layer.

#### Structure of the CommandHandler Component

The CommandHandler component acts as the controller in the application architecture:

![Class Diagram of CommandHandler Component](images/CommandHandler.png)

* `CommandHandler` - Static class that executes commands and manages application state
* `Command` - Encapsulates the parsed command with its type and relevant parameters
* `CommandType` - Enumeration defining all supported command types
* `TaskList` - Model component managing the list of tasks
* `ReminderList` - Model component managing the list of reminders
* `HabitList` - Model component managing the list of habits
* `Timer` - Manages timer state and countdown functionality
* `TimerState` - Enumeration defining timer states (IDLE, RUNNING, PAUSED)
* `Task` - Abstract base class for all task types
* `Reminder` - Represents a reminder with schedule information
* `Habit` - Represents a habit with deadline, interval, and streak tracking
* `StreakResult` - Enumeration defining streak increment results (TOO_EARLY, ON_TIME, TOO_LATE)
* `MessageHandler` - UI component for displaying results to the user
* `IndexValidator` - Utility class for validating index inputs
* `StudyMateException` - Exception type thrown for command execution errors

#### CommandHandler Component Interactions

The diagram below shows how the CommandHandler component interacts with other components in the system:

![CommandHandler Interactions Diagram](images/CommandHandlerInteractions.png)

The CommandHandler receives Command objects from the Parser, coordinates with Model components (TaskList, ReminderList, HabitList, Timer), validates operations using IndexValidator, and communicates results through MessageHandler.

#### How the CommandHandler Component Works

The sequence diagram below illustrates the interactions within the CommandHandler component, taking `executeCommand(taskList, reminderList, habitList, todoCommand)` as an example where the command is for "todo read book":

![Sequence Diagram for Command Execution](images/CommandHandlerToDo.png)

**Example: Executing "todo read book" command**

When the CommandHandler component is called upon to execute a command, the following steps occur:

1. `StudyMate` calls `CommandHandler.executeCommand()` with the TaskList, ReminderList, HabitList, and parsed Command object (containing type=TODO, desc="read book").

2. CommandHandler uses a switch statement on `cmd.type` to determine which handler method to invoke. Since the command type is TODO, it routes to `handleToDo()`.

3. The `handleToDo()` method is invoked with the TaskList and Command object.

4. **For the todo command execution:**
   * `handleToDo()` calls `taskList.addToDo(cmd.desc)` to add the new task
   * TaskList creates a new ToDo task object and adds it to its internal list
   * `handleToDo()` calls `taskList.getCount()` to retrieve the total number of tasks
   * `handleToDo()` calls `taskList.getTask(listCount - 1)` to retrieve the newly added task
   * `handleToDo()` calls `MessageHandler.sendAddTaskMessage(newTask, listCount)` to display the confirmation message

5. Control returns to StudyMate through the call stack.

**General Command Execution Pattern:**

For all commands, the CommandHandler follows a similar pattern:

4. **For index-based operations:**
   * `IndexValidator.validateIndexes()` is called to ensure all indices are within valid ranges
   * If validation fails, a `StudyMateException` is thrown with a descriptive error message
   * If validation succeeds, the operation proceeds on the Model component

5. **For task operations:**
   * The handler interacts with `TaskList` to add, modify, or delete tasks
   * After the operation, the result is retrieved from TaskList
   * `MessageHandler` is called to display the result to the user

6. **For reminder operations:**
   * The handler interacts with `ReminderList` to add or remove reminders
   * For recurring reminders, a `Duration` interval is stored alongside the reminder
   * Results are displayed through `MessageHandler`

7. **For timer operations:**
   * The handler manages a static `activeTimer` instance
   * When starting a timer, a new `Timer` object is created and `startTimerMonitoring()` is invoked
   * `ScheduledExecutorService` is used to check timer state every second
   * When the timer completes, pauses, or is reset, appropriate messages are sent via `MessageHandler`
   * The scheduler is properly shut down when the timer ends or is reset

8. **Cleanup on exit:**
   * The `cleanup()` method is called when the application terminates
   * Active timers are reset and the scheduler is shut down to prevent resource leaks

#### Key Design Considerations

**Aspect: Static vs Instance Methods**

* **Current choice**: Use static methods and static state for CommandHandler
  * Pros: Simple access pattern; no need to pass CommandHandler instance around; centralised state management
  * Cons: Harder to test; global mutable state; potential concurrency issues; single active timer limitation

* **Alternative**: Use instance methods with dependency injection
  * Pros: Better testability; easier to mock; supports multiple independent instances
  * Cons: More complex initialisation; need to pass instance around; more boilerplate code

**Aspect: Command Routing**

* **Current choice**: Single large switch statement on CommandType
  * Pros: Simple to understand; centralised routing logic; easy to add new commands
  * Cons: Large method; potential for the class to become bloated as more commands are added

* **Alternative**: Command pattern with polymorphic dispatch
  * Pros: Better separation of concerns; each command encapsulates its own execution logic; follows OOP principles
  * Cons: More classes; more complex structure; harder to trace execution flow

#### Command Execution Flow

The CommandHandler executes commands in the following categories:

**Task Commands:**
1. **Add Commands** (`TODO`, `DEADLINE`, `EVENT`):
   - Add new task to TaskList
   - Retrieve the newly added task
   - Display confirmation message with task details and new list count

2. **View Commands** (`LIST`, `FIND`):
   - For `LIST`: Display all tasks or sorted view if `-s` flag is present
   - For `FIND`: Filter tasks by keyword and display results

3. **Edit Commands** (`EDIT_DESC`, `EDIT_DEADLINE`, `EDIT_FROM`, `EDIT_TO`):
   - Validate index
   - Call appropriate TaskList edit method based on command type
   - Display confirmation message

4. **Status Commands** (`MARK`, `UNMARK`):
   - Validate all indices
   - Update task completion status
   - Display confirmation message

5. **Delete Commands** (`DELETE`):
   - Validate all indices
   - Remove tasks from TaskList
   - Display confirmation with deleted tasks

**Reminder Commands:**
1. **Add Commands** (`REM_ADD_ONETIME`, `REM_ADD_REC`):
   - Add reminder to ReminderList with date/time
   - For recurring reminders, include interval duration
   - Display confirmation with reminder details

2. **View Commands** (`REM_LS`):
   - Display all reminders with their schedules

3. **Delete Commands** (`REM_RM`):
   - Validate all indices
   - Remove reminders from ReminderList
   - Display confirmation

4. **Snooze Commands** (`rem snooze INDEX INTERVAL`):
   - Snoozes a One-Time reminder by the interval duration
   - Turns the One-Time reminder back on, if the new remindAt timing valid (in the future)

5. **Turn On/Off Commands** (`rem on/off INDEX`)
   - Turns a One-Time/Recurring reminder On/Off

**Timer Commands:**
1. **Start Command** (`START`):
   - Check if another timer is active
   - Create new Timer with task index/label and duration
   - Start timer and initialize monitoring scheduler
   - Display start confirmation

2. **Control Commands** (`PAUSE`, `RESUME`, `RESET`):
   - Validate timer exists and is in correct state
   - Update timer state
   - For `RESET`: shutdown scheduler and clear active timer
   - Display confirmation with current timer status

3. **Status Command** (`STAT`):
   - Validate timer exists
   - Display timer statistics

**Habit Commands:**
1. **Add Command** (`HABIT_ADD`):
   - Add new habit to HabitList with name and interval
   - Initial streak is set to 1
   - Deadline is calculated as current time + interval
   - Display confirmation with habit details

2. **View Command** (`HABIT_LIST`):
   - Display all habits with their deadlines and current streaks

3. **Streak Command** (`HABIT_STREAK`):
   - Validate habit index
   - Attempt to increment the habit's streak
   - Check timing constraints:
     * TOO_EARLY: Current time is before the deadline (truncated to minute)
     * ON_TIME: Current time is within valid window (after deadline, within grace period of interval/4 + 1 minute)
     * TOO_LATE: Current time is beyond grace period (streak resets to 1)
   - Update habit deadline to current time + interval
   - Display result message with streak status

4. **Delete Command** (`HABIT_DELETE`):
   - Validate habit index
   - Remove habit from HabitList
   - Display confirmation

### Habits Component

**API**: `Habit.java`, `HabitList.java`

The Habits component is responsible for tracking recurring habits with deadlines, intervals, and streak counting. It enables users to build consistency by incrementing streaks when they complete habits within valid time windows.

#### Structure of the Habits Component

The Habits component consists of the following key classes:

![Class Diagram of Habit](images/Habit.png)

* `HabitList` - Manages the collection of habits and provides operations for adding, deleting, and incrementing streaks
* `Habit` - Represents a single habit with a name, deadline, interval, streak count, and clock for time operations
* `StreakResult` - Enumeration defining the result of a streak increment attempt (TOO_EARLY, ON_TIME, TOO_LATE)
* `DateTimeArg` - Encapsulates date and time for habit deadlines
* `Clock` - Provides time operations for testing and production use
* `MessageHandler` - UI component for displaying habit-related messages
* `StudyMateException` - Exception type thrown for habit operation errors

#### Habits Component Interactions

The diagram below shows how the Habits component interacts with other components in the system:

![Habits Component Interactions](images/HabitInteractions.png)

The HabitList manages Habit objects and coordinates with MessageHandler for user notifications. The Habit class uses Clock for time operations and DateTimeArg for deadline management, returning StreakResult to indicate the outcome of streak increment attempts.

#### HabitList Component Interactions

The diagram below shows the detailed interactions of the HabitList component:

![HabitList Interactions Diagram](images/HabitListInteractions.png)

#### How the Habits Component Works

The sequence diagram below illustrates the interactions within the Habits component when incrementing a habit's streak:

![Sequence Diagram for Habit Streak Increment](images/HabitStreakSequence.png)

**Example: Incrementing a habit's streak**

When a user attempts to increment a habit's streak, the following steps occur:

1. `CommandHandler` calls `HabitList.incStreak(index)` with the habit index.

2. `HabitList` retrieves the `Habit` at the specified index.

3. `HabitList` calls `habit.incStreak()` to attempt the increment.

4. **Within the Habit's incStreak() method:**
   * Gets the current time from the injected `Clock`
   * Compares current time against the deadline (truncated to minutes)
   * **If TOO_EARLY**: Current time is before the deadline
     - Returns `StreakResult.TOO_EARLY` without modifying the habit
   * **If TOO_LATE**: Current time exceeds deadline + grace period (interval/4 + 1 minute)
     - Resets streak to 1
     - Calculates new deadline as current time + interval
     - Updates the habit's deadline
     - Returns `StreakResult.TOO_LATE`
   * **If ON_TIME**: Current time is within the valid window
     - Increments streak by 1
     - Calculates new deadline as current time + interval
     - Updates the habit's deadline
     - Returns `StreakResult.ON_TIME`

5. The `StreakResult` is returned to `HabitList`.

6. `HabitList` calls `MessageHandler.sendIncStreakMessage(habit, result)` to display the appropriate message.

7. Control returns to `CommandHandler`.

#### Key Design Considerations

**Aspect: Time Precision**

* **Current choice**: Truncate deadline comparison to minutes (ignore seconds)
  * Pros: User-friendly; allows completion at any second within the same minute; reduces false negatives
  * Cons: Less precise; possible to complete slightly before/after and still count

* **Alternative**: Compare exact timestamps including seconds
  * Pros: Maximum precision; deterministic behaviour
  * Cons: Too strict for user experience; seconds-level precision rarely matters for habits

**Aspect: Clock Injection**

* **Current choice**: Inject `Clock` dependency into Habit and HabitList constructors
  * Pros: Testable with fixed clocks; supports deterministic testing; follows dependency injection principles
  * Cons: Additional parameter in constructors; slightly more complex initialization

* **Alternative**: Use `LocalDateTime.now()` directly
  * Pros: Simpler code; fewer parameters; standard Java approach
  * Cons: Untestable; time-dependent tests become flaky; cannot simulate different times

#### Habit Operations Flow

**Adding a Habit:**
1. User provides habit name and interval (e.g., "habit Exercise -t 1d")
2. Parser creates `HABIT_ADD` command with name and interval
3. CommandHandler calls `HabitList.addHabit(name, interval)`
4. HabitList creates new Habit with:
   - Initial streak = 1
   - Deadline = current time + interval
   - Injected clock for time operations
5. Habit is added to internal list
6. MessageHandler displays confirmation with habit details

**Listing Habits:**
1. User types "habit ls"
2. Parser creates `HABIT_LIST` command
3. CommandHandler calls `MessageHandler.sendHabitList(habitList)`
4. MessageHandler iterates through all habits and displays:
   - Index number (1-based)
   - Habit name
   - Current deadline
   - Current streak count

**Incrementing a Streak:**
1. User provides habit index (e.g., "habit streak 1")
2. Parser creates `HABIT_STREAK` command with index
3. CommandHandler validates index with IndexValidator
4. CommandHandler calls `HabitList.incStreak(index)`
5. HabitList retrieves habit and calls `habit.incStreak()`
6. Habit evaluates timing:
   - Gets current time from clock
   - Compares against deadline with grace period
   - Updates streak and deadline if valid
   - Returns StreakResult (TOO_EARLY, ON_TIME, or TOO_LATE)
7. MessageHandler displays result with updated habit information

**Deleting a Habit:**
1. User provides habit index (e.g., "habit rm 1")
2. Parser creates `HABIT_DELETE` command with index
3. CommandHandler validates index with IndexValidator
4. CommandHandler calls `HabitList.deleteHabit(index)`
5. HabitList removes habit from internal list
6. MessageHandler displays confirmation

#### Grace Period Calculation

The grace period is a critical feature that makes habit tracking user-friendly. Here's how it works:

```
Valid Window = [deadline (truncated to minute), deadline + (interval / 4) + 1 minute]
```

**Examples:**

For a daily habit (interval = 24 hours):
- Deadline: 2025-10-26 08:00
- Grace period: 24h / 4 = 6 hours
- Valid window: 2025-10-26 08:00 to 2025-10-26 14:01

For a weekly habit (interval = 7 days):
- Deadline: 2025-10-26 08:00  
- Grace period: 168h / 4 = 42 hours (1.75 days)
- Valid window: 2025-10-26 08:00 to 2025-10-27 02:01

For an hourly habit (interval = 1 hour):
- Deadline: 2025-10-26 08:00
- Grace period: 1h / 4 = 15 minutes
- Valid window: 2025-10-26 08:00 to 2025-10-26 08:16

This design ensures that habits with longer intervals have proportionally longer grace periods, maintaining fairness across different habit types.

#### Integration with Storage

Habits are persisted to the data file with the following format:

```
H<DELIM>name<DELIM>deadline<DELIM>interval<DELIM>streak
```

Where:
- `H` indicates a habit entry
- `name` is the habit description
- `deadline` is in yyyy-MM-ddTHH:mm format
- `interval` is a Duration string (e.g., PT24H for 24 hours)
- `streak` is the current streak count

When loading from file:
1. Storage parses the habit line and extracts all fields
2. Storage calls `HabitList.addHabit(name, deadline, interval, streak)`
3. HabitList creates Habit with existing data (no message displayed)
4. System clock is injected into the loaded habit

This ensures habits persist across application sessions with their streaks and deadlines intact.

{Describe the design and implementation of the product. Use UML diagrams and short code snippets where applicable.}

## Product scope
### Target user profile

NUS students who rely on laptops to manage lectures, labs, CCAs, and project deadlines, prefer typing to clicking, 
and need a quick, distraction-free way to record tasks, run short focus timers, set reminders, and track simple 
habits offline.

### Value proposition

A keyboard-first planner: add tasks, start focus timers, set reminders, and log habits in seconds from the command line. 
Built for fast typing and offline use, it displays clear summaries of tasks, due dates, time spent, and habit streaks 
to help students manage workload during busy weeks and exams.

## User Stories

| Version | As a ...                          | I want to ...                                                                         | So that I can ...                                               |
|---------|-----------------------------------|---------------------------------------------------------------------------------------|-----------------------------------------------------------------|
| v1.0    | student                           | add a simple to-do task with a description                                            | track basic tasks without deadlines                             |
| v1.0    | student                           | add a deadline task with a description and due date                                   | manage time-sensitive assignments                               |
| v1.0    | student                           | list all my tasks                                                                     | view my current workload at a glance                            |
| v1.0    | student                           | mark a task as done                                                                   | indicate completion and track progress                          |
| v1.0    | student                           | unmark a task as not done                                                             | correct mistakes or reopen tasks                                |
| v1.0    | student                           | delete a task                                                                         | remove tasks I no longer need                                   |
| v1.0    | student                           | find tasks by keyword                                                                 | quickly locate specific tasks in a long list                    |
| v1.0    | student                           | receive error messages for invalid task inputs                                        | avoid mistakes and understand what went wrong                   |
| v1.0    | student                           | have my tasks automatically saved and loaded                                          | persist data across sessions                                    |
| v1.0    | student                           | use case-insensitive commands                                                         | type commands comfortably without worrying about capitalization |
| v1.0    | student with poor time management | start a timer with a custom duration (in minutes)                                     | focus on tasks for a set period                                 |
| v1.0    | student with poor time management | start a timer linked to a specific task (by index or name)                            | associate focus time with particular assignments                |
| v1.0    | student with poor time management | pause and resume the timer                                                            | handle interruptions without losing progress                    |
| v1.0    | student with poor time management | reset the timer                                                                       | stop and restart if needed                                      |
| v1.0    | student with poor time management | view timer statistics                                                                 | track my productivity and time spent                            |
| v2.0    | user                              | see usage instructions                                                                | refer to them when I forget how to use the application          |
| v2.0    | student                           | edit a task's description                                                             | update task details without recreating it                       |
| v2.0    | student                           | edit a deadline task's due date                                                       | adjust deadlines as plans change                                |
| v2.0    | student                           | perform batch operations on tasks (e.g., mark multiple by indices)                    | efficiently manage multiple tasks at once                       |
| v2.0    | busy student                      | add one-time reminders with a message, date, and time                                 | set alerts for important events like meetings                   |
| v2.0    | busy student                      | add recurring reminders with a message, date/time, and interval (e.g., daily, weekly) | automate reminders for routine tasks like study sessions        |
| v2.0    | busy student                      | list all my reminders                                                                 | review upcoming notifications                                   |
| v2.0    | busy student                      | delete reminders by index                                                             | remove outdated or unnecessary alerts                           |
| v2.0    | busy student                      | perform batch operations on reminders (e.g., delete multiple by indices)              | efficiently manage multiple reminders at once                   |
| v2.0    | busy student                      | receive error messages for invalid reminder inputs                                    | avoid mistakes and understand what went wrong                   |
| v2.0    | student                           | use flexible index formats (e.g., ranges like 1...3)                                  | perform operations on groups of tasks or reminders easily       |
| v2.0    | student                           | handle whitespace normalisation in commands                                           | type commands without worrying about extra spaces               |
| v2.0    | student                           | manage overlapping or invalid index ranges                                            | avoid errors in batch operations                                |
| v2.0    | student                           | use date-time parsing for deadlines and reminders                                     | input dates and times reliably                                  |

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
