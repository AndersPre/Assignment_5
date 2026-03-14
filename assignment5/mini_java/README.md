# Mini Java Implementation — Assignments 5a, 5b & 5c

## Assignment 5a: Adding Operators and PrintStatement

### Operators

All arithmetic operators were implemented for both `INT` and `FLOAT` types:

| Operator | Symbol | Arity  | Types supported |
|----------|--------|--------|-----------------|
| PLUS1    | `+x`   | Unary  | INT, FLOAT      |
| MINUS1   | `-x`   | Unary  | INT, FLOAT      |
| PLUS2    | `x+y`  | Binary | INT, FLOAT      |
| MINUS2   | `x-y`  | Binary | INT, FLOAT      |
| MULT     | `x*y`  | Binary | INT, FLOAT      |
| DIV      | `x/y`  | Binary | INT, FLOAT      |
| MOD      | `x%y`  | Binary | INT, FLOAT      |

**Type checking** (`ProgramTypeVisitor`):
The `operatorTypes` map associates each operator with its supported types (`INT` and `FLOAT`). During type checking, the visitor verifies that all operands of an operator have the same type and that the operator supports that type.

**Evaluation** (`ProgramExecutorVisitor`):
Each operator-type combination has a corresponding lambda function (e.g. `multint`, `divfloat`). These are stored in the `operatorFunctions` map and looked up at execution time based on the operator and the type derived during type checking.

### PrintStatement

The `PrintStatement` was implemented in `ProgramExecutorVisitor.visit(PrintStatement)`:
1. The expression is evaluated by calling `accept(this)` on it.
2. The resulting value is retrieved from the `values` map.
3. The prefix string followed by the value is printed to the console.

In `ProgramTypeVisitor`, the visit method recursively type-checks the expression without imposing any additional type constraints.

---

## Assignment 5b: While Loop (Control Flow)

Mini Java has no boolean type. Instead, the while loop condition is an integer expression interpreted as: the loop continues while the expression value is **>= 0**.

**Type checking** (`ProgramTypeVisitor.visit(WhileLoop)`):
- The condition expression and the loop body are recursively type-checked.
- The condition expression must be of type `INT`. If it is not (e.g. `FLOAT`), a type error is added to the `problems` list.

**Execution** (`ProgramExecutorVisitor.visit(WhileLoop)`):
1. The condition expression is evaluated.
2. While the value is `>= 0`:
   - The loop body statement is executed.
   - The condition expression is re-evaluated.
3. When the value becomes negative, the loop terminates.

---

## Assignment 5c: Testing

### Test overview

The following tests are implemented in `TestMiniJava.java`:

| Test | What it covers |
|------|----------------|
| `testCorrectProgramWithInts` | Declaration, Assignment, PLUS2 with int, nested assignments |
| `testCorrectlyTypedProgramWithFloats` | Declaration, Assignment, MINUS2 with float |
| `testWronglyTypedProgram` | Redeclared variable, undefined variable, type mismatch errors |
| `testLoopProgram` | Nested WhileLoops, PLUS2/MINUS2 with int, PrintStatement |
| `testPrintAndAdditionalOperators` | PLUS1, MINUS1 (int & float), MOD/DIV (int), DIV (float), PrintStatement |
| `testMultiplicationAndRemainingOperators` | MULT (int & float), PLUS2 (float), MOD (float) |
| `testWhileLoopWithFloatConditionIsRejected` | Type error: float condition in WhileLoop |
| `testOperandTypeMismatch` | Type error: mixed int/float operands in operator |
| `testAssignmentTypeMismatch` | Type error: assigning float to int variable |
| `testDeclarationTypeMismatch` | Type error: int declaration with float initializer |
| `testWhileLoopZeroIterations` | WhileLoop that never executes (condition initially < 0) |
| `testDeclarationWithoutInitializer` | Declaration without initializer, followed by assignment |

### Lambda coverage

All 14 operator lambda functions are covered by tests:

- `plus1int`, `plus1float`, `minus1int`, `minus1float` — covered by `testPrintAndAdditionalOperators`
- `plus2int` — covered by `testCorrectProgramWithInts` and `testLoopProgram`
- `plus2float` — covered by `testMultiplicationAndRemainingOperators`
- `minus2int` — covered by `testLoopProgram`
- `minus2float` — covered by `testCorrectlyTypedProgramWithFloats`
- `multint`, `multfloat` — covered by `testMultiplicationAndRemainingOperators`
- `divint` — covered by `testPrintAndAdditionalOperators`
- `divfloat` — covered by `testPrintAndAdditionalOperators`
- `modint` — covered by `testPrintAndAdditionalOperators`
- `modfloat` — covered by `testMultiplicationAndRemainingOperators`

### Lines not covered by tests and why they are not relevant

The following lines in `ProgramTypeVisitor` and `ProgramExecutorVisitor` may not be covered:

These uncovered lines are **defensive error-handling code** that protects against internal inconsistencies. They cannot be reached through valid usage of the Mini Java system (type-check first, then execute only if no errors). They are not relevant for functional coverage of the assignment requirements.
