1. p42 itypeRef instead of typeRef

2. No name on decisionTable because it extends tExpression which extends tDMNElement

http://solitaire.omg.org/browse/DMN11-43 Gary writes 
  "The proposal for DMN11-92 removes name from all Expressions, including decision tables. So it is correct to say that the table name SHALL be the name of the Decision or Business Knowledge Model that directly contains the table." 

3. no rules on decision rule

4. Strange to use LiteralExpression.Text to hold name of var rather than IDREF. Even href would be better than that I'd have thought. 

 