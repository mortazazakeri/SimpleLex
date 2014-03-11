package lexicalanalyzer;

public class Token {

    int row,
        column,
        blockNumber,
        blockOrder;
    SymbolType.Type type;
    String words = "";

    public Token() {
        this.row = 1;
        this.column = 1;
        this.blockNumber = 1;
        this.blockOrder = 0;
    }
}
