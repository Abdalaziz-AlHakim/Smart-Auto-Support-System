package behavioral.state;

import behavioral.state.TicketContext;

/**
 * State interface for the ticket lifecycle machine.
 *
 * PATTERN: State
 * Each concrete state class (OpenState, InProgressState, EscalatedState,
 * ResolvedState) implements this interface. When an action is called on
 * TicketContext, it delegates to the current state object which either
 * executes the transition or throws IllegalStateException if the transition
 * is not valid from the current state.
 *
 * This eliminates all if/switch chains on status throughout the codebase.
 */
public interface TicketState {

    /**
     * Transition from OPEN → IN_PROGRESS.
     * Only valid from OpenState; all others throw IllegalStateException.
     */
    void startProgress(TicketContext ctx);

    /**
     * Transition from IN_PROGRESS → ESCALATED.
     * Only valid from InProgressState.
     */
    void escalate(TicketContext ctx);

    /**
     * Transition from IN_PROGRESS or ESCALATED → RESOLVED.
     */
    void resolve(TicketContext ctx);

    /**
     * Transition from RESOLVED → OPEN (re-entry).
     * Only valid from ResolvedState.
     */
    void reopen(TicketContext ctx);

    /**
     * Human-readable name of this state for logging and GUI display.
     */
    String getName();
}
