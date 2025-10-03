package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;




import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code Lexer} class is responsible for lexical analysis (tokenization)
 * of an input string into a sequence of tokens based on a predefined pattern.
 * <p>
 * It recognizes keywords, operators, identifiers, numbers, symbols, and throws
 * an exception for any unrecognized input.
 * <p>
 * Example recognized tokens include:
 * <ul>
 *     <li>Keywords: {@code Klausul}, {@code og}, {@code eller}</li>
 *     <li>Operators: {@code >=}, {@code <=}, {@code =}, {@code i}</li>
 *     <li>Identifiers: sequences starting with a letter followed by letters or digits</li>
 *     <li>Numbers: numeric digits</li>
 *     <li>Symbols: {@code :}, {@code ,}, {@code (}, {@code )}</li>
 * </ul>
 */
class Lexer {

    /**
     * Regular expression pattern for matching different kinds of tokens.
     */
    private static final Pattern TOKEN_PATTERNS = Pattern.compile(
            "\\s*(?:(klausul|og|eller)|" +       // keywords
                    "(>=|<=|=|>|<|\\bi\\b)|" +   // operators (with word-boundary for "i")
                    "([A-Za-z][A-Za-z0-9_]*)|" + // identifiers
                    "([0-9]+)|" +                // numbers
                    "([:,()*])|" +               // symbols
                    "(\\S))",                    // unknown
            Pattern.CASE_INSENSITIVE
    );


    private final Matcher matcher;
    private final List<Token> tokens = new ArrayList<>();

    /**
     * Constructs a {@code Lexer} for the given input string.
     * It immediately tokenizes the input upon creation.
     *
     * @param input the string to be tokenized
     */
    public Lexer(String input) {
        matcher = TOKEN_PATTERNS.matcher(input);
        tokenize();
    }


    /**
     * Performs the tokenization by matching the input string against
     * the predefined token patterns and classifying each match accordingly.
     * <p>
     * If an unrecognized token is encountered, it throws a {@code RuntimeException}.
     */
    private void tokenize() {
        while (matcher.find()) {
            if (matcher.group(1) != null)
                tokens.add(new Token(TokenType.KEYWORD, matcher.group(1)));
            else if (matcher.group(2) != null)
                tokens.add(new Token(TokenType.OPERATOR, matcher.group(2)));
            else if (matcher.group(3) != null)
                tokens.add(new Token(TokenType.IDENTIFIER, matcher.group(3)));
            else if (matcher.group(4) != null)
                tokens.add(new Token(TokenType.NUMBER, matcher.group(4)));
            else if (matcher.group(5) != null)
                tokens.add(new Token(TokenType.SYMBOL, matcher.group(5)));
            else
                throw new RuntimeException("Unknown token: " + matcher.group(6));
        }
        tokens.add(new Token(TokenType.EOF, ""));
    }

    /**
     * Returns the list of tokens generated from the input string.
     *
     * @return a list of {@code Token} objects
     */
    public List<Token> getTokens() {
        return tokens.stream().map(x-> new Token(x.type(), x.text().toUpperCase())).toList();
    }
}
