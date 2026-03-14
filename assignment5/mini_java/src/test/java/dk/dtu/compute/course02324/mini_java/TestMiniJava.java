package dk.dtu.compute.course02324.mini_java;

import dk.dtu.compute.course02324.mini_java.model.*;
import dk.dtu.compute.course02324.mini_java.semantics.*;

import static dk.dtu.compute.course02324.mini_java.utils.Shortcuts.*;
import static dk.dtu.compute.course02324.mini_java.model.Operator.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These are some basic tests of the MiniJava for computing the types and
 * evaluating expressions.
 */
public class TestMiniJava{

    private ProgramTypeVisitor ptv;

    private ProgramExecutorVisitor pev;

    /**
     *  Sets up the visitors for type checking and execution.
     */
    @BeforeEach
    public void setUp() {
        ptv = new ProgramTypeVisitor();
        pev = new ProgramExecutorVisitor(ptv);
    }

    @Test
    public void testCorrectProgramWithInts() {
        int i;
        int j = i = 2 + (i = 3) ;

        Statement statement = new Sequence(
                new Declaration(INT, new Var("i")),
                new Declaration(
                        INT,
                        new Var("j"),
                        new Assignment(
                                new Var("i"),
                                new OperatorExpression(
                                        PLUS2,
                                        new IntLiteral(2),
                                        new Assignment(
                                                new Var("i"),
                                                new IntLiteral(3)
                                        )))
                )
        );

        ptv.visit(statement);
        if (!ptv.problems.isEmpty()) {
            fail("The type visitor did detect typing problems, which should not be there!");
        }

        pev.visit(statement);

        Set<String> variables = new HashSet<>(List.of("i", "j"));
        for (Var var: ptv.variables) {
            variables.remove(var.name);

            if (var.name.equals("i")) {
                assertEquals(i, pev.values.get(var), "Value of variable i should be " + i + ".");
            } else if (var.name.equals("j")) {
                assertEquals(j, pev.values.get(var), "Value of variable j should be " + j + ".");
            } else {
                fail("A non-existing variable " + var.name + " occurred in evaluation of program.");
            }
        }
        assertEquals(0, variables.size(), "Some variables have not been evaluated");
    }

    @Test
    public void testCorrectlyTypedProgramWithFloats() {
        float i;
        float j = i = 2.75f - ( i = 3.21f );

        Statement statement =
                new Sequence(
                        new Declaration(FLOAT, new Var("i")),
                        new Declaration(
                                FLOAT,
                                new Var("j"),
                                new Assignment(
                                        new Var("i"),
                                        new OperatorExpression(
                                                MINUS2,
                                                new FloatLiteral(2.75f),
                                                new Assignment(
                                                        new Var("i"),
                                                        new FloatLiteral(3.21f)
                                                )
                                        )
                                )
                        )
                );

        ptv.visit(statement);
        if (!ptv.problems.isEmpty()) {
            fail("The type visitor did detect typing problems, which should not be there!");
        }
        pev.visit(statement);

        Set<String> variables = new HashSet<>(List.of("i", "j"));
        for (Var var: ptv.variables) {
            variables.remove(var.name);

            if (var.name.equals("i")) {
                assertEquals(i, pev.values.get(var), "Value of variable i should be " + i + ".");
            } else if (var.name.equals("j")) {
                assertEquals(j, pev.values.get(var), "Value of variable j should be " + j + ".");
            } else {
                fail("A non-existing variable " + var.name + " occurred in evaluation of program.");
            }
        }
        assertEquals(0, variables.size(), "Some variables have not been evaluated");

    }

    @Test
    public void testWronglyTypedProgram() {
        int i;
        int j = i = 2 + (i = 3) ;

        Statement statement =
                new Sequence(
                        new Declaration(FLOAT, new Var("i")),
                        new Declaration(INT, new Var("j")),
                        new Declaration(
                                FLOAT,
                                new Var("j"),
                                new Assignment(
                                        new Var("i"),
                                        new OperatorExpression(
                                                MINUS2,
                                                new FloatLiteral(2.75f),
                                                new Assignment(
                                                        new Var("i"),
                                                        new FloatLiteral(3.21f)
                                                )
                                        )
                                )
                        ),
                        Assignment(Var("i"), Var("k")),
                        Assignment(Var("k"), Literal(3))
                );

        ptv.visit(statement);

        if (ptv.problems.isEmpty()) {
            fail("No type problems detected in a mistyped statement!");
        }
    }

    @Test
    public void testLoopProgram() {
        int i = 5;
        int j = 0;
        int sum = 0;
        while ( i >= 0 ) {
            j = i;
            while ( j >= 0 ) {
                sum = sum + j;
                j = j - 1;
                // println(" i: ", i);
                // println(" j: ", j);
            };
            i = i - 1;
        };

        Statement statement = Sequence(
                Declaration(INT, Var("i"), Literal(5)),
                Declaration(INT, Var("sum"), Literal(0)),
                WhileLoop(
                        Var("i"),
                        Sequence(
                                Declaration(INT, Var("j"), Var("i")),
                                WhileLoop(
                                        Var("j"),
                                        Sequence(
                                                Assignment(
                                                        Var("sum"),
                                                        OperatorExpression(PLUS2,
                                                                Var("sum"),
                                                                Var("j")
                                                        )
                                                ),
                                                Assignment(
                                                        Var("j"),
                                                        OperatorExpression(MINUS2,
                                                                Var("j"),
                                                                Literal(1)
                                                        )
                                                ),
                                                PrintStatement(" i: ", Var("i")),
                                                PrintStatement(" j: ", Var("j"))
                                        )
                                ),
                                Assignment(
                                        Var("i"),
                                        OperatorExpression(MINUS2,
                                                Var("i"),
                                                Literal(1)
                                        )
                                )
                        )
                )
        );

        ptv.visit(statement);
        if (!ptv.problems.isEmpty()) {
            fail("The type visitor did detect typing problems, which should not be there!");
        }
        pev.visit(statement);

        Set<String> variables = new HashSet<>(List.of("i", "j", "sum"));
        for (Var var: ptv.variables) {
            variables.remove(var.name);

            if (var.name.equals("i")) {
                assertEquals(i, pev.values.get(var), "Value of variable i should be " + i + ".");
            } else if (var.name.equals("j")) {
                assertEquals(j, pev.values.get(var), "Value of variable j should be " + j + ".");
            } else if (var.name.equals("sum")) {
                assertEquals(sum, pev.values.get(var), "Value of variable sum should be " + sum + ".");
            } else {
                fail("A non-existing variable " + var.name + " occurred in evaluation of program.");
            }
        }
        assertEquals(0, variables.size(), "Some variables have not been evaluated");
    }

    @Test
    public void testPrintAndAdditionalOperators() {
        int i = - + -1 + 7 - 1;
        float x = - + -1.5f + 7.0f - 1.0f;
        int j = 36 % 7;
        int k = 36 / 7;
        float y = 36.0f / 7.0f;

        Sequence printStatements = Sequence(
                Declaration(INT,
                        Var("i"),
                        OperatorExpression(MINUS2,
                                OperatorExpression(PLUS2,
                                        OperatorExpression(MINUS1,
                                                OperatorExpression(PLUS1,
                                                        Literal(-1)
                                                )
                                        ),
                                        Literal(7)
                                ),
                                Literal(1)
                        )
                ),
                PrintStatement(" - + -1 + 7 - 1: ",
                        Var("i")
                ),
                Declaration(FLOAT,
                        Var("x"),
                        OperatorExpression(MINUS2,
                                OperatorExpression(PLUS2,
                                        OperatorExpression(MINUS1,
                                                OperatorExpression(PLUS1,
                                                        Literal(-1.5f)
                                                )
                                        ),
                                        Literal(7.0f)
                                ),
                                Literal(1.0f)
                        )
                ),
                PrintStatement(" - + -1.5f + 7.0f - 1.0f: ",
                        Var("x")
                ),
                Declaration(INT,
                        Var("j"),
                        OperatorExpression(MOD,
                                Literal(36),
                                Literal(7)
                        )
                ),
                PrintStatement("36 % 7: ",
                        Var("j")
                ),
                Declaration(INT,
                        Var("k"),
                        OperatorExpression(DIV,
                                Literal(36),
                                Literal(7)
                        )
                ),
                PrintStatement("36 / 7: ",
                        Var("k")
                ),
                Declaration(FLOAT,
                        Var("y"),
                        OperatorExpression(DIV,
                                Literal(36.0f),
                                Literal(7.0f)
                        )
                ),
                PrintStatement("36.0f / 7.0: ",
                        Var("y")
                )
        );


        ptv.visit(printStatements);
        if (!ptv.problems.isEmpty()) {
            fail("The type visitor did detect typing problems, which should not be there!");
        }

        pev.visit(printStatements);

        Set<String> variables = new HashSet<>(List.of("i", "x", "j", "k", "y"));
        for (Var var: ptv.variables) {
            variables.remove(var.name);

            if (var.name.equals("i")) {
                assertEquals(i, pev.values.get(var), "Value of variable i should be " + i + ".");
            } else if (var.name.equals("x")) {
                assertEquals(x, pev.values.get(var), "Value of variable j should be " + x + ".");
            } else if (var.name.equals("j")) {
                assertEquals(j, pev.values.get(var), "Value of variable j should be " + j + ".");
            } else if (var.name.equals("k")) {
                assertEquals(k, pev.values.get(var), "Value of variable j should be " + k + ".");
            } else if (var.name.equals("y")) {
                assertEquals(y, pev.values.get(var), "Value of variable j should be " + y + ".");
            } else {
                fail("A non-existing variable " + var.name + " occurred in evaluation of program.");
            }
        }
        assertEquals(0, variables.size(), "Some variables have not been evaluated");
    }

