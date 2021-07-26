package book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import book.inface.TestLifecycleLogger;
import gr.aegean.book.domain.Book;
import io.restassured.common.mapper.TypeRef;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

    @DisplayName("Adding Book Testing")
    @TestMethodOrder(OrderAnnotation.class)
    public class SecondIT implements TestLifecycleLogger{
    
	    @Test
	    @Order(1)
	    public void addWrongBook() throws Exception{
	    	Book book = new Book();
	    	given().contentType("application/json").body(book).when().put("/rest/books/book/admin/xxx").then().assertThat().statusCode(400);
	    }
	    
	    private Book createBook(String isbn) {
	    	Random r = new Random();
	    	Book book = new Book();
	    	book.setIsbn(isbn);
	    	int pubId = r.nextInt(10) + 1;
	    	book.setPublisher("Pub" + pubId);
	    	int titleId = r.nextInt(10) + 1;
	    	book.setTitle("Title" + titleId);
	    	List<String> authors = new ArrayList<String>();
	    	int auth1 = r.nextInt(10) + 1;
	    	int auth2 = r.nextInt(10) + 1;
	    	authors.add("Auth" + auth1);
	    	authors.add("Auth2" + auth2);
	    	book.setAuthors(authors);
	    	
	    	return book;
	    }
	    
	    @Test
	    @Order(2)
	    public void putCorrectBook() throws Exception{
	    	Book book = createBook("xxx");
	    	given().contentType("application/json").body(book).when().put("/rest/books/book/admin/xxx").then().assertThat().statusCode(200);
	    }
	    
	    @Test
	    @Order(3)
	    public void getExistingBook() throws Exception{
	    	given().accept("application/json").get("/rest/books/book/xxx").then().assertThat().statusCode(200).and().body("isbn", equalTo("xxx"));
	    }
	    
	    @ParameterizedTest
	    @ValueSource(strings = { "1111", "2222", "3333" })
	    @Order(4)
	    void addCorrectBook(String isbn) {
	    	Book book = createBook(isbn);
	    	given().contentType("application/json").body(book).when().put("/rest/books/book/admin/" + isbn).then().assertThat().statusCode(200);
	    }
	    
	    @Test
	    @Order(5)
	    public void getExistingBooks() throws Exception{
	    	List<Book> books = given().accept("application/json").get("/rest/books/").then().assertThat().statusCode(200).extract().as(new TypeRef<List<Book>>(){});
	    	assertThat(books, hasSize(4));
	    	assertThat(books.get(0).getIsbn(),anyOf(equalTo("xxx"),equalTo("1111"),equalTo("2222"),equalTo("3333")));
	    	assertThat(books.get(1).getIsbn(),anyOf(equalTo("xxx"),equalTo("1111"),equalTo("2222"),equalTo("3333")));
	    	assertThat(books.get(2).getIsbn(),anyOf(equalTo("xxx"),equalTo("1111"),equalTo("2222"),equalTo("3333")));
	    	assertThat(books.get(3).getIsbn(),anyOf(equalTo("xxx"),equalTo("1111"),equalTo("2222"),equalTo("3333")));
	    }
	    
	    @TestFactory
	    @Order(6)
	    Stream<DynamicTest> generateRandomNumberOfTestsFromIterator() {

	        // Generates random positive integers between 0 and 100 until
	        // a number evenly divisible by 7 is encountered.
	        Iterator<Integer> inputGenerator = new Iterator<Integer>() {

	            Random random = new Random();
	            int current = 0;
	            int times = 0;

	            @Override
	            public boolean hasNext() {
	                current = 11 + random.nextInt(10);
	                times++;
	                System.out.println("Times is: " + times);
	                if (times >= 11) return false;
	                else return true;
	            }

	            @Override
	            public Integer next() {
	                return current;
	            }
	        };

	        // Generates display names like: input:5, input:37, input:85, etc.
	        Function<Integer, String> displayNameGenerator = (input) -> "input:" + input;

	        // Executes tests based on the current input value.
	        ThrowingConsumer<Integer> testExecutor = (input) -> 
	        given().accept("application/json").param("publisher","Publisher" + input).get("/rest/books").then().assertThat().statusCode(404);

	        // Returns a stream of dynamic tests.
	        return DynamicTest.stream(inputGenerator, displayNameGenerator, testExecutor);
	    }
    }
    
   
