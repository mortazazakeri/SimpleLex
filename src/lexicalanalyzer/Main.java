package lexicalanalyzer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static FileInputStream inputContext = null;
    FileOutputStream outputContext = null;

    public static void main(String[] args) throws IOException {

        Analyzer analyzer = new Analyzer("C:\\Users\\Ali\\Documents\\NetBeansProjects\\LexicalAnalyzer\\src\\lexicalanalyzer\\Input.txt");
        Token token = new Token();
        System.out.print("TOKEN");
        System.out.print("\t\t");
        System.out.print("COLUMN");
        System.out.print("\t");
        System.out.print("ROW");
        System.out.print("\t");
        System.out.print("BLOCK#");
        System.out.print("\t");
        System.out.print("BLOCK ORDER");
        System.out.print("\t");
        System.out.print("TYPE");
        System.out.println();
        System.out.println("--------------------------------------------------------------------");
        
        while (true) {
            token = analyzer.nextToken();
            if (token.type == SymbolType.Type.END) {
                break;
            }
            System.out.print(token.words);
            if(token.words.length()>8)
                System.out.print("\t");
            else
                System.out.print("\t\t");
            System.out.print(token.column);
            System.out.print("\t");
            System.out.print(token.row);
            System.out.print("\t");
            System.out.print(token.blockNumber);
            System.out.print("\t");
            System.out.print(token.blockOrder);
            System.out.print("\t\t");
            System.out.print(token.type);
            System.out.println();
            System.out.println("--------------------------------------------------------------------");
        }

    }
}
