package lexicalanalyzer;

public class SymbolType {
    enum Type { keyword,        //  char, int, if, else, for
                id,             //  Any ID
                intDigit,       //  int 
                floatDigit,     //  float
                openParanthese, //  (
                closeParanthese,//  )
                openBrace,      //  {
                closeBrace,     //  }
                openBracket,    //  [
                closeBracket,   //  ]
                plusPlus,       //  ++
                minusMinus,     //  --
                not,            //  !
                multiply,       //  *
                divide,         //  /
                plus,           //  +
                minus,          //  -    
                greater,        //  >
                greaterEqual,   //  >=
                lesser,         //  <
                lesserEqual,    //  <=
                equal,          //  ==
                notEqual,       //  !=
                and,            //  &&
                or,             //  ||
                assignEqual,    //  =
                assignPlus,     //  +=
                assignMinus,    //  -=
                assignMultiply, //  *=
                assignDivide,   //  /=
                charConst,      //  " ... "
                semicolon,      //  ;
                END;            // For The End
    }
}
