package com.kritter.DAO;

import com.kritter.classes.Book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This DAO class provides CRUD database operations for the table book in the database.
 */

public class BookDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;
    private Connection jdbcConnection;

    //get the username, url, password for the jdbc connection
    public BookDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        this.jdbcURL = jdbcURL;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
    }

    //create a connection
    protected void connect() throws SQLException {
        if (jdbcConnection == null || jdbcConnection.isClosed())                        //check if the connection is closed or not
        {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException(e);
            }
            jdbcConnection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);      //open a connetion
        }
    }

    //disconnect the connection
    protected void disconnect() throws SQLException                                      //close the connection
    {
        if (jdbcConnection != null && !jdbcConnection.isClosed()) {
            jdbcConnection.close();
        }
    }

    //this inserts a new row into the table book.
    public boolean insertBook(Book book) throws SQLException {
        String sql = "INSERT INTO book (title, author, price) VALUES (?, ?, ?)";
        connect();

        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, book.getTitle());
        statement.setString(2, book.getAuthor());
        statement.setFloat(3, book.getPrice());

        boolean rowInserted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowInserted;
    }

    // this retrieves all rows
    public List<Book> listAllBooks() throws SQLException {
        List<Book> listBook = new ArrayList();

        String sql = "SELECT * FROM book";                  //select from the book table

        connect();

        Statement statement = jdbcConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {                                          //retrieve the book information from the table
            int id = resultSet.getInt("book_id");
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            float price = resultSet.getFloat("price");

            Book book = new Book(id, title, author, price);
            listBook.add(book);
        }

        resultSet.close();
        statement.close();

        disconnect();                               //close connection

        return listBook;
    }

    //this removes an existing row in the database based on the primary key value(ID)
    public boolean deleteBook(Book book) throws SQLException {
        String sql = "DELETE FROM book where book_id = ?";                                  //sql query to delete the book information from the table

        connect();                                                                          //open connection

        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setInt(1, book.getId());                                              //use getid (primary key) to retrieve info

        boolean rowDeleted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();                                                                   //close connection
        return rowDeleted;
    }

    //this updates an existing row in the database
    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE book SET title = ?, author = ?, price = ?";                    //update query to update the table
        sql += " WHERE book_id = ?";
        connect();

        PreparedStatement statement = jdbcConnection.prepareStatement(sql);

        //update all the columns of the table
        statement.setString(1, book.getTitle());
        statement.setString(2, book.getAuthor());
        statement.setFloat(3, book.getPrice());
        statement.setInt(4, book.getId());

        boolean rowUpdated = statement.executeUpdate() > 0;
        statement.close();
        disconnect();                                                                       //close connection
        return rowUpdated;
    }

    //returns a specific row based on the primary key value(ID)
    public Book getBook(int id) throws SQLException {
        Book book = null;
        String sql = "SELECT * FROM book WHERE book_id = ?";                               //select query to display the book information on the bases of id

        connect();

        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {                                                         //print all the data
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            float price = resultSet.getFloat("price");

            book = new Book(id, title, author, price);
        }

        resultSet.close();
        statement.close();
        disconnect();
        return book;
    }
}
