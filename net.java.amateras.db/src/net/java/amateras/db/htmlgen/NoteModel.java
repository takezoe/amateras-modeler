package net.java.amateras.db.htmlgen;


/**
 * This class has been ported from AmaterasUML.
 *
 * @author Naoki Takezoe
 * @author Takahiro Shida
 * @since 1.0.6
 */
public class NoteModel extends AbstractDBEntityModel {

	private String content = "";

	@Override
	public boolean canSource(AbstractDBConnectionModel conn) {
		if(conn instanceof ForeignKeyModel){
			return false;
		}
		return true;
	}

	@Override
	public boolean canTarget(AbstractDBConnectionModel conn) {
		if(conn instanceof ForeignKeyModel){
			return false;
		}
		return true;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
