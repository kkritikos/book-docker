package book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import book.inface.TestLifecycleLogger;
import gr.aegean.book.domain.Book;
import io.restassured.common.mapper.TypeRef;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;

    @DisplayName("Deleting Book Testing")
    @TestMethodOrder(OrderAnnotation.class)
    public class ThirdIT implements TestLifecycleLogger{
    	@Test
        @Order(1)
        public void deleteWrongBook() throws Exception{
        	given().delete("/rest/books/book/admin/111").then().assertThat().statusCode(404);
        }
        
        @Test
        @Order(2)
        public void deleteCorrectBook() throws Exception{
        	given().delete("/rest/books/book/admin/xxx").then().assertThat().statusCode(200);
        }
        
        @Test
        @Order(3)
        public void getNonExistingBook() throws Exception{
        	given().accept("application/json").get("/rest/books/book/xxx").then().assertThat().statusCode(404);
        }
        
        @Test
	    @Order(4)
	    public void getExistingBooks() throws Exception{
        	List<Book> books = given().accept("application/json").get("/rest/books/").then().assertThat().statusCode(200).extract().as(new TypeRef<List<Book>>(){});
	    	assertThat(books, hasSize(3));
	    	assertThat(books.get(0).getIsbn(),anyOf(equalTo("1111"),equalTo("2222"),equalTo("3333")));
	    	assertThat(books.get(1).getIsbn(),anyOf(equalTo("1111"),equalTo("2222"),equalTo("3333")));
	    	assertThat(books.get(2).getIsbn(),anyOf(equalTo("1111"),equalTo("2222"),equalTo("3333")));
	    }
    }
