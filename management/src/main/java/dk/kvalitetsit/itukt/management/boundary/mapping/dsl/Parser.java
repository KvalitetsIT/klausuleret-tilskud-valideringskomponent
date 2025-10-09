package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;


import org.openapitools.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * The {@code Parser} class is responsible for parsing a sequence of {@link Token} objects
 * into an abstract syntax tree (AST) composed of {@link Expression} instances.
 * <p>
 * It supports parsing logical expressions with operators like {@code og} (AND),
 * {@code eller} (OR), and nested/parenthesized expressions, as well as simple
 * field-based conditions (e.g., {@code age >= 18}).
 */
class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    /**
     * Constructs a {@code Parser} from a list of lexical tokens.
     *
     * @param tokens the tokens to parse
     */
    protected Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Returns the current token without advancing the position.
     */
    private Token peek() {
        return tokens.get(pos);
    }

    /**
     * Consumes and returns the current token, advancing the position.
     */
    private Token next() {
        return tokens.get(pos++);
    }

    /**
     * Attempts to match and consume a token with the given text.
     *
     * @param text the expected token text
     * @return true if the token matched and was consumed; false otherwise
     */
    private boolean match(String text) {
        if (peek().text().equalsIgnoreCase(text)) {
            next();
            return true;
        }
        return false;
    }

    /**
     * Consumes the next token and verifies that it matches the expected text.
     *
     * @param text the expected token text
     * @throws RuntimeException if the token does not match the expected text
     */
    private void expect(String text) {
        Token token = next();
        if (!token.text().equalsIgnoreCase(text)) {
            throw new RuntimeException("Expected '" + text + "', got '" + token.text() + "'");
        }
    }

    /**
     * Parses a top-level "Klausul" expression, which starts with the keyword {@code Klausul}
     * followed by a name and a colon, and then a logical expression.
     *
     * @return the parsed {@code Expression}
     */
    protected ParsedClause parseClause() {
        expect("Klausul");
        Token name = next();
        expect(":");

        return new ParsedClause(name.text(), parseExpression());
    }


    /**
     * Parses a generic expression, delegating to {@link #parseOrExpression()}.
     *
     * @return the parsed {@code Expression}
     */
    private Expression parseExpression() {
        return parseOrExpression();
    }

    /**
     * Parses an expression joined by the {@code eller} (OR) operator.
     */
    private Expression parseOrExpression() {
        Expression left = parseAndExpression();
        while (match("eller")) {
            Expression right = parseAndExpression();
            left = new BinaryExpression(left, BinaryOperator.OR, right, "BinaryExpression");
        }
        return left;
    }

    /**
     * Parses an expression joined by the {@code og} (AND) operator.
     */
    private Expression parseAndExpression() {
        Expression left = parseOperand();
        while (match("og")) {
            Expression right = parseOperand();
            left = new BinaryExpression(left, BinaryOperator.AND, right, "BinaryExpression");
        }
        return left;
    }

    /**
     * Parses a single operand, which could be a condition or a nested expression in parentheses.
     */
    private Expression parseOperand() {
        if (match("(")) {
            Expression inner = parseExpression();
            expect(")");
            return inner;
        } else if (peekAheadIsCondition()) {
            return parseCondition(); // bare condition
        }
        throw new RuntimeException("Unexpected token: " + peek().text());
    }

    /**
     * Looks ahead to determine whether the upcoming tokens form a condition.
     *
     * @return true if the next tokens are a valid field/operator pair; false otherwise
     */
    private boolean peekAheadIsCondition() {
        if (pos + 1 < tokens.size()) {
            Token t1 = tokens.get(pos);     // Expected: IDENTIFIER
            Token t2 = tokens.get(pos + 1); // Expected: OPERATOR
            return t1.type() == TokenType.IDENTIFIER &&
                    t2.type() == TokenType.OPERATOR;
        }
        return false;
    }

    /**
     * Parses a condition expression, which consists of:
     * <ul>
     *     <li>An identifier (field name)</li>
     *     <li>An operator (e.g., {@code =}, {@code >=}, {@code i})</li>
     *     <li>One or more values, possibly separated by commas</li>
     * </ul>
     * <p>
     * The expression ends with a closing parenthesis.
     *
     * @return the parsed {@code Expression.Condition}
     */

    private Expression parseCondition() {
        String field = next().text();
        String operatorString = next().text();
        Operator operator = operatorString.equalsIgnoreCase("i") ? Operator.EQUAL : Operator.fromValue(operatorString);
        List<String> values = parseValues();

        return createExpressionFromMultiValueCondition(field, operator, values);
    }

    private List<String> parseValues() {
        List<String> values = new ArrayList<>();
        do {
            values.add(next().text());
        } while (match(","));
        return values;
    }

    private Expression createExpressionFromMultiValueCondition(String field, Operator operator, List<String> values) {
        Iterator<String> valuesIterator = values.iterator();
        Expression currentExpression = createCondition(field, operator, valuesIterator.next());
        while (valuesIterator.hasNext()) {
            Expression nextCond = createCondition(field, operator, valuesIterator.next());
            currentExpression = new BinaryExpression(currentExpression, BinaryOperator.OR, nextCond, "BinaryExpression");
        }
        return currentExpression;
    }

    private Expression createCondition(String field, Operator operator, String value) {
        final var upperCaseField = field.toUpperCase();
        return tryParseInt(value)
                .map(intValue -> createNumberCondition(upperCaseField, operator, intValue))
                .orElseGet(() -> createStringCondition(upperCaseField, value));
    }

    private Expression createStringCondition(String field, String value) {
        return new StringCondition(field, value, "StringCondition");
    }

    private Expression createNumberCondition(String field, Operator operator, int value) {
        return new NumberCondition(field, operator, value, "NumberCondition");
    }

    private Optional<Integer> tryParseInt(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    protected record ParsedClause(String name, Expression expression) {

    }
}

