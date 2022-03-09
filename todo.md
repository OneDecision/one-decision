# STATUS

## decisions-ui 
  - advantages: 
    - typeahead based on domain model
  - disadvantages:
    - not aligned to DMN
    - broken in various ways

## decisions-dmn
  - advantages
    - aligned to DMN
  - disadvantages:
    - incomplete: 
      - does not save
      - no typeahead on hit policy
      - cannot add columns or rows

# TASKS
  X remove decisions-ui
  X remove JSON tests 
  X move domain classes out to own module

  X revise Ch11 example to not require JSON model or domain

  - new BDD and DMN Fluent based tests (some kind of structure)
    - single end to end example: price based on age 
  - negative schema test
  - validation of end-to-end example
  - execution of end-to-end example

  - engine tests
  - webapp tests

  - address Keith code review

# TESTS needed
  - assert links provided by controllers (suspicion is they are broken)
  - decision.variable, check spec expectations, but we certainly need it.
