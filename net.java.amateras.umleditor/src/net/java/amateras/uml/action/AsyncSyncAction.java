package net.java.amateras.uml.action;

/**
 * Automatic synchronization actions (class diagram updates...) are launched on events processed
 * by {@link JavaClassSynchronizer}, ie a java source modification.
 * Normally those synchronization action are processed immediately after the event. But it is
 * not always possible so they are delayed (asynchronous).
 * 
 * Cases of impossible immediate process could be caused by the error:
 * 	org.eclipse.swt.SWTException: Invalid thread access
 * It seems to be encountered, if the editor could not get the focus when the synchronization
 * action is processed. Example: If a file is deleted, the confirmation popup has the focus.
 */
public interface AsyncSyncAction {

	void doSyncAction();
}