    /**
     * Tests MULT operator for both int and float, PLUS2 with floats,
     * and MOD with floats — covering all remaining lambda expressions.
     */
    @Test
    public void testMultiplicationAndRemainingOperators() {
        // Java reference computations
        int a = 6 * 7;
        float b = 2.5f * 3.0f;
        float c = 2.5f + 3.5f;
        float d = 10.0f % 3.0f;

        Statement statement = Sequence(
                Declaration(INT, Var("a"),
                        OperatorExpression(MULT, Literal(6), Literal(7))
                ),
                Declaration(FLOAT, Var("b"),
                        OperatorExpression(MULT, Literal(2.5f), Literal(3.0f))
                ),
                Declaration(FLOAT, Var("c"),
                        OperatorExpression(PLUS2, Literal(2.5f), Literal(3.5f))
                ),
                Declaration(FLOAT, Var("d"),
                        OperatorExpression(MOD, Literal(10.0f), Literal(3.0f))
                )
        );

        ptv.visit(statement);
        assertTrue(ptv.problems.isEmpty(), "No type problems expected: " + ptv.problems);

        pev.visit(statement);

        for (Var var : ptv.variables) {
            Number val = pev.values.get(var);
            switch (var.name) {
                case "a" -> assertEquals(a, val, "6 * 7 should be " + a);
                case "b" -> assertEquals(b, val, "2.5f * 3.0f should be " + b);
                case "c" -> assertEquals(c, val, "2.5f + 3.5f should be " + c);
                case "d" -> assertEquals(d, val, "10.0f % 3.0f should be " + d);
                default -> fail("Unexpected variable: " + var.name);
            }
        }
    }

    /**
     * Tests that a WhileLoop with a float condition is rejected by the type checker.
     */
    @Test
    public void testWhileLoopWithFloatConditionIsRejected() {
        Statement statement = Sequence(
                Declaration(FLOAT, Var("x"), Literal(5.0f)),
                WhileLoop(
                        Var("x"),
                        Assignment(Var("x"),
                                OperatorExpression(MINUS2, Var("x"), Literal(1.0f))
                        )
                )
        );

        ptv.visit(statement);
        assertFalse(ptv.problems.isEmpty(), "Float condition in while loop should be rejected");
    }

    /**
     * Tests type mismatch between operands of an operator (int + float).
     */
    @Test
    public void testOperandTypeMismatch() {
        Statement statement = Sequence(
                Declaration(INT, Var("a"), Literal(1)),
                Declaration(FLOAT, Var("b"), Literal(2.0f)),
                Declaration(INT, Var("c"),
                        OperatorExpression(PLUS2, Var("a"), Var("b"))
                )
        );

        ptv.visit(statement);
        assertFalse(ptv.problems.isEmpty(), "Mixing int and float operands should produce a type error");
    }

    /**
     * Tests assignment type mismatch (assigning float expression to int variable).
     */
    @Test
    public void testAssignmentTypeMismatch() {
        Statement statement = Sequence(
                Declaration(INT, Var("x"), Literal(0)),
                Assignment(Var("x"), Literal(1.5f))
        );

        ptv.visit(statement);
        assertFalse(ptv.problems.isEmpty(), "Assigning float to int variable should be a type error");
    }

    /**
     * Tests declaration type mismatch (declaring int variable with float initializer).
     */
    @Test
    public void testDeclarationTypeMismatch() {
        Statement statement = Sequence(
                Declaration(INT, Var("x"), Literal(3.14f))
        );

        ptv.visit(statement);
        assertFalse(ptv.problems.isEmpty(), "Declaring int with float initializer should be a type error");
    }

    /**
     * Tests that a while loop executes zero times when condition is initially negative.
     */
    @Test
    public void testWhileLoopZeroIterations() {
        int x = -1;

        Statement statement = Sequence(
                Declaration(INT, Var("x"), Literal(-1)),
                WhileLoop(
                        Var("x"),
                        Assignment(Var("x"),
                                OperatorExpression(MINUS2, Var("x"), Literal(1))
                        )
                )
        );

        ptv.visit(statement);
        assertTrue(ptv.problems.isEmpty(), "No type problems expected");

        pev.visit(statement);
        for (Var var : ptv.variables) {
            if (var.name.equals("x")) {
                assertEquals(x, pev.values.get(var), "x should remain " + x + " (loop never entered)");
            }
        }
    }

    /**
     * Tests declaration without initializer followed by assignment.
     */
    @Test
    public void testDeclarationWithoutInitializer() {
        int r = 10;

        Statement statement = Sequence(
                Declaration(INT, Var("r")),
                Assignment(Var("r"), Literal(10))
        );

        ptv.visit(statement);
        assertTrue(ptv.problems.isEmpty(), "No type problems expected");

        pev.visit(statement);
        for (Var var : ptv.variables) {
            if (var.name.equals("r")) {
                assertEquals(r, pev.values.get(var));
            }
        }
    }

}
