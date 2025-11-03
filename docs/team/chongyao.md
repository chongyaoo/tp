# Ong Chong Yao - Project Portfolio Page

## Overview
NUS students who rely on laptops to manage lectures, labs, CCAs, and project deadlines, prefer typing to clicking, and need a quick, distraction-free way to record tasks, run short focus timers, set reminders, and track simple habits offline. A keyboard-first planner: add tasks, start focus timers, set reminders, and log habits in seconds from the command line, built for fast typing and offline use, it displays clear summaries of tasks, due dates, time spent, and habit streaks to help students manage workload during busy weeks and exams.

### Summary of Contributions

#### Code contributed
[RepoSense link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=chongyaoo&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-09-19T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=chongyaoo&tabRepo=AY2526S1-CS2113-W12-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

#### Enhancements implemented
* **Reminders Feature**: Implemented complete reminder system for time-based notifications:
    - `Reminder` class with support for both one-time and recurring schedules
    - `Schedule` interface with polymorphic behavior for different reminder types
    - `OneTimeSchedule` class for single-fire reminders with automatic turn-off after firing
    - `RecurringSchedule` class for repeating reminders with automatic rescheduling
    - `ReminderList` class for managing collections of reminders with thread-safe operations
    - `IndexedReminder` wrapper class for associating reminders with their list positions
    - Snooze functionality for one-time reminders with validation to prevent past-time snoozing
    - Turn on/off functionality to enable/disable reminders without deletion
    - Clock dependency injection for testable time operations
    - Comprehensive unit tests for timing edge cases and concurrent operations


* **Scheduler Component**: Implemented background reminder monitoring system:
    - `Scheduler` class using `ScheduledExecutorService` for periodic checks
    - Configurable check interval (default 30 seconds) for due reminder detection
    - `tick()` method that scans all reminders and identifies due ones
    - Automatic notification system that displays fired reminders to users
    - Thread-safe reminder checking with proper synchronization
    - Handling shutdowns to prevent resource leaks
    - Integration with `MessageHandler` for user notifications
    - Support for both immediate checks on startup and periodic background monitoring


* **Reminder Command Parsing**: Extended the Parser component to support reminder commands:
    - One-time reminder format: `rem MESSAGE @ YYYY-MM-DD HH:mm`
    - Recurring reminder format: `rem MESSAGE @ YYYY-MM-DD HH:mm -r INTERVAL`
    - List reminders: `rem ls`
    - Delete reminders: `rem rm INDEX[,INDEX...]`
    - Turn reminders on/off: `rem on/off INDEX[,INDEX...]`
    - Snooze reminders: `rem snooze INDEX INTERVAL`
    - Duration interval parsing: supports minutes (m), hours (h), days (d), and weeks (w)
    - Comprehensive error handling with descriptive messages for invalid formats
  

* **Reminder Command Execution**: Extended the CommandHandler component for reminder operations:
    - Add one-time and recurring reminders with date-time validation
    - List all reminders with status and schedule information
    - Delete single or multiple reminders using flexible index notation
    - Turn reminders on/off in batch operations
    - Snooze one-time reminders with future-time validation
    - Prevent snoozing of recurring reminders with appropriate error messages
    - Index validation for all batch operations
    - Proper integration with the Scheduler for automatic firing
  

#### Contributions to the User Guide
* Documented the complete **Reminders** section with comprehensive coverage:
    - Adding one-time reminders with format, examples, and expected output
    - Adding recurring reminders with interval specification and behavior
    - Listing reminders with status indicators 
    - Turning reminders on/off with batch operation support
    - Snoozing reminders with detailed rules and restrictions
    - Deleting reminders with confirmation messages
    - Reminder Behavior and Rules section explaining:
        * Background monitoring with 30-second check interval
        * Firing notifications and display format
        * One-time and recurring reminder lifecycles
        * Time precision and firing behavior
* Enhanced the Command Summary with reminder command reference organized by category


#### Contributions to the Developer Guide
* **Reminders Component Section**: Comprehensive documentation including:
    - Class diagram showing Reminder, Schedule, OneTimeSchedule, RecurringSchedule, and ReminderList relationships
    - Component interactions diagram with MessageHandler, Clock, DateTimeArg, and Storage
    - Detailed sequence diagram for One-Time and Recurring Reminder
    - Snooze operation sequence diagram with validation logic
    - Key design considerations and trade-offs (thread safety, grace period handling)
    - Integration with Storage component for data persistence

* **Scheduler Component Section**: Comprehensive documentation including:
    - Component structure with ScheduledExecutorService architecture
    - Sequence diagram for background monitoring and firing workflow
    - Thread safety considerations and synchronization patterns
    - Key design decisions (check interval selection, graceful shutdown, notification handling)
    - Integration points with ReminderList and MessageHandler

* **Task Component Diagrams**: Created UML diagrams for the Task component:
    - Task class diagram showing inheritance hierarchy (Task, ToDo, Deadline, Event)
    - Task interactions diagram illustrating relationships with CommandHandler, Parser, Storage, and MessageHandler
    - TaskList interactions diagram showing detailed method calls and data flow
    - Add deadline sequence diagram demonstrating complete workflow from user input to confirmation


#### Contributions to team-based tasks
* **Clock Dependency Injection for Reminders**: Implemented dependency injection pattern for Clock instances in reminder-related classes:
    - Modified `Scheduler` to work with testable clocks
    - Enabled deterministic testing with fixed clocks in test suites
    - Allowed simulation of different time scenarios for edge case testing (past reminders, future reminders, exact timing)

* **Integration Testing for Scheduler**: Implemented comprehensive integration tests:
    - Tested proper cleanup and shutdown behavior of ScheduledExecutorService
    - Verified notification delivery through MessageHandler integration

* **End-to-End Testing for Reminders**: Enhanced the end-to-end test suite:
    - Added one-time and recurring reminder creation
    - Validated reminder persistence across application restarts
    - Verified turn on/off functionality persists correctly
    - Ensured snooze operations work correctly with fixed clock testing
  
---

## Pull Requests
- [#7](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/7)
- [#21](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/21)
- [#28](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/28)
- [#37](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/37)
- [#39](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/39)
- [#43](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/43)
- [#49](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/49)
- [#50](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/50)
- [#69](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/69)
- [#86](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/86)
- [#159](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/159)


## Review/mentoring contributions
Reviewed and provided feedback for 7 pull requests:
- [#6](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/6)
- [#47](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/47)
- [#48](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/48)
- [#85](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/85)
- [#158](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/158)
- [#163](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/163)
- [#165](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/165)

