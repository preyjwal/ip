# UI Test Plan

Test cases for the `test-ui` skill (`.claude/skills/test-ui/`). Each test case
is run as one continuous Steph session: its commands are sent to the program
in order, and the output that follows each command is checked against the
recorded expected output. State (e.g. tasks added earlier in the same test
case) carries across commands within a test case, the same way it would in a
real session -- but each test case starts a fresh instance of the program, so
nothing carries over *between* test cases.

Run the whole plan with:

```
python3 .claude/skills/test-ui/scripts/run_ui_tests.py
```

## Format

Each test case starts with a `## Test case: <short name>` heading, followed
by an `**Aim:**` line describing what it's checking and why. After that comes
one or more pairs of sections: a `### Command` heading with the single
command in a fenced code block, then an `### Expected output` heading with
the response Steph should print in a fenced code block -- without the
surrounding `____...` delimiter lines, since those are added automatically.
The test cases below are the canonical example of the format; copy the
structure of one of those rather than improvising.

A test case can have as many Command/Expected output pairs as needed. Don't
put more than one command in a single Command block.

Don't hand-type the expected output -- it's easy to get a space or a blank
line wrong and end up "fixing" a false failure instead of a real one. Instead
run the commands for real and let the script generate the block:

```
python3 .claude/skills/test-ui/scripts/run_ui_tests.py record "todo buy milk" "list"
```

then read over the printed output to confirm it's actually correct (the
script only captures what the program did, not what it *should* do) before
pasting it in here.

Don't include `bye` as a command in a test case: the program doesn't print a
response block for it (it just ends the session), and the session ends on its
own once its commands run out anyway.

---

## Test case: Add a todo and list it

**Aim:** A `todo` command with a valid name adds a task, and `list` displays
it with the `[T]` type marker and an unchecked `[ ]` box.

### Command
```
todo borrow book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Command
```
list
```

### Expected output
```
Here are the tasks in your list:
1.[T][ ] borrow book
```

## Test case: Add a deadline

**Aim:** A `deadline` command splits its argument on `/by` into the task name
and the due date/time, and displays both with the `[D]` marker.

### Command
```
deadline return book /by Sunday
```

### Expected output
```
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
```

## Test case: Add an event

**Aim:** An `event` command splits its argument on `/from` and `/to` into the
task name, start, and end, and displays all three with the `[E]` marker.

### Command
```
event project meeting /from Mon 2pm /to 4pm
```

### Expected output
```
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
```

## Test case: Mark and unmark a task

**Aim:** `mark` sets a task's checkbox to `[X]` and `unmark` sets it back to
`[ ]`, referencing the task by its 1-based position in the list.

### Command
```
todo borrow book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Command
```
mark 1
```

### Expected output
```
Awesome! I've marked this task as done:
  [T][X] borrow book
```

### Command
```
unmark 1
```

### Expected output
```
OK, I've marked this task as not done yet:
  [T][ ] borrow book
```

## Test case: Delete a task

**Aim:** `delete` removes the task at the given 1-based position, shifts the
remaining tasks up, and `list` reflects the updated numbering.

### Command
```
todo borrow book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Command
```
todo return book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
```

### Command
```
delete 1
```

### Expected output
```
Okay! I've removed this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Command
```
list
```

### Expected output
```
Here are the tasks in your list:
1.[T][ ] return book
```

## Test case: Delete with an out-of-range task number is rejected

**Aim:** `delete` reuses the same task-number validation as `mark`/`unmark`,
so a number beyond the end of the list should report a usage error instead
of throwing an `IndexOutOfBoundsException`.

### Command
```
todo borrow book
```

### Expected output
```
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
```

### Command
```
delete 5
```

### Expected output
```
Hmm.. I don't understand that.
Please type "delete <task-number>" with a valid task number.
```

## Test case: Deadline missing /by is rejected

**Aim:** A `deadline` command with no `/by` marker should not silently create
a task with a blank due date -- it should report a usage error instead.

### Command
```
deadline missing name
```

### Expected output
```
Hmm.. I don't understand that.
Please type "deadline <task-name> /by <date/time>".
```

## Test case: Unknown command is rejected

**Aim:** A command that isn't `list`/`mark`/`unmark`/`todo`/`deadline`/`event`
should report an error instead of being silently added to the task list as
plain text.

### Command
```
foo bar
```

### Expected output
```
Hmm.. I don't understand that command: "foo".
```
