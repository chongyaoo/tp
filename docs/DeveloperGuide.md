# Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

### Parser Component

**API**: `Parser.java`

The Parser component is responsible for interpreting user input and converting it into executable commands that the application can process.

#### Structure of the Parser Component

The Parser component consists of several key classes that work together to handle command parsing and validation:

![Class Diagram of Parser Component](images/ParserClassDiagram.png)

* `Parser` - The main parser class that interprets raw user input and creates `Command` objects
* `Command` - Encapsulates the parsed command with its type and relevant parameters
* `CommandType` - An enumeration that defines all supported command types in the application
* `CommandHandler` - Executes the parsed commands by coordinating with the Model (TaskList, ReminderList) and UI components
* `IndexValidator` - Validates index inputs to ensure they are within valid ranges
* `DateTimeArg` - Encapsulates date/time arguments for deadline, event, and reminder commands with both date and optional time components

The Parser component works with the following workflow:

1. **User Input Processing**: Raw string input from the user is received
2. **Command Parsing**: The `Parser` analyzes the input and identifies the command type
3. **Parameter Extraction**: Relevant parameters (descriptions, indices, dates, times, etc.) are extracted and validated
4. **Command Object Creation**: A `Command` object is instantiated with the appropriate `CommandType` and parameters
5. **Command Execution**: The `CommandHandler` receives the `Command` object and executes the corresponding logic

#### How the Parser Component Works

The sequence diagram below illustrates the interactions within the Parser component, taking `parse("deadline Submit report /by 2025-10-25")` as an example:

![Sequence Diagram for Parsing Deadline Command](images/ParserSequenceDiagram.png)

