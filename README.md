

# Advent of Code 2023

https://adventofcode.com/2023

A beautiful set of interesting tasks accompanied by the merry Christmas story.

My solutions to all tasks are implemented in **Java**, in one file each.

Each class usually contains several tests: test1, and test2 are for examples; answer1 and answer2 are solutions for part 1 and part 2. Everything with asserts to catch a bug after refactorings.

## Interesting sum-ups.

### Day 1.

### Day 20.

I am reflecting on the topic that a friend pointed out, that it is bad to modify the behavior of a function depending on booleans (and indeed any parameters, perhaps). In this case, the solution became simpler when I added a callback to collect the necessary information for two different tasks from the same simulation.
Callback allowed the function itself to focus purely on the simulation. Which makes it primitively simple, practically a reflection of the description of the problem. The calling code analyzes the internal state needed for the specific task.
And yes, even though the calling code delves into the internal state, in this case, it is easier than trying to hide this state (and implement two different logic based on this state).

### Day 21.

Discovered interesting basic properties for this world, where you can only walk N/S/E/W and make one step at a time.

1. If you paint the surface in a chess-board pattern, you will always end up on the same color on even/odd steps.
2. When waterspilling, you don't have to keep all seen places to avoid repeating. It is enough to keep only the previous step as 'seen'. It suffice to block you from going back.

### Day 23

When you implement pruning - you find the solution in an hour instead of years. When you prune really carefully, you solve the search in a second, instead of an hour.

