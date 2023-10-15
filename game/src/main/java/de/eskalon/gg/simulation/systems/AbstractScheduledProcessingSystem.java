package de.eskalon.gg.simulation.systems;

public abstract class AbstractScheduledProcessingSystem<E>
		implements IProcessingSystem<E> {

	private int tickToRunAt;

	public AbstractScheduledProcessingSystem(int tickToRunAt) {
		this.tickToRunAt = tickToRunAt;
	}

	@Override
	public boolean doProcess(int currentTick) {
		return currentTick == tickToRunAt;
	}

}
