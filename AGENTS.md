# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Some experience with building web-apps (with AI help).
* IDE and level of expertise: IntelliJ Idea. Knowledge of basic features.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing

After every code update (any change under `src/main/java`), before treating the task as done:

1. Update `test/ui-test-plan.md` if the change affects console output or adds/changes a command — add new test cases for new behavior, and update expected output for existing cases whose output legitimately changed. Generate expected-output blocks from a real run (the `test-ui` skill's `record` mode) rather than hand-typing them.
2. Invoke the `test-ui` skill to run the test plan and confirm it passes.

If the test plan fails, treat that as a signal to investigate before proceeding — either the change introduced a regression, or the expected output is stale and needs a deliberate update. Don't edit the test plan to match broken output just to make it pass.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
