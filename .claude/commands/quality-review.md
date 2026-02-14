Perform a code quality analysis starting from: $ARGUMENTS

## How to explore the code

Start with the entry points specified above. For each component you encounter, follow it one level deeper — from screen into viewmodel, viewmodel into usecases, usecases into repositories. Stop at the repository implementation level. Do not go into datasources or platform-specific code. Stay within the shared module.

## Analysis steps (repeat for each component)

1. **Name** — note the function or class name.
2. **Purpose** — summarize what this component exists to do in short bullet points. Do not look at the implementation yet.
3. **Ideal contract** — based solely on your purpose summary, define:
   - What inputs/parameters it should need
   - What it should return, emit, or call
   - What internal state it should need to hold during its lifecycle
4. **Reality check** — now read the actual implementation and compare it to step 3. Flag any of the following:
   - Unnecessary data flowing through that shouldn't be there
   - Internal state that doesn't need to exist
   - Complexity that exceeds what the purpose requires
   - Spaghetti dependencies or coupling
   - Architectural concerns or bad practices

## Output format

Present findings as a report, one section per component. Be direct and specific — this is a reflective exercise to identify what went wrong and why, so we can improve both the code and the prompts that produced it.