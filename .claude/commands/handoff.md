---
description: Generate a developer handoff summary for the next session or PR
---

Provide a structured Handoff Summary for current session: $ARGUMENTS

Start with the branch and PR target (`git branch --show-current`; PRs target `main`).

Include the following sections:
- **Work Completed**: Summary of code added/modified across `core` and `app`.
- **Architectural Notes**: Any new Ports or Adapters introduced, and which AI profile(s) the
  change applies to.
- **Pending Items / Next Steps**: Tasks remaining from the original plan.
- **Verification Status**: Result of `./gradlew build` (the correctness gate) and `git status`
  for uncommitted work.

Format the response in concise Markdown so it can be pasted into a pull request or issue comment.
