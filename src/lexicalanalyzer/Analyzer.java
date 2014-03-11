package lexicalanalyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Analyzer {

    FileInputStream inputContext;
    public Token token;
    static int inputCode,
            readingContextFlag = 1,
            lastRedCode,
            column = 1,
            row = 1,
            blockNumber = 0,
            blockNumberCounter = 0,
            blockOrder = 0;
    int state;
    char inputChar;
    String[] keywords = {"char", "int", "if", "else", "for"};
    static Stack<Integer> stack;

    //FileOutputStream outputContext;
    public Analyzer(String filePath) {
        Analyzer.stack = new Stack<>();
        stack.push(0);
        this.state = 0;
        try {
            this.inputContext = new FileInputStream(filePath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private boolean isAlpha(char c) {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }

    private boolean isDigit(char c) {
        return (c >= 48 && c <= 57);
    }

    private boolean isKeyword(String s) {
        for (String keyword : keywords) {
            if (keyword.equals(s)) {
                return true;
            }
        }
        return false;
    }

    private Token whenWordMatches(Token token, int nextState) {
        Analyzer.column++;
        token.words += Character.toString(this.inputChar);
        this.state = nextState;
        Analyzer.readingContextFlag = 1;
        return token;
    }

    private void whenWordNotMatches(int nextState) {
        this.state = nextState;
        Analyzer.readingContextFlag = 0;
        Analyzer.lastRedCode = (int) this.inputChar;
    }

    private Token setTokenDetails(Token token, String type) {
        token.column = Analyzer.column - token.words.length();
        token.row = Analyzer.row;
        token.blockNumber = Analyzer.blockNumber;
        token.blockOrder = Analyzer.blockOrder;
        token.type = SymbolType.Type.valueOf(type);
        Analyzer.readingContextFlag = 0;
        Analyzer.lastRedCode = (int) this.inputChar;

        if (type.equals("closeBrace")) {
            stack.pop();
            if (!stack.isEmpty()) {
                blockNumber = stack.pop();
            }
            if (stack.isEmpty()) {
                stack.push(0);
            }
            blockOrder = stack.peek();
            stack.push(blockNumber);
        }
        return token;
    }

    public Token nextToken() throws IOException {

        token = new Token();
        state = 0;
        while (true) {

            if (readingContextFlag == 1) {
                inputCode = inputContext.read();
                if (inputCode == -1) {
                    token.type = SymbolType.Type.END;
                    return token;
                }
            } else {
                inputCode = lastRedCode;
            }

            inputChar = (char) inputCode;

            switch (state) {
                case 0:
                    // Blanks (\b)
                    if (inputChar == ' ') {
                        column++;
                        readingContextFlag = 1;
                    } // Tabs (\t)
                    else if (inputChar == '\t') {
                        column += 8;
                        readingContextFlag = 1;
                    } // New lines (\n)
                    else if (inputChar == '\n' || inputChar == '\r') {
                        if (inputChar == '\n') {
                            column = 0;
                            row++;
                        }
                        readingContextFlag = 1;
                    } // ID or Keyword
                    else if (isAlpha(inputChar) || inputChar == '_') {
                        token = whenWordMatches(token, 1);
                    } // int or float
                    else if (isDigit(inputChar)) {
                        token = whenWordMatches(token, 3);
                    } // (
                    else if (inputChar == '(') {
                        token = whenWordMatches(token, 7);
                    } // )
                    else if (inputChar == ')') {
                        token = whenWordMatches(token, 8);
                    } // {
                    else if (inputChar == '{') {
                        blockNumberCounter++;
                        blockNumber = blockNumberCounter;
                        blockOrder = stack.peek();
                        stack.push(blockNumber);
                        token = whenWordMatches(token, 9);
                    } // }
                    else if (inputChar == '}') {
                        token = whenWordMatches(token, 10);

                    } // [
                    else if (inputChar == '[') {
                        token = whenWordMatches(token, 11);
                    } // ]
                    else if (inputChar == ']') {
                        token = whenWordMatches(token, 12);
                    } // ++ or += or + 
                    else if (inputChar == '+') {
                        token = whenWordMatches(token, 13);
                    } // -- or -= or - 
                    else if (inputChar == '-') {
                        token = whenWordMatches(token, 17);
                    } // ! or != 
                    else if (inputChar == '!') {
                        token = whenWordMatches(token, 21);
                    } // * or *= 
                    else if (inputChar == '*') {
                        token = whenWordMatches(token, 24);
                    } // / or /=
                    else if (inputChar == '/') {
                        token = whenWordMatches(token, 27);
                    } // > or >=
                    else if (inputChar == '>') {
                        token = whenWordMatches(token, 32);
                    } // < or <=
                    else if (inputChar == '<') {
                        token = whenWordMatches(token, 35);
                    } // = or == 
                    else if (inputChar == '=') {
                        token = whenWordMatches(token, 38);
                    } else if (inputChar == '"') {
                        token = whenWordMatches(token, 42);
                    } // semicolon (;)
                    else if (inputChar == ';') {
                        token = whenWordMatches(token, 44);
                    } else {
                        System.err.println("ERROR!"); //***************//
                        readingContextFlag = 1;
                    }
                    break;

                case 1:
                    if ((isAlpha(inputChar)) || (inputChar == '_') || (isDigit(inputChar))) {
                        token = whenWordMatches(token, 1);
                    } else {
                        whenWordNotMatches(2);
                    }
                    break;

                case 2:
                    return setTokenDetails(token, (isKeyword(token.words) ? "keyword" : "id"));

                case 3:
                    if (isDigit(inputChar)) {
                        token = whenWordMatches(token, 3);
                    } else if (inputChar == '.') {
                        token = whenWordMatches(token, 4);
                    } else {
                        whenWordNotMatches(6);
                    }
                    break;

                case 4:
                    if (isDigit(inputChar)) {
                        token = whenWordMatches(token, 4);
                    } else {
                        whenWordNotMatches(5);
                    }
                    break;

                case 5:
                    return setTokenDetails(token, "floatDigit");

                case 6:
                    return setTokenDetails(token, "intDigit");

                case 7:
                    return setTokenDetails(token, "openParanthese");

                case 8:
                    return setTokenDetails(token, "closeParanthese");

                case 9:
                    return setTokenDetails(token, "openBrace");

                case 10:
                    return setTokenDetails(token, "closeBrace");

                case 11:
                    return setTokenDetails(token, "openBracket");

                case 12:
                    return setTokenDetails(token, "closeBracket");

                case 13:
                    if (inputChar == '+') {
                        token = whenWordMatches(token, 14);
                    } else if (inputChar == '=') {
                        token = whenWordMatches(token, 15);
                    } else {
                        whenWordNotMatches(16);
                    }
                    break;

                case 14:
                    return setTokenDetails(token, "plusPlus");

                case 15:
                    return setTokenDetails(token, "assignPlus");

                case 16:
                    return setTokenDetails(token, "plus");

                case 17:
                    if (inputChar == '-') {
                        token = whenWordMatches(token, 18);
                    } else if (inputChar == '=') {
                        token = whenWordMatches(token, 19);
                    } else {
                        whenWordNotMatches(20);
                    }
                    break;

                case 18:
                    return setTokenDetails(token, "minusMinus");

                case 19:
                    return setTokenDetails(token, "assignMinus");

                case 20:
                    return setTokenDetails(token, "minus");

                case 21:
                    if (inputChar == '=') {
                        token = whenWordMatches(token, 22);
                    } else {
                        whenWordNotMatches(23);
                    }
                    break;

                case 22:
                    return setTokenDetails(token, "notEqual");

                case 23:
                    return setTokenDetails(token, "not");

                case 24:
                    if (inputChar == '=') {
                        token = whenWordMatches(token, 25);
                    } else {
                        whenWordNotMatches(26);
                    }
                    break;

                case 25:
                    return setTokenDetails(token, "assignMultiply");

                case 26:
                    return setTokenDetails(token, "multiply");

                case 27:
                    if (inputChar == '=') {
                        token = whenWordMatches(token, 28);
                    } else {
                        whenWordNotMatches(31);
                    }
                    break;

                case 28:
                    return setTokenDetails(token, "assignDivide");

                case 31:
                    return setTokenDetails(token, "divide");

                case 32:
                    if (inputChar == '=') {
                        token = whenWordMatches(token, 33);
                    } else {
                        whenWordNotMatches(34);
                    }
                    break;

                case 33:
                    return setTokenDetails(token, "greaterEqual");

                case 34:
                    return setTokenDetails(token, "greater");

                case 35:
                    if (inputChar == '=') {
                        token = whenWordMatches(token, 36);
                    } else {
                        whenWordNotMatches(37);
                    }
                    break;

                case 36:
                    return setTokenDetails(token, "lesserEqual");

                case 37:
                    return setTokenDetails(token, "lesser");

                case 38:
                    if (inputChar == '=') {
                        token = whenWordMatches(token, 39);
                    } else {
                        whenWordNotMatches(40);
                    }
                    break;

                case 39:
                    return setTokenDetails(token, "equal");

                case 40:
                    return setTokenDetails(token, "assignEqual");

                case 42:
                    if (inputChar != '"') {
                        token = whenWordMatches(token, 42);
                    } else {
                        whenWordNotMatches(43);
                    }
                    break;

                case 43:
                    return setTokenDetails(token, "charConst");

                case 44:
                    return setTokenDetails(token, "semicolon");

            }
        }
    }
}
