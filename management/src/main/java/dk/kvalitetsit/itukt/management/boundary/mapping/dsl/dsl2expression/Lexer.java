package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression;


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
 *     <li>Keywords: {@code og}, {@code eller}</li>
 *     <li>Operators: {@code >=}, {@code <=}, {@code =}, {@code i}</li>
 *     <li>Identifiers: sequences starting with a letter followed by letters or digits</li>
 *     <li>Numbers: numeric digits</li>
 *     <li>Symbols: {@code :}, {@code ,}, {@code (}, {@code )}</li>
 * </ul>
 */
public class Lexer {

    /**
     * Regular expression pattern for matching different kinds of tokens.
     */
    private static final Pattern TOKEN_PATTERNS = Pattern.compile(
            "\\s*(?:(og|eller)|" +                              // keywords
                    "(>=|<=|=|>|<|\\bi\\b)|" +                  // operators (with word-boundary for "i")
                    "([a-z0-9_æøåÆØÅ*]+)|" +                    // values
                    "([,()\\[\\]{}])|" +                        // symbols
                    "(\\S))",                                   // unknown
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Performs the tokenization by matching the input string against
     * the predefined token patterns and classifying each match accordingly.
     * <p>
     * If an unrecognized token is encountered, it throws a {@code RuntimeException}.
     */
    private List<Token> tokenize(String input) {
        Matcher matcher = TOKEN_PATTERNS.matcher(input);
        ArrayList<Token> tokens = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group(1) != null)
                tokens.add(new Token(TokenType.KEYWORD, matcher.group(1)));
            else if (matcher.group(2) != null)
                tokens.add(new Token(TokenType.OPERATOR, matcher.group(2)));
            else if (matcher.group(3) != null)
                tokens.add(new Token(TokenType.VALUE, matcher.group(3)));
            else if (matcher.group(4) != null)
                tokens.add(new Token(TokenType.SYMBOL, matcher.group(4)));
            else
                throw new RuntimeException("Unknown token: " + matcher.group(5));
        }
        return tokens;
    }

    /**
     * Returns the list of tokens generated from the input string.
     *
     * @return a list of {@code Token} objects
     */
    public List<Token> getTokens(String input) {
        List<Token> tokens = tokenize(input);
        return tokens.stream().map(x -> new Token(x.type(), x.text().toUpperCase())).toList();
    }
}
