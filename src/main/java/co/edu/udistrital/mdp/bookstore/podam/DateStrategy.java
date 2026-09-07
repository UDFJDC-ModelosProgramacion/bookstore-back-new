/*
MIT License

Copyright (c) 2026 Universidad Distrital Francisco José de Caldas

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package co.edu.udistrital.mdp.bookstore.podam;

import java.lang.annotation.Annotation;
import java.time.LocalDate;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import uk.co.jemos.podam.common.AttributeStrategy;

public class DateStrategy implements AttributeStrategy<LocalDate> {
	Random r = new Random();

	public LocalDate getValue() {
		LocalDate startInclusive = LocalDate.of(1600, 1, 1);
		LocalDate endExclusive = LocalDate.now();
		return getRandomLocalDate(startInclusive, endExclusive);
	}

	public static LocalDate getRandomLocalDate(LocalDate startInclusive, LocalDate endExclusive) {
		long startEpochDay = startInclusive.toEpochDay();
		long endEpochDay = endExclusive.toEpochDay();
		long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay);
		return LocalDate.ofEpochDay(randomEpochDay);
	}

	@Override
	public LocalDate getValue(Class<?> attrType, List<Annotation> attrAnnotations) {
		return null;
	}

}
