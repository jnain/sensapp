/**
 * ====
 *     This file is part of SensApp [ http://sensapp.modelbased.net ]
 *
 *     Copyright (C) 2011-  SINTEF ICT
 *     Contact: SINTEF ICT <nicolas.ferry@sintef.no>
 *
 *     Module: net.modelbased.sensapp
 *
 *     SensApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     SensApp is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General
 *     Public License along with SensApp. If not, see
 *     <http://www.gnu.org/licenses/>.
 * ====
 *
 * This file is part of SensApp [ http://sensapp.modelbased.net ]
 *
 * Copyright (C) 2011-  SINTEF ICT
 * Contact: SINTEF ICT <nicolas.ferry@sintef.no>
 *
 * Module: net.modelbased.sensapp
 *
 * SensApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * SensApp is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with SensApp. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.modelbased.sensapp.service.websocketdatabase.raw.fixture;

import java.lang.reflect.Field;

import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Represents a jUnit 4.10+ Rule.
 * 
 * @author aldrin
 * 
 */
public class ContainerRule implements TestRule {
	public class ContainerRuleStatement extends Statement {
		private Statement base;

		private boolean initialized = false;

		public ContainerRuleStatement(Statement base, Description description) {
			this.base = base;
		}

		@Override
		public void evaluate() throws Throwable {
			boolean shouldInitializeP = (!initialized)
					&& (base instanceof RunBefores || base instanceof InvokeMethod);

			if (shouldInitializeP) {
				Injector injector = getInjector();

				Class<?> baseClass = base.getClass();

				Field fTargetRef = baseClass.getDeclaredField("fTarget");

				fTargetRef.setAccessible(true);

				Object target = fTargetRef.get(base);

				injector.injectMembers(target);

				initialized = true;
			}

			base.evaluate();
		}
	}

	private final Module module;

	@Override
	public Statement apply(Statement base, Description description) {
		return new ContainerRuleStatement(base, description);
	}

	protected Injector getInjector() {
		return Guice.createInjector(getModule());
	}

	public ContainerRule(Module module) {
		this.module = module;
	}

	public Module getModule() {
		return module;
	}
}
