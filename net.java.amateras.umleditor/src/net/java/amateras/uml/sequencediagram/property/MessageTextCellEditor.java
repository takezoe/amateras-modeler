/**
 * 
 */
package net.java.amateras.uml.sequencediagram.property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.classdiagram.model.Argument;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.sequencediagram.ClassModelImageResolver;

import org.eclipse.jface.contentassist.IContentAssistSubjectControl;
import org.eclipse.jface.contentassist.ISubjectControlContentAssistProcessor;
import org.eclipse.jface.contentassist.SubjectControlContentAssistant;
import org.eclipse.jface.contentassist.TextContentAssistSubjectAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Takahiro Shida.
 * 
 */
public class MessageTextCellEditor extends TextCellEditor {

	private List<AbstractUMLModel> umlModel = new ArrayList<AbstractUMLModel>();

	public MessageTextCellEditor() {
		super();
	}

	public MessageTextCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	public MessageTextCellEditor(Composite parent) {
		super(parent);
	}

	/**
	 * 
	 * @param umlModel
	 */
	public void setUMLModel(List<AbstractUMLModel> umlModel) {
		this.umlModel = umlModel;
	}

	/*
	 * (non-Javadoc) Method declared on CellEditor.
	 */
	protected Control createControl(final Composite parent) {
		super.createControl(parent);
		
		TextContentAssistSubjectAdapter adapter = new TextContentAssistSubjectAdapter(
				text);
		SubjectControlContentAssistant assistant = new SubjectControlContentAssistant();
		assistant.install(adapter);
		assistant.setContentAssistProcessor(new ContentAssistProcessor(),
				IDocument.DEFAULT_CONTENT_TYPE);
		adapter.appendVerifyKeyListener(new CodeCompilationKeyListener(text, parent,  assistant, adapter));
		return text;
	}

	class CodeCompilationKeyListener implements VerifyKeyListener {

		private Composite composite;
		
		private SubjectControlContentAssistant assistant;
		
		private TextContentAssistSubjectAdapter adapter;
		
		
		public CodeCompilationKeyListener(Text text, Composite composite, SubjectControlContentAssistant assistant, TextContentAssistSubjectAdapter adapter) {
			super();
			this.composite = composite;
			this.assistant = assistant;
			this.adapter = adapter;
		}


		public void verifyKey(final VerifyEvent event) {
			if (event.stateMask == SWT.CTRL && event.character == ' ') {
				assistant.showPossibleCompletions();
				event.doit = false;
			}
			if (event.character == SWT.ESC) {
				composite.getDisplay().syncExec(new Runnable() {

					public void run() {
						if (assistant.hasProposalPopupFocus()) {
							text.forceFocus();
						} else {
							adapter.removeVerifyKeyListener(CodeCompilationKeyListener.this);
							event.doit = false;
						}
					}
					
				});
			}
		}
		
	}

	class ContentAssistProcessor implements
			ISubjectControlContentAssistProcessor {

		public ICompletionProposal[] computeCompletionProposals(
				IContentAssistSubjectControl contentAssistSubjectControl,
				int documentOffset) {
			int caretPosition = text.getCaretPosition();
			String previouse = text.getText().substring(0, caretPosition);
			List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
			for (Iterator<AbstractUMLModel> iter = umlModel.iterator(); iter.hasNext();) {
				AbstractUMLModel model = (AbstractUMLModel) iter.next();
				CompletionProposal proposal = createProposal(model, previouse, documentOffset);
				if (proposal != null && proposal.getDisplayString().startsWith(previouse)) {
					result.add(proposal);
				}
			}
			ICompletionProposal[] proposals = new ICompletionProposal[result
					.size()];
			result.toArray(proposals);
			return proposals;
		}

		private CompletionProposal createProposal(AbstractUMLModel model,
				String previouse, int documentOffset) {
			String displayString = model.toString();
			Image image = null;
			String replaceString = null;

			if (model instanceof AttributeModel) {
				AttributeModel attr = (AttributeModel) model;
				image = ClassModelImageResolver.getAttributeImage(attr);
				replaceString = attr.getName();
			} else if (model instanceof OperationModel) {
				OperationModel ope = (OperationModel) model;
				image = ClassModelImageResolver.getOperationImage(ope);
				replaceString = getOperationReplacementString(ope);
			}
			if (replaceString == null) {
				return null;
			}
			return new CompletionProposal(replaceString,
					documentOffset
					- previouse.length(),
					previouse.length(),
					replaceString.length(),
					image,displayString, null,null);
		}

		private String getOperationReplacementString(OperationModel model) {
			StringBuffer sb = new StringBuffer();
			sb.append(model.getName());
			sb.append("(");
			for (int i = 0; i < model.getParams().size(); i++) {
				if (i != 0) {
					sb.append(", ");
				}
				Argument arg = (Argument) model.getParams().get(i);
				sb.append(arg.getName());
			}
			sb.append(")");
			return sb.toString();
		}

		public IContextInformation[] computeContextInformation(
				IContentAssistSubjectControl contentAssistSubjectControl,
				int documentOffset) {
			// TODO Auto-generated method stub
			return null;
		}

		public ICompletionProposal[] computeCompletionProposals(
				ITextViewer viewer, int offset) {
			// TODO Auto-generated method stub
			return null;
		}

		public IContextInformation[] computeContextInformation(
				ITextViewer viewer, int offset) {
			// TODO Auto-generated method stub
			return null;
		}

		public char[] getCompletionProposalAutoActivationCharacters() {
			// TODO Auto-generated method stub
			return null;
		}

		public char[] getContextInformationAutoActivationCharacters() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getErrorMessage() {
			// TODO Auto-generated method stub
			return null;
		}

		public IContextInformationValidator getContextInformationValidator() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
