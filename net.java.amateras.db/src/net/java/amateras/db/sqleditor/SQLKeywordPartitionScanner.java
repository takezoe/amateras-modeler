package net.java.amateras.db.sqleditor;

import net.java.amateras.db.DBPlugin;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

public class SQLKeywordPartitionScanner extends RuleBasedScanner {

	private static String[] KEYWORDS = {"select", "update", "insert",
		"into", "delete", "from", "where", "values", "set", "order", "by",
		"left", "outer", "join", "having", "group", "create", "alter", "drop", "table"};

	public SQLKeywordPartitionScanner(){
		IToken keyword = DBPlugin.getDefault().getEditorColorProvider().getToken(
				DBPlugin.PREF_COLOR_KEYWORD);
		IToken other = DBPlugin.getDefault().getEditorColorProvider().getToken(
				DBPlugin.PREF_COLOR_DEFAULT);

		WordRule wordRule = new WordRule(new IWordDetector() {
			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}

			public boolean isWordStart(char c) {
				return Character.isJavaIdentifierStart(c);
			}
		}, other);
		for (int i = 0; i < KEYWORDS.length; i++) {
			wordRule.addWord(KEYWORDS[i], keyword);
			wordRule.addWord(KEYWORDS[i].toUpperCase(), keyword);
		}
		IRule[] rules = new IRule[2];
		rules[0] = wordRule;
		rules[1] = new WhitespaceRule(new IWhitespaceDetector() {
			public boolean isWhitespace(char character) {
				return Character.isWhitespace(character);
			}
		});

		setRules(rules);
	}

}
