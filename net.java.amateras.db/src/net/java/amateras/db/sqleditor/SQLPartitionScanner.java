package net.java.amateras.db.sqleditor;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 * @author Naoki Takezoe
 */
public class SQLPartitionScanner extends RuleBasedPartitionScanner {
	
	public static final String SQL_COMMENT = "__sql_comment";
	public static final String SQL_STRING = "__sql_string";

	public SQLPartitionScanner() {
		IPredicateRule[] rules = new IPredicateRule[4];

		IToken comment = new Token(SQL_COMMENT);
		rules[0] = new MultiLineRule("/*", "*/", comment, (char) 0, true);
		rules[1] = new EndOfLineRule("--", comment);

		IToken string = new Token(SQL_STRING);
		rules[2] = new SingleLineRule("\"", "\"", string, '\\');
		rules[3] = new SingleLineRule("\'", "\'", string, '\\');

		setPredicateRules(rules);
	}
	
}
