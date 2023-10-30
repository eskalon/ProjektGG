package de.eskalon.gg.simulation.systems;

public abstract class AbstractContinuousProcessingSystem<E>
		implements IProcessingSystem<E> {

	private int tickRate;

	public AbstractContinuousProcessingSystem(int tickRate) {
		this.tickRate = tickRate;
	}

	@Override
	public boolean doProcess(int currentTick) {
		return currentTick != 0 && currentTick % tickRate == 0;
	}

}
