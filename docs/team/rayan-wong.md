# Rayan Wong - Project Portfolio Page

## Overview
NUS students who rely on laptops to manage lectures, labs, CCAs, and project deadlines, prefer typing to clicking, and need a quick, distraction-free way to record tasks, run short focus timers, set reminders, and track simple habits offline. A keyboard-first planner: add tasks, start focus timers, set reminders, and log habits in seconds from the command line, built for fast typing and offline use, it displays clear summaries of tasks, due dates, time spent, and habit streaks to help students manage workload during busy weeks and exams.

### Summary of Contributions

#### Code contributed
[RepoSense link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=&sort=totalCommits%20dsc&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-09-19T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Rayan-Wong&tabRepo=AY2526S1-CS2113-W12-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

#### Enhancements implemented
* **Parser Component**: Implemented the core parsing system that interprets user input and converts it into executable commands. This includes:
  - Support for multiple command formats (todo, deadline, event, list, find, edit, mark, unmark, delete, reminders, timer operations, habits)
  - Flexible index parsing (single, comma-separated, ranges with "...")
  - Date-time parsing with validation
  - Duration interval parsing for reminders and habits
  - Whitespace normalisation and case-insensitive command handling
  - Comprehensive error handling with descriptive error messages
  - Habit command parsing (add, list, streak increment, delete) with interval validation

* **CommandHandler Component**: Implemented the command execution system that coordinates between Model components and the UI. This includes:
  - Routing commands to appropriate handler methods
  - Task management operations (add, edit, mark, delete)
  - Reminder management operations (add one-time/recurring, list, delete, snooze, on/off)
  - Timer lifecycle management (start, pause, resume, reset, statistics)
  - Habit management operations (add, list, increment streak with timing validation, delete)
  - Index validation for batch operations
  - Integration with ScheduledExecutorService for timer monitoring
  - Proper cleanup and resource management

* **Habits Feature**: Implemented complete habit tracking system for building consistency:
  - `Habit` class with streak counting, deadline management, and grace period validation
  - `HabitList` class for managing collections of habits
  - Timing validation logic (TOO_EARLY, ON_TIME, TOO_LATE) with configurable grace period (interval/4 + 1 minute)
  - Automatic deadline updates upon successful streak increments
  - Clock dependency injection for testable time operations
  - Storage integration for persistence across sessions
  - Comprehensive unit tests for timing edge cases

#### Contributions to the User Guide
* Documented the **todo** command with format, examples, and expected output
* Documented the **deadline** command with format, examples, and usage notes (including required time component)
* Documented the **event** command with format, examples, and validation rules (including required time components)
* Documented the **habit tracking** feature with comprehensive coverage:
  - Adding habits with interval specification
  - Listing all habits with deadlines and streaks
  - Incrementing streaks with detailed timing rules (TOO_EARLY, ON_TIME, TOO_LATE)
  - Grace period calculation and examples for different intervals
  - Deleting habits with warnings about data loss
  - Added habit-specific FAQs (grace period, timing, interval modification)
* Enhanced the Command Summary with complete command reference organised by category (Tasks, Habits, Reminders, Timer, Other)

#### Contributions to the Developer Guide
* **Parser Component Section**: Comprehensive documentation including:
  - Component structure with class diagrams
  - Component interactions with other system parts
  - Detailed sequence diagram for command parsing workflow
  - Key design considerations and trade-offs
  - Complete list of supported command formats (including habit commands)

* **CommandHandler Component Section**: Comprehensive documentation including:
  - Component structure with class diagrams
  - Component interactions diagram
  - Detailed sequence diagram for command execution workflow
  - Key design considerations and trade-offs

* **Habits Component Section**: Comprehensive documentation including:
  - Class diagram with Habit and HabitList class diagrams
  - Habit interaction diagram showing relationships with MessageHandler, Clock, DateTimeArg
  - Detailed sequence diagram for habit streak increment with all three timing scenarios
  - Key design considerations and trade-offs
  - Grace period calculation with concrete examples for different intervals
  - Habit operations flow (add, list, increment streak, delete)
  - Integration with Storage component

#### Contributions to team-based tasks
* **CI/CD Pipeline**: Set up GitHub Actions workflows to automatically notify the team via Telegram when:
    - New pull requests are opened 
    - New commits are pushed to the main branch

* **Code Coverage Enhancement**: Added JaCoCo test coverage reporting with:
    - Total project code coverage metrics
    - Per-class code coverage breakdown

* **Clock Dependency Injection for Testing**: Implemented dependency injection pattern for Clock instances to eliminate flaky time-dependent tests:
    - Modified `Habit` and `HabitList` to accept Clock parameters in constructors
    - Modified `Reminder`, `OneTimeSchedule`, and `RecurringSchedule` to accept Clock parameters
    - Modified `ReminderList` to inject Clock into all reminders
    - Enabled deterministic testing with fixed clocks in test suites
    - Minimised race conditions and timing-dependent test failures
    - Maintained backwards compatibility with default system clock for production use
    - Allowed simulation of different time scenarios for edge case testing

* **Integration Testing**: Implemented comprehensive integration tests for the Scheduler component:
    - Ensured proper cleanup and shutdown behaviour of scheduled tasks
    - Tested concurrent operations and thread safety of the scheduler

* **End-to-End Testing Enhancement**: Significantly improved the end-to-end test suite:
    - Modified test scripts (`runtest.bat` and `runtest.sh`) to support multi-stage testing with jar reloading
    - Added storage persistence validation by:
      * First run: Add tasks, deadlines, events, one-time reminders, recurring reminders, and habits
      * Second run: Reload jar and verify all data persists correctly using `list`, `rem ls`, and `habit ls` commands
    - Created `input2.txt` and `EXPECTED2.TXT` for the second test stage
    - Implemented environment variable-based fixed clock (`FIXED_CLOCK_TIME`) for deterministic date/time testing
    - Validated complete user workflows from command input to output across application restarts

#### Review/mentoring contributions
Reviewed and provided feedback for 17 pull requests:
* [#51](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/51)
* [#49](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/49)
* [#44](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/44)
* [#43](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/43)
* [#42](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/42)
* [#40](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/40)
* [#39](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/39)
* [#38](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/38)
* [#37](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/37)
* [#29](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/29)
* [#28](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/28)
* [#26](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/26)
* [#24](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/24)
* [#21](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/21)
* [#20](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/20)
* [#14](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/14)
* [#4](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/4)
