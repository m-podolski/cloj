# cloj

## Practice Ideas

### Math

- [x] logic-library
    - [x] and, nand, or, xor, nor, if, iff
    - [ ] macros: converse, inverse, contrapositive
    - [ ] truth-value-evaluation
        - eval
        - contingency?, tautology?, contradiction?
- [ ] root-simplificator
- [ ] polynomial-expander-factorizer
- [ ] rectangular-polar-coord-converter
    - [ ] extend for complex numbers

### Easy

- [x] (wonderland clojure kata) alphabet-cipher
- [ ] password-tool
    - [ ] validator
        - core
        - parallel
    - [ ] generator
        - core (with property-based tests)
        - console-to-file-io (with java-interop)
- [ ] quicksort and binary search with some optimisation
- [ ] (khan cst rec. alg.) sierpinski gasket

### Difficult

- [ ] scale- and chord-finder
    - [ ] find chords on note
    - [ ] find chords on scale
- [ ] dijkstra's algorithm
- [ ] (rosetta code) twelve statements
- [ ] self-balancing search-tree (red-black or AVL)
- [ ] (rosetta code) knapsack-problem
- [ ] simplified timsort

## Documentation

### Encryption

#### Alphabet-Cipher

Exercise taken from [github.com/gigasquid/wonderland-clojure-katas/tree/master/alphabet-cipher](https://github.com/gigasquid/wonderland-clojure-katas/tree/master/alphabet-cipher)

Solution taken from [github.com/aaronj1335/wonderland-clojure-katas/tree/master/alphabet-cipher](https://github.com/aaronj1335/wonderland-clojure-katas/tree/master/alphabet-cipher)

#### Password-Generator

##### BSI-Criteria

Taken from [bsi.bund.de/EN/.../Sichere-Passwoerter-erstellen/sichere-passwoerter-erstellen_node. html](https://www.bsi.bund.de/EN/Themen/Verbraucherinnen-und-Verbraucher/Informationen-und-Empfehlungen/Cyber-Sicherheitsempfehlungen/Accountschutz/Sichere-Passwoerter-erstellen/sichere-passwoerter-erstellen_node.html)

1. Different chracter-types (upper/lower-case letters, numbers, special characters) relative to length

    - `>=` 8 characters for complex passwords (4 types of characters)
    - `>=` 20 characters for simple passwords (2 types of characters)

2. No surrounding characters on an otherwise simple password

    - no single numbers at the end
    - no usual special characters (`$ ! ? #`) at the start or end

3. No Character Patterns

    - no repeated sequences
    - no keyboard/alphabetic patterns (`asdfgh` or `1234abcd`)

4. No Semantic patterns

    - no common sequences of characters
    - full password not a word or consisting of words that appear in the dictionary (for simple passwords with length
      `>=` 20)

5. Easily rememberable

##### Unsupported BSI-Criteria

- **Validation**: 4, 5
- **Generation**: WIP

##### Validation-Rating

- **strong**: 1 and 2 and 3
- **moderate**: 1 and (2 xor 3)
- **weak**: 1 xor (2 or 3)

Note that the app assumes a keyboard-layout of type 'PC German' (which can easily be changed in `pw-tool.validation`.

### Math

#### Clojical

This library provides logical primitives that operate only on and through booleans so you can express logical constructs concisely and without risiking subtle errors introduced by any 'hybrid' behaviour like short-circuiting, default-values, etc. It provides a consistent API that corresponds to the mathematical entities closely and also allows argument-lists of arbitrary size in most cases.

##### API

###### clojical.core

All functions expect one or more arguments which are all boolean and return `nil` if called differently. This allows for a clear distinction between boolean evaluation-results and type-related problems while avoiding any explicit error-handling.

| Function | Parameters | Description                                                                     |
|:---------|:-----------|:--------------------------------------------------------------------------------|
| `and`    | `[& args]` | True if all arguments are true.                                                 |
| `nand`   | `[& args]` | True if at least one argument is false.                                         |
| `or`     | `[& args]` | True if at least one argument is true.                                          |
| `xor`    | `[& args]` | True if exactly one argument is true.                                           |
| `nor`    | `[& args]` | True if all arguments are false.                                                |
| `lif`    | `[x y]`    | '(Logical) If'. True if both arguments are true or the first argument is false. |
| `iff`    | `[x y]`    | 'If and only if'. True if both arguments are true or both are false.            |
| `nnil?`  | `[& args]` | 'not nil?'. True when 'clojure.core/nil?' is false and vice versa. (Macro)      |
