package gr.aegean.book.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import gr.aegean.book.configuration.PropertyReader;
import gr.aegean.book.domain.Book;
import gr.aegean.book.exception.MyInternalServerErrorException;

public class DBHandler {
	
	private static Connection getConnection() throws MyInternalServerErrorException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");  
			Connection con = DriverManager.getConnection(  
			"jdbc:mysql://" + PropertyReader.getDbHost() + ":" + PropertyReader.getDbPort() + "/books",PropertyReader.getLogin(),PropertyReader.getPwd());
			return con;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println(e.getLocalizedMessage());
			System.out.println(" " + PropertyReader.getDbHost() + " " + PropertyReader.getDbPort());
			throw new MyInternalServerErrorException("Cannot connect to underlying database");
		}
	}
	
	private static List<String> getAuthors(String str) {
		List<String> authors = new ArrayList<String>();
		String[] res = str.split(", ");
		for (String s: res) authors.add(s);
		return authors;
	}
	
	private static Book getBookFromRS(ResultSet rs) throws SQLException{
		Book book = new Book();
		book.setIsbn(rs.getString("isbn"));
		book.setTitle(rs.getString("title"));
		book.setPublisher(rs.getString("publisher"));
		String authors = rs.getString("authors");
		List<String> auths = getAuthors(authors);
		book.setAuthors(auths);
		book.setSummary(rs.getString("summary"));
		book.setLanguage(rs.getString("language"));
		book.setDate(rs.getString("date"));
		book.setCategory(rs.getString("category"));
		
		return book;
	}
	
	
	public static List<Book> getAllBooks() throws MyInternalServerErrorException{
		List<Book> books = null;
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from book");
			if (rs.next()) {
				books = new ArrayList<Book>();
				books.add(getBookFromRS(rs));
				while (rs.next()) {
					books.add(getBookFromRS(rs));
				}
			}
			
			con.close();
		}
		catch(Exception e) {
			throw new MyInternalServerErrorException("An internal error prevented from getting the information of the library's books");
		}
		return books;
	}
	
	public static List<Book> getBooks(String title, List<String> authors, String publisher) throws MyInternalServerErrorException{
		List<Book> books = null;
		boolean hasTitle = false, hasAuthors = false, hasPublisher = false;
		hasTitle = (title != null && !title.trim().equals(""));
		hasPublisher = (publisher != null && !publisher.trim().equals(""));
		hasAuthors = (authors != null && !authors.isEmpty());
		
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			String query = "select * from book";
			if (hasTitle || hasAuthors || hasPublisher) query += " where ";
			if (hasTitle) query += "title = '" + title + "' ";
			if (hasAuthors) {
				if (hasTitle) query += " and (";
				query += "authors LIKE '" + authors.get(0) + "'";
				for (int i = 1; i < authors.size(); i++) 
					query += " or authors LIKE '" + authors.get(i) + "'";
				if (hasTitle) query += ")";
			}
			if (hasPublisher) {
				if (hasTitle || hasAuthors) query += " and ";
				query += "publisher = '" + publisher + "'";
			}
			System.out.println("query is: " + query);
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				books = new ArrayList<Book>();
				books.add(getBookFromRS(rs));
				while (rs.next()) {
					books.add(getBookFromRS(rs));
				}
			}
			
			con.close();
		}
		catch(Exception e) {
			throw new MyInternalServerErrorException("An internal error prevented from getting the information of the library's books");
		}
		return books;
	}
	
	public static Book getBook(String isbn) throws MyInternalServerErrorException{
		Book book = null;
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			String query = "select * from book where isbn='" + isbn + "'";
			System.out.println("query is: " + query);
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				book = getBookFromRS(rs);
			}
			
			con.close();
		}
		catch(Exception e) {
			throw new MyInternalServerErrorException("An internal error prevented from getting the information of the library's books");
		}
		return book;
	}
	
	public static boolean existsBook(String isbn) throws MyInternalServerErrorException {
		boolean hasBook = false;
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			String query = "select * from book where isbn='" + isbn + "'";
			System.out.println("query is: " + query);
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				hasBook = true;
			}
			
			con.close();
		}
		catch(Exception e) {
			throw new MyInternalServerErrorException("An internal error prevented from getting the information of the library's books");
		}
		return hasBook;
	}
	
	public static void updateBook(Book book) throws MyInternalServerErrorException{
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			String query = "update book set " +
			"title='" + book.getTitle() + "', " +
			"publisher='" + book.getPublisher() + "', "+
			"authors='" + HTMLHandler.getAuthors(book.getAuthors()) + "', " +
			"date=" + getFieldValue(book.getDate()) + ", " +
			"summary=" + getFieldValue(book.getSummary()) + ", " + 
			"category=" + getFieldValue(book.getCategory()) + ", " + 
			"language=" + getFieldValue(book.getLanguage()) + " " +
			"where isbn='" + book.getIsbn() + "'";
			System.out.println("query is: " + query);
			stmt.execute(query);
			
			con.close();
		}
		catch(Exception e) {
			throw new MyInternalServerErrorException("An internal error prevented from getting the information of the library's books");
		}
	}
	
	private static String getFieldValue(String value) {
		if (value == null || value.trim().equals("")) return new String("NULL");
		else return "'" + value + "'";
	}
	
	public static void createBook(Book book) throws MyInternalServerErrorException{
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			String query = "insert into book(isbn,authors,title,publisher,date,category,summary,language) "
					+ "values('" + book.getIsbn() + "','" + HTMLHandler.getAuthors(book.getAuthors()) + "','" + 
					book.getTitle() + "','" +
					book.getPublisher() + "'," + getFieldValue(book.getDate()) + "," +
					getFieldValue(book.getCategory()) + "," + getFieldValue(book.getSummary()) + "," +
					getFieldValue(book.getLanguage()) + ")";
			System.out.println("query is: " + query);
			stmt.execute(query);
			
			con.close();
		}
		catch(Exception e) {
			throw new MyInternalServerErrorException("An internal error prevented from getting the information of the library's books");
		}
	}
	
	public static boolean deleteBook(String isbn) throws MyInternalServerErrorException{
		boolean deleted = false;
		Connection con = getConnection();
		try {
			Statement stmt = con.createStatement();
			String query = "delete from book where isbn='" + isbn + "'";
			System.out.println("query is: " + query);
			stmt.execute(query);
			if (stmt.getUpdateCount() == 1) {
				deleted = true;
			}
			
			con.close();
		}
		catch(Exception e) {
			throw new MyInternalServerErrorException("An internal error prevented from getting the information of the library's books");
		}
		
		return deleted;
	}
}
