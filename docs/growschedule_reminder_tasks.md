# Grow Schedule Reminder Workflow Tasks

This document outlines the remaining tasks for implementing the improved reminder and completion
workflow described in the design notes.

## Tasks

- [ ] Add `notificationShown` field to `Task` model and persist it in the database.
- [ ] Update `WateringScheduler.runDailyCheck` to only send a notification when `notificationShown`
  is `false`.
- [ ] Implement a fun banner or animation in `HomeFragment` to nudge users when tasks remain
  incomplete.
- [ ] Replace the current reminders list with a `RecyclerView` that allows checking a task as done
  in one tap.
- [ ] Show a `Snackbar` with an undo option after marking a task complete.
- [ ] Document the new logic in `docs/growschedule_reminders.md` and link it from
  `feature-growschedule/README.md`.

Completing these items will finish the first version of the reminder workflow.
