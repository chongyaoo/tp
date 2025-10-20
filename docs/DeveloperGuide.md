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
* `DateTimeArg` - Encapsulates date/time arguments for deadline and reminder commands

The Parser component works with the following workflow:

1. **User Input Processing**: Raw string input from the user is received
2. **Command Parsing**: The `Parser` analyzes the input and identifies the command type
3. **Parameter Extraction**: Relevant parameters (descriptions, indices, dates, etc.) are extracted and validated
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
   * `parseMark()`, `parseUnmark()`, `parseDelete()` - for index-based operations
   * `parseRem()` - for reminder-related commands
   * `parseTimerStart()` - for timer start commands

4. Each parsing method:
   * Validates the command syntax and parameters
   * Extracts relevant data (descriptions, dates, indices, durations)
   * Throws `StudyMateException` if the input format is invalid
   * Creates and returns a `Command` object with the appropriate `CommandType` and parameters

5. The resulting `Command` object is returned to `StudyMate`, which passes it to `CommandHandler.executeCommand()`.

6. The `CommandHandler` uses a switch statement on the `CommandType` to route the command to the appropriate handler method:
   * Task commands are handled by methods like `handleToDo()`, `handleDeadline()`, `handleMark()`
   * Reminder commands are handled by methods like `handleRemAddOneTime()`, `handleRemList()`
   * Timer commands are handled by methods like `handleTimerStart()`, `handleTimerPause()`

7. Handler methods interact with the Model components (TaskList, ReminderList, Timer) to execute the command logic.

8. The `IndexValidator` is used when commands involve list indices to ensure they are within valid ranges before execution.

9. Results are communicated back to the user through the `MessageHandler` UI component.

#### Key Design Considerations

**Aspect: Command representation**

* **Current choice**: Use a single `Command` class with multiple constructors to handle different command types
  * Pros: Simpler to pass around as a single object type; easier serialization
  * Cons: The Command class has many fields, most of which are null for any given command

* **Alternative**: Create separate command classes for each command type (e.g., `DeadlineCommand`, `MarkCommand`)
  * Pros: Each class only contains relevant fields; clearer separation of concerns
  * Cons: More classes to maintain; requires more complex type handling in CommandHandler

**Aspect: Parsing validation**

* **Current choice**: Perform validation during parsing and throw exceptions immediately
  * Pros: Fail-fast approach; errors caught before command execution; clearer error messages
  * Cons: Parsing logic is tightly coupled with validation logic

* **Alternative**: Parse optimistically and validate during execution
  * Pros: Cleaner separation between parsing and validation
  * Cons: Invalid commands might not be caught until execution time; harder to provide specific error messages

**Aspect: Index handling**

* **Current choice**: Support multiple index formats (single, comma-separated, ranges with "...")
  * Pros: User-friendly; allows batch operations; flexible input
  * Cons: More complex parsing logic; higher chance of parsing errors

* **Alternative**: Support only single indices
  * Pros: Simpler parsing; fewer edge cases
  * Cons: Less convenient for users; requires multiple commands for batch operations

#### Supported Command Formats

The Parser component supports the following command formats:

**Task Management:**
* `todo DESCRIPTION` - Creates a todo task
* `deadline DESCRIPTION /by DATE` - Creates a deadline task (DATE format: yyyy-MM-dd)
* `list` - Lists all tasks
* `mark INDEX[,INDEX...]` - Marks tasks as done
* `unmark INDEX[,INDEX...]` - Marks tasks as not done
* `delete INDEX[,INDEX...]` - Deletes tasks

**Reminder Management:**
* `rem MESSAGE @ DATE` - Creates a one-time reminder
* `rem MESSAGE @ DATE -r INTERVAL` - Creates a recurring reminder (INTERVAL format: number + unit [s/m/h/d/w])
* `rem ls` - Lists all reminders
* `rem rm INDEX[,INDEX...]` - Deletes reminders

**Timer Operations:**
* `start [INDEX|NAME] [@MINUTES]` - Starts a timer
* `pause` - Pauses the active timer
* `resume` - Resumes the paused timer
* `reset` - Resets and stops the timer
* `stat` - Shows timer statistics

**Index Formats:**
* Single: `1`
* Multiple: `1,2,3`
* Range: `1...5`
* Combined: `1,3...5,7`

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
