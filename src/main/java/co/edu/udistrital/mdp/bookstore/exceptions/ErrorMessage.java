/*
MIT License

Copyright (c) 2025 Universidad Distrital

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
package co.edu.udistrital.mdp.bookstore.exceptions;

public final class ErrorMessage {
	public static final String BOOK_NOT_FOUND = "The book with the given id was not found";
	public static final String REVIEW_NOT_FOUND = "The review with the given id was not found";
	public static final String EDITORIAL_NOT_FOUND = "The editorial with the given id was not found";
	public static final String PRIZE_NOT_FOUND = "The prize with the given id was not found";
	public static final String PRIZE_NOT_ASSOCIATED_AUTHOR = "The prize does not have an author";
	public static final String AUTHOR_NOT_FOUND = "The author with the given id was not found";
	public static final String ORGANIZATION_NOT_FOUND = "The organization with the given id was not found";
	public static final String ORGANIZATION_NOT_VALID = "Organization is not valid";
	public static final String ORGANIZATION_HOLDS_PRIZE = "Organization already holds a prize";
	public static final String REVIEW_NOT_ASSOCIATED_TO_BOOK = "The review is not associated to the book";
	public static final String BIRHT_DATE_AFTER = "Birth date if after current date";
	public static final String AUTHOR_ASSOCIATED_BOOKS = "Unable to delete the author because he/she has associated books";
	public static final String AUTHOR_ASSOCIATED_PRIZES = "Unable to delete the author because he/she has associated prizes";
	public static final String AUTHOR_NOT_ASSOCIATED_BOOK = "The author is not associated to the book";
	public static final String EDITORIAL_NOT_VALID = "Editorial is not valid";
	public static final String EDITORIAL_EXISTS = "Editorial name already exists";
	public static final String EDITORIAL_ASSOCIATED_BOOKS = "Unable to delete editorial because it has associated books";
	public static final String ISBN_NOT_VALID = "ISBN is not valid";
	public static final String ISBN_ALREADY_EXISTS = "ISBN already exists";
	public static final String BOOK_ASSOCIATED_AUTHORS = "Unable to delete book because it has associated authors";
	public static final String BOOK_NOT_ASSOCIATED_EDITORIAL = "The book is not associated to the editorial";
	public static final String ORGANIZATION_EXISTS = "Organization name already exists";
	public static final String ORGANIZATION_ASSOCIATED_PRIZE = "Unable to delete organization because it has a prize";
	public static final String PRIZE_ASSOCIATED_AUTHOR = "Unable to delete prize because it has an associated author";

	private ErrorMessage() {
		throw new IllegalStateException("Utility class");
	}
}