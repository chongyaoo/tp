# Ryan Koh - Project Portfolio Page

## Overview
NUS students who rely on laptops to manage lectures, labs, CCAs, and project deadlines, prefer typing to clicking, and need a quick, distraction-free way to record tasks, run short focus timers, set reminders, and track simple habits offline. A keyboard-first planner: add tasks, start focus timers, set reminders, and log habits in seconds from the command line, built for fast typing and offline use, it displays clear summaries of tasks, due dates, time spent, and habit streaks to help students manage workload during busy weeks and exams.

### Code Contributed: [RepoSense link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=&sort=totalCommits%20dsc&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-09-19T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=ry-koh&tabRepo=AY2526S1-CS2113-W12-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

---

## Summary of Contributions

---

### Task Component
- **Task.java (Abstract Class):**
    - Established the core abstraction for task entities with fields for description, completion status, and construction logic
    - Defined a clear, extensible API for marking, unmarking, displaying, and saving tasks
    - Ensured only concrete subclasses like ToDo and Deadline could be instantiated, enforcing best design patterns and supporting future extensibility
    - Provided `getDone()` for status checks, and abstract `toSaveString()` for consistent data serialization across all subtypes

- **ToDo.java:**
    - Implemented simple tasks with fast creation via the command line
    - Specialised overriding for display and file persistence, ensuring UI consistency and robust loading from storage

- **Deadline.java:**
    - Extended the base Task logic, adding date/time fields and flexible input parsing (with comprehensive error handling for invalid formats)
    - Designed string conversion methods for both user feedback and save/load reliability
    - Supported future deadline-specific features such as alerts and sorting

- **TaskList.java (Central Data Structure):**
    - Established the data model supporting a dynamic list of `Task` objects—including both `ToDo` and `Deadline` with add, delete, mark, unmark, and query operations
    - Implemented index validation for user actions, batch operations (multi-mark/unmark), and error checking to prevent invalid state mutations
    - Designed feedback and UI logic for all list commands, ensuring users always see accurate results and helpful messages for actions (including empty, full, and filtered lists)
    - Supported extensibility to future task types and features, making use of inheritance and dynamic casting for robust polymorphism
    - Maintained state integrity with rigorous validation and defensive programming patterns
    - Provided accessor and mutator methods (`getCount()`, `getTasks()`, etc.) for integration with UI and testing modules

- **Testing:**
  - Tested adding of both `ToDo` and `Deadline` tasks to ensure correct data assignment and type instantiation
  - Verified marking and unmarking (single and batch) of tasks, covering state toggling and edge case handling (such as marking already-completed tasks)
  - Covered deletion operations for single and multiple tasks, validating index management, list shifting, and state integrity after each removal
  - Confirmed the correctness of getters (`getCount()`, `getTask()`, `getTasks()`) for empty and populated lists
  - Extensively covered error scenarios, such as invalid indices and empty list operations, preventing silent failures
  - Maintained rigorous integration between the test suite and updated task core modules, refactoring and exposing necessary accessors

---

### Timer Component
- **Timer.java & TimerState.java (Asynchronous Timer Engine):**
    - Developed the Timer class from scratch, leveraging ScheduledExecutorService for asynchronous countdown functionality, allowing timers to run independently of the main command flow
    - Supported full timer lifecycle: start, pause, resume, reset, and status queries, each mapped to user commands (`timer start`, `timer pause`, etc.)
    - Implemented custom labels, durations, and formatted time display for intuitive user feedback
    - Engineered argument validation for labels and time intervals
    - Robust error handling to prevent invalid or conflicting timer states
    - Developed state management logic, tracking timer progress and guaranteeing correct transitions between all operation modes (e.g., paused, running, ended)
    - Created seamless integration with MessageHandler for real-time user notifications on timer events: start, end, pause, resume, and completion alerts
    - Supported concurrent timers and integration with other productivity features

- **Testing:**
  - Comprehensive validation of timer lifecycle commands: `start`, `pause`, `resume`, `reset`, and `status` queries
  - Tested correct state transitions, notification outputs, and message formatting for each timer event
  - Verified error handling for conflicting timers, invalid time inputs, and rapid state changes
  - Ensured correct and responsive user feedback for all timer actions, supporting asynchronous and concurrent scenarios

---

