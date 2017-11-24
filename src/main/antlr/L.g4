grammar L;


file
    :   block EOF
    ;

block
    :   (NEWLINE)* statements += statement (NEWLINE+ statements += statement)* (NEWLINE)*
    ;

statement
    :   'fun' functionName = IDENTIFIER '(' parameterNames += IDENTIFIER
                                            (',' parameterNames += IDENTIFIER)* ')'
        '{' functionBody = block '}'
        # functionDefinitionStatement

    |   'var' variableName = IDENTIFIER (':=' initialValueExpression = expression)?
        # variableDefinitionStatement

    |   expression
        # expressionStatement

    |   'while' '(' condition = expression ')' '{' body = block '}'
        # whileStatement

    |   'if' '(' condition = expression ')' '{' thenBody = block '}'
        ('else' '{' elseBody = block '}')?
        # ifStatement

    |   IDENTIFIER ':=' expression
        # assignmentStatement

    |   'return' expression
        # returnStatement

    |   'write' '(' expression ')'
        # writeStatement
    ;

expression
    :   '(' expression ')'
        # expressionInParentheses

    |   IDENTIFIER
        # variableAccessExpression

    |   'read' '(' ')'
        # readExpression

    |   IDENTIFIER '(' arguments += expression (',' arguments += expression)* ')'
        # functionCallExpression

    |   DECIMAL_FLOATING_POINT_LITERAL
        # floatLiteralExpression

    |   DECIMAL_INTEGER_LITERAL
        # integerLiteralExpression

    |   leftOperand = expression operation = ('*' | '/' | '%') rightOperand = expression
        # binaryExpression

    |   leftOperand = expression operation = ('+' | '-') rightOperand = expression
        # binaryExpression

    |   leftOperand = expression operation = ('<' | '>' | '<=' | '>=') rightOperand = expression
        # binaryExpression

    |   leftOperand = expression operation = ('==' | '!=') rightOperand = expression
        # binaryExpression

    |   leftOperand = expression operation = '&&' rightOperand = expression
        # binaryExpression

    |   leftOperand = expression operation = '||' rightOperand = expression
        # binaryExpression
    ;


fragment ZERO_DIGIT
    :   '0'
    ;

fragment NON_ZERO_DIGIT
    :   [1-9]
    ;

fragment DIGIT
    :   ZERO_DIGIT
    |   NON_ZERO_DIGIT
    ;

fragment LETTER
    :   [a-zA-Z]
    ;

fragment UNDERSCORE
    :   '_'
    ;

fragment DIGIT_OR_UNDERSCORE
    :   DIGIT
    |   UNDERSCORE
    ;

fragment DIGITS_AND_UNDERSCORES
    :   DIGIT_OR_UNDERSCORE+
    ;

fragment DIGITS
    :   DIGIT
    |   DIGIT DIGITS_AND_UNDERSCORES? DIGIT
    ;

IDENTIFIER
    :   (LETTER | UNDERSCORE) (LETTER | DIGIT | UNDERSCORE)*
    ;

DECIMAL_INTEGER_LITERAL
    :   ZERO_DIGIT
    |   NON_ZERO_DIGIT DIGITS?
    |   NON_ZERO_DIGIT UNDERSCORE DIGITS
    ;


fragment EXPONENT_INDICATOR
    :   'e'
    |   'E'
    ;

fragment SIGN
    :   '+'
    |   '-'
    ;

fragment SIGNED_INTEGER
    :   SIGN? DIGITS
    ;

fragment EXPONENT_PART
    :   EXPONENT_INDICATOR SIGNED_INTEGER
    ;

DECIMAL_FLOATING_POINT_LITERAL
    :   DIGITS '.' DIGITS? EXPONENT_PART?
    |   '.' DIGITS EXPONENT_PART?
    |   DIGITS EXPONENT_PART
    ;

NEWLINE
    :   '\r'? '\n'
    ;

COMMENTARY
    :   '//' .*? (NEWLINE | EOF) -> skip
    ;

WHITESPACE
    :   ('\u0020' | '\u0009' | '\u000C')+ -> skip // SP | HT | FF
    ;