**Note**: The lifeline for `Parser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

When the Parser component is called upon to parse a command, the following steps occur:

1. The `StudyMate` main class receives user input and passes it to the `Parser` object's `parse()` method.

2. The `Parser` analyzes the input string to identify the command word (e.g., "deadline", "todo", "mark", "start").

3. Based on the identified command type, the `Parser` invokes the appropriate private parsing method:
   * `parseToDo()` - for todo commands
   * `parseDeadline()` - for deadline commands
   * `parseEvent()` - for event commands
   * `parseFind()` - for find commands
   * `parseList()` - for list commands (with optional sorting flag)
   * `parseEdit()` - for edit commands
   * `parseMark()`, `parseUnmark()`, `parseDelete()` - for index-based operations
   * `parseRem()` - for reminder-related commands
   * `parseTimerStart()` - for timer start commands

4. Each parsing method:
   * Validates the command syntax and parameters
   * Extracts relevant data (descriptions, dates, times, indices, durations)
   * Throws `StudyMateException` if the input format is invalid
   * Creates and returns a `Command` object with the appropriate `CommandType` and parameters

5. The resulting `Command` object is returned to `StudyMate`, which passes it to `CommandHandler.executeCommand()`.

6. The `CommandHandler` uses a switch statement on the `CommandType` to route the command to the appropriate handler method:
   * Task commands are handled by methods like `handleToDo()`, `handleDeadline()`, `handleEvent()`, `handleMark()`, `handleEdit()`
   * Reminder commands are handled by methods like `handleRemAddOneTime()`, `handleRemAddRec()`, `handleRemList()`
   * Timer commands are handled by methods like `handleTimerStart()`, `handleTimerPause()`

7. Handler methods interact with the Model components (TaskList, ReminderList, Timer) to execute the command logic.

8. The `IndexValidator` is used when commands involve list indices to ensure they are within valid ranges before execution.

9. Results are communicated back to the user through the `MessageHandler` UI component.

#### Key Design Considerations

**Aspect: Command representation**

* **Current choice**: Use a single `Command` class with multiple constructors to handle different command types
  * Pros: Simpler to pass around as a single object type; easier serialization; centralized command representation
  * Cons: The Command class has many fields, most of which are null for any given command; potential for confusion about which fields are relevant for each command type

* **Alternative**: Create separate command classes for each command type (e.g., `DeadlineCommand`, `MarkCommand`, `EditCommand`)
  * Pros: Each class only contains relevant fields; clearer separation of concerns; type safety
  * Cons: More classes to maintain; requires more complex type handling in CommandHandler; polymorphism adds complexity

**Aspect: Parsing validation**

* **Current choice**: Perform validation during parsing and throw exceptions immediately
  * Pros: Fail-fast approach; errors caught before command execution; clearer error messages tied to specific parsing failures
  * Cons: Parsing logic is tightly coupled with validation logic

* **Alternative**: Parse optimistically and validate during execution
  * Pros: Cleaner separation between parsing and validation; parser focuses solely on structure
  * Cons: Invalid commands might not be caught until execution time; harder to provide specific error messages; wasted resources creating Command objects for invalid input

**Aspect: Index handling**

* **Current choice**: Support multiple index formats (single, comma-separated, ranges with "...")
  * Pros: User-friendly; allows batch operations; flexible input; reduces repetitive commands
  * Cons: More complex parsing logic; higher chance of parsing errors; need to handle edge cases (overlapping ranges, invalid ranges)

* **Alternative**: Support only single indices
  * Pros: Simpler parsing; fewer edge cases; easier to debug
  * Cons: Less convenient for users; requires multiple commands for batch operations; reduced productivity

**Aspect: Time handling in reminders**

* **Current choice**: Require time component for all reminders (format: `yyyy-MM-dd HH:mm`)
  * Pros: Precise scheduling; consistent behavior with recurring reminders; no ambiguity about when reminders fire
  * Cons: Slightly more verbose input; users must specify time even for all-day reminders

* **Alternative**: Make time optional and default to midnight or current time
  * Pros: More convenient for date-only reminders; less typing
  * Cons: Ambiguity about default time; inconsistent reminder behavior; harder to reason about recurring reminder schedules

#### Supported Command Formats

The Parser component supports the following command formats:

**Task Management:**
* `todo DESCRIPTION` - Creates a todo task
* `deadline DESCRIPTION /by DATE` - Creates a deadline task (DATE format: yyyy-MM-dd)
* `event DESCRIPTION /from DATE /to DATE` - Creates an event task (DATE format: yyyy-MM-dd)
* `list` - Lists all tasks
* `list -s` - Lists all deadlines and events sorted by date (soonest first)
* `find KEYWORD` - Finds tasks containing the keyword
* `edit INDEX -n DESCRIPTION` - Edits task description
* `edit INDEX -d DATE` - Edits deadline date (for deadline tasks only)
* `edit INDEX -f DATE` - Edits event from date (for event tasks only)
* `edit INDEX -t DATE` - Edits event to date (for event tasks only)
* `mark INDEX[,INDEX...]` - Marks tasks as done
* `unmark INDEX[,INDEX...]` - Marks tasks as not done
* `delete INDEX[,INDEX...]` - Deletes tasks

**Reminder Management:**
* `rem MESSAGE @ DATE TIME` - Creates a one-time reminder (DATE TIME format: yyyy-MM-dd HH:mm)
* `rem MESSAGE @ DATE TIME -r INTERVAL` - Creates a recurring reminder (INTERVAL format: number + unit [s/m/h/d/w])
* `rem ls` - Lists all reminders
* `rem rm INDEX[,INDEX...]` - Deletes reminders

**Timer Operations:**
* `start [INDEX|NAME] [@MINUTES]` - Starts a timer with optional task index/label and duration
* `pause` - Pauses the active timer
* `resume` - Resumes the paused timer
* `reset` - Resets and stops the timer
* `stat` - Shows timer statistics

**Index Formats:**
* Single: `1`
* Multiple: `1,2,3`
* Range: `1...5`
* Combined: `1,3...5,7`

**Special Parsing Features:**

1. **Case Insensitivity**: All command words are case-insensitive (e.g., `TODO`, `todo`, `ToDo` are all valid)

2. **Whitespace Normalization**: Multiple consecutive spaces are collapsed into single spaces

3. **Index Range Expansion**: Range notation (e.g., `1...5`) is automatically expanded to individual indices

4. **Date-Time Parsing**: Uses Java's `LocalDate.parse()` and `LocalTime.parse()` for robust date/time validation

5. **Duration Interval Parsing**: Custom `parseInterval()` method supports human-readable formats like `1d`, `2w`, `30m`

### CommandHandler Component

**API**: `CommandHandler.java`

The CommandHandler component is responsible for executing parsed commands by coordinating between the Model components (TaskList, ReminderList, Timer) and the UI layer.

#### Structure of the CommandHandler Component

The CommandHandler component acts as the controller in the application architecture:

![Class Diagram of CommandHandler Component](images/CommandHandlerClassDiagram.png)

* `CommandHandler` - Static class that executes commands and manages application state
* `Timer` - Manages timer state and countdown functionality
* `ScheduledExecutorService` - Java concurrent utility for scheduling timer monitoring
* `MessageHandler` - UI component for displaying results to the user

#### How the CommandHandler Component Works

The sequence diagram below illustrates the interactions within the CommandHandler component, taking `executeCommand(taskList, reminderList, markCommand)` as an example:

![Sequence Diagram for Command Execution](images/CommandHandlerSequenceDiagram.png)

When the CommandHandler component is called upon to execute a command, the following steps occur:

1. `StudyMate` calls `CommandHandler.executeCommand()` with the TaskList, ReminderList, and parsed Command object.

2. CommandHandler uses a switch statement on `cmd.type` to determine which handler method to invoke.

3. The appropriate handler method is called (e.g., `handleMark()` for MARK commands, `handleRemAddRec()` for recurring reminders).

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
  * Pros: Simple access pattern; no need to pass CommandHandler instance around; centralized state management
  * Cons: Harder to test; global mutable state; potential concurrency issues; single active timer limitation

* **Alternative**: Use instance methods with dependency injection
  * Pros: Better testability; easier to mock; supports multiple independent instances
  * Cons: More complex initialization; need to pass instance around; more boilerplate code

**Aspect: Timer Monitoring Mechanism**

* **Current choice**: Use `ScheduledExecutorService` with 1-second polling interval
  * Pros: Simple to implement; reliable; built-in Java concurrency support; automatic thread management
  * Cons: Polling overhead; 1-second granularity; scheduler must be properly cleaned up

* **Alternative**: Use a separate daemon thread with sleep/wait
  * Pros: More control over thread behavior; can adjust sleep intervals dynamically
  * Cons: Manual thread management; more complex; harder to ensure proper cleanup

**Aspect: Command Routing**

* **Current choice**: Single large switch statement on CommandType
  * Pros: Simple to understand; centralized routing logic; easy to add new commands
  * Cons: Large method; potential for the class to become bloated as more commands are added

* **Alternative**: Command pattern with polymorphic dispatch
  * Pros: Better separation of concerns; each command encapsulates its own execution logic; follows OOP principles
  * Cons: More classes; more complex structure; harder to trace execution flow

**Aspect: Error Handling**

* **Current choice**: Throw `StudyMateException` for validation errors and state violations
  * Pros: Centralized error handling; exceptions propagate up to main loop; consistent error reporting
  * Cons: Exceptions can be expensive; control flow through exceptions is sometimes considered poor practice

* **Alternative**: Return Result objects with success/failure status
  * Pros: Explicit error handling; no exception overhead; clearer API contracts
  * Cons: Callers must check return values; more verbose; easy to ignore errors

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

{Describe the design and implementation of the product. Use UML diagrams and short code snippets where applicable.}


## Product scope
### Target user profile

{Describe the target user profile}

### Value proposition

{Describe the value proposition: what problem does it solve?}

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
