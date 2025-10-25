# Rayan Wong - Project Portfolio Page

## Overview
NUS students who rely on laptops to manage lectures, labs, CCAs, and project deadlines, prefer typing to clicking, and need a quick, distraction-free way to record tasks, run short focus timers, set reminders, and track simple habits offline. A keyboard-first planner: add tasks, start focus timers, set reminders, and log habits in seconds from the command line, built for fast typing and offline use, it displays clear summaries of tasks, due dates, time spent, and habit streaks to help students manage workload during busy weeks and exams.

### Summary of Contributions

#### Code contributed
[RepoSense link](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=&sort=totalCommits%20dsc&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-09-19T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Rayan-Wong&tabRepo=AY2526S1-CS2113-W12-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

#### Enhancements implemented
* **Parser Component**: Implemented the core parsing system that interprets user input and converts it into executable commands. This includes:
  - Support for multiple command formats (todo, deadline, event, list, find, edit, mark, unmark, delete, reminders, timer operations)
  - Flexible index parsing (single, comma-separated, ranges with "...")
  - Date-time parsing with validation
  - Duration interval parsing for reminders
  - Whitespace normalization and case-insensitive command handling
  - Comprehensive error handling with descriptive error messages

* **CommandHandler Component**: Implemented the command execution system that coordinates between Model components and the UI. This includes:
  - Routing commands to appropriate handler methods
  - Task management operations (add, edit, mark, delete)
  - Reminder management operations (add one-time/recurring, list, delete, snooze, on/off)
  - Timer lifecycle management (start, pause, resume, reset, statistics)
  - Index validation for batch operations
  - Integration with ScheduledExecutorService for timer monitoring
  - Proper cleanup and resource management

#### Contributions to the User Guide
* Documented the **todo** command with format, examples, and expected output
* Documented the **deadline** command with format, examples, and usage notes
* Documented the **event** command with format, examples, and validation rules
* Updated the Introduction and Quick Start sections with accurate setup instructions
* Enhanced the Command Summary with complete command reference

#### Contributions to the Developer Guide
* **Parser Component Section**: Comprehensive documentation including:
  - Component structure with class diagrams
  - Component interactions with other system parts
  - Detailed sequence diagram for command parsing workflow
  - Key design considerations and trade-offs
  - Complete list of supported command formats

* **CommandHandler Component Section**: Comprehensive documentation including:
  - Component structure with class diagrams
  - Component interactions diagram
  - Detailed sequence diagram for command execution workflow
  - Key design considerations (static vs instance methods, timer monitoring, command routing, error handling)
  - Command execution flow for all command categories

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

#### Contributions beyond the project team
* **CI/CD Pipeline**: Set up GitHub Actions workflows to automatically notify the team via Telegram when:
  - New pull requests are opened ([pull-request.yml](https://github.com/AY2526S1-CS2113-W12-3/tp/blob/master/.github/workflows/pull-request.yml))
  - New commits are pushed to the main branch ([push.yml](https://github.com/AY2526S1-CS2113-W12-3/tp/blob/master/.github/workflows/push.yml))
  
* **Code Coverage Enhancement**: Added JaCoCo test coverage reporting with:
  - Total project code coverage metrics
  - Per-class code coverage breakdown
  - Configured to show reports in the build process for continuous monitoring of test quality

