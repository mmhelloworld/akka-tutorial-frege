package mmhelloworld.akkatutorialfregeadapter;

import akka.actor.Actor;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.japi.Creator;
import frege.runtime.Lambda;

public class Actors {

	public static UntypedActor untypedActor(final Lambda func) {
		return new FregeUntypedActor(func);
	}

	public static Props propsUntypedActor(final Lambda func) {
		return new Props().withCreator(new Creator<Actor>() {
			public Actor create() throws Exception {
				return new FregeUntypedActor(func);
			}
		});
	}

	public static class FregeUntypedActor extends UntypedActor {
		final Lambda func;

		public FregeUntypedActor(final Lambda func) {
			this.func = func.apply(this).result().forced();
		}

		@Override
		public void onReceive(final Object arg0) throws Exception {
			func.apply(arg0).apply(1).result().forced();

		}
	}

	public static Props propsStatefulActor(final Lambda constructor,
			final Lambda onReceive) {
		return new Props().withCreator(new Creator<Actor>() {
			public Actor create() throws Exception {
				return new StatefulUntypedActor(constructor, onReceive);
			}
		});
	}

	public static class StatefulUntypedActor extends UntypedActor {
		private Object state;
		private final Lambda onReceive;

		public StatefulUntypedActor(final Lambda constructor,
				final Lambda onReceive) {
			this.state = constructor.apply(this).apply(1).result().forced();
			this.onReceive = onReceive.apply(this).result().forced();
		}

		@Override
		public void onReceive(final Object msg) throws Exception {
			state = onReceive.apply(state).apply(msg).apply(1)
					.result().forced();
		}
	}

	public static UntypedActorFactory untypedActorFactory(final Lambda func) {
		return new UntypedActorFactory() {
			public Actor create() throws Exception {
				return func.apply(1).result().forced();
			}
		};
	}

}
