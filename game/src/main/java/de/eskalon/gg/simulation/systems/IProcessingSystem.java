package de.eskalon.gg.simulation.systems;

/**
 * A system for processing entities of a certain type.
 *
 * @param <E>
 */
public interface IProcessingSystem<E> {

	public boolean doProcess(int currentTick);

	/**
	 * Is responsible for processing one entity.
	 *
	 * @param id
	 *            The id of the entity in question.
	 * @param e
	 *            The entity itself.
	 */
	public abstract void process(short id, E e);

}
