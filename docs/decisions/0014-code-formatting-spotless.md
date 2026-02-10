# ADR-0014 – Code Formatting and Style via Spotless

## Status

Accepted

## Context

The project uses AI-assisted development (Cursor, Claude Code). Consistent formatting is essential to:

- reduce meaningless diffs
- keep PRs readable
- enforce a uniform style across generated/refactored code

The project uses `spotless-maven-plugin` with:

- google-java-format
- import order rules
- no wildcard imports
- remove unused imports

## Decision

Spotless is mandatory and runs in Maven `validate` phase.

### Rules

- All code must be formatted by Spotless.
- No manual formatting rules that conflict with Spotless.
- Wildcard imports are forbidden.
- Import order is enforced as configured in the plugin.
- PRs must not include formatting-only diffs mixed with logic changes unless unavoidable.

## Rationale

- Uniform style across contributors and AI tools
- Smaller diffs, easier reviews
- Lower cognitive load

## Consequences

- Developers must run Maven validate/build regularly
- AI-generated code must comply with formatting rules (Spotless will enforce)