### UI Component
- **MessageHandler.java (Display and Feedback Engine):**
    - Architected all methods for displaying user feedback: confirmations, errors, warnings, successful operations, and contextual status updates
    - Developed clear and consistent output routines for all core user actions: adding, deleting, marking, unmarking, and listing tasks, as well as for timer and error events
    - Created utility methods for formatting messages, including separators, index displays, completion icons, and deadline/timer status formatting
    - Designed dynamic message generation to reflect real-time application state, ensuring users always receive precise, actionable responses

- **Impact on Usability:**
    - Enhanced user experience with concise, instructive, and visually organised feedback
    - Reduced confusion and friction by providing specific error messages, usage hints, and detailed feedback following every command
    - Encouraged best practices for UI messaging and user onboarding throughout the codebase

- **Extension and Maintainability:**
    - Structured the message logic to accommodate future feature additions (e.g., reminders, habits, more timers) without redundancy or brittle code
    - Centralised all feedback output, supporting easier updates and project-wide consistency for both GUI and command-line modes

---

### CommandHandler Component
- **CommandHandler.java (Execution & Routing Engine):**
    - Built core routing logic to dispatch every supported command (`todo`, `deadline`, `list`, `mark`, `unmark`, `delete`, `timer` etc.) to the correct handling modules
    - Managed task operations: adding, editing, marking, deleting, and reading from the TaskList, with batch operation and index validation logic for robust user input
    - Integrated advanced exception handling for user errors, invalid commands, and edge cases, ensuring resilience and clear, actionable feedback for all command scenarios
    - Refactored and centralised all feedback output (added/removed/modified tasks, timer events, errors) to ensure UI clarity and maintainable code separation
    - Coordinated with Timer features by routing timer control commands and synchronising UI feedback and status messaging on timer events
    - Structured command handling methods for future extensibility (e.g., reminders, habits), and maintained rigorous separation between core application state, UI, and parser logic

---

### Parser Component
- **Parser.java (Input Interpretation & Validation):**
    - Authored parsing logic for major commands (`todo`, `deadline`, `list`, `mark`, `unmark`, `delete`, `timer`, etc.), transforming free-form user input to well-defined command objects
    - Implemented flexible argument splitting and whitespace normalisation, guaranteeing user commands are interpreted accurately, regardless of spacing or minor format deviations
    - Designed error handling and validation flows to immediately notify users of missing or invalid arguments, preventing misinterpretation or application crashes
    - Ensured extensibility for future commands (reminders, habits, etc.) by using a modular, pattern-driven design

- **Impact:**
    - Enabled the entire StudyMate experience to be keyboard-first, command-driven, and extremely tolerant to variations in user input style
    - Reduced user friction, enhanced productivity, and made the application robust for both beginners and advanced users
    - Positioned the project for easy addition of new command types and features

---

### Data Storage Component
- **DataPersistence (File I/O and Storage Module):**
    - Implemented robust file read/write logic to serialise and deserialise Task objects (`ToDo`, `Deadline`) to/from disk
    - Designed save/load formats and ensured backward compatibility, allowing tasks to persist smoothly across StudyMate sessions
    - Developed utility routines for formatting task data as strings (`toSaveString()` in Task and subclasses) and for parsing saved entries during application startup
    - Integrated defensive error handling to manage corrupted/missing files, ensuring the application never crashes and users never lose their work
    - Facilitated straightforward extensibility so new features (e.g., timer sessions, future modules) could also be persisted with minimal changes

---

## Pull Requests
- [#4](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/4/)
- [#6](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/6/)
- [#20](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/20)
- [#24](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/24)
- [#26](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/26)
- [#38](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/38)
- [#42](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/42)
- [#44](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/44)
- [#51](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/51)

---

## Review Contributions
- [#3](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/3)
- [#5](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/5)
- [#7](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/7)
- [#8](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/8)
- [#10](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/10)
- [#17](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/17)
- [#18](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/18)
- [#21](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/21)
- [#25](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/25)
- [#30](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/30)
- [#31](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/31)
- [#32](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/32)
- [#41](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/36)
- [#48](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/48)
- [#50](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/50)
- [#52](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/52)
- [#56](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/56)
- [#57](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/57)
- [#62](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/62)
- [#63](https://github.com/AY2526S1-CS2113-W12-3/tp/pull/63)