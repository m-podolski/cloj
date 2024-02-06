# cloj

## Practice Ideas

### Math

- [ ] root-simplificator
- [ ] polynomial-expander-factorizer
- [ ] rectangular-polar-coord-converter
    - [ ] extend for complex numbers

### Easy

- [x] (wonderland clojure kata) alphabet-cipher
    - [ ] refactor extract ucp-char-conversion
- [ ] password-generator
    - [ ] strength-validator
    - [ ] generation with console-to-file-io
- [ ] quicksort and binary search with some optimisation
- [ ] (khan cst rec. alg.) sierpinski gasket

### Difficult

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
    - `>=` 25 characters for simple passwords (2 types of characters)

2. No surrounding characters on an otherwise simple password

    - no single numbers at the end
    - no usual special characters (`$ ! ? #`) at the start or end

3. No Character Patterns

    - no repeated sequences
    - no keyboard/alphabetic patterns (`asdfgh` or `1234abcd`)

4. No Semantic patterns

    - no common sequences of characters
    - full password not a word that appears in the dictionary (for simple passwords with length
      `>=` 25)

5. Easily rememberable

##### Unsupported BSI-Criteria

- **Validation**: 4, 5
- **Generation**: WIP

##### Validation-Rating

- **strong**: 1 and 2 and 3
- **moderate**: 1 and (2 xor 3)
- **weak**: 1 xor (2 or 3)
