package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;

public class FilmDaoTestCase {

	private FilmDao filmDao = new FilmDao();

	@Before
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS film (\r\n"
				+ "  idfilm INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM film");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first film')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second film')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third film')");
		stmt.close();
		connection.close();
	}

	 @Test
	 public void shouldListFilms() {
		 // WHEN
		 List<Film> films = filmDao.listFilms();
		 // THEN
		 assertThat(films).hasSize(3);
		 assertThat(films).extracting("id", "title", "releaseDate", "genre.name", "duration", "director", "summary")
		 		 .containsOnly(tuple(1, "Title 1", LocalDateTime.parse("2015-11-26T12:00:00.000").toLocalDate(), new Genre(1, "Drama").getName() , 120, "director 1", "summary of the first film"),
						 tuple(2, "My Title 2", LocalDateTime.parse("2015-11-14T12:00:00.000").toLocalDate(), new Genre(2, "Comedy").getName(), 114, "director 2", "summary of the second film"),
		 		 tuple(3, "Third title", LocalDateTime.parse("2015-12-12T12:00:00.000").toLocalDate(), new Genre (2, "Comedy").getName(), 176, "director 3", "summary of the third film"));
	 }

	 @Test
	 public void shouldListFilmsByGenre() {
		 // WHEN
		 List<Film> films = filmDao.listFilmsByGenre("Comedy");
		 // THEN
		 assertThat(films).hasSize(2);
		 assertThat(films).extracting("id", "title", "genre.name")
				 .containsOnly(tuple(2, "My Title 2", new Genre(2, "Comedy").getName()),
						 tuple(3, "Third title", new Genre (2, "Comedy").getName()));
	 }

	 @Test
	 public void shouldAddFilm() throws Exception {
		 // WHEN
		 Film film = new Film(null, "title 5", LocalDateTime.parse("2015-11-29T00:00:00.000").toLocalDate(),
				 new Genre(5, "genre 5"), 12, "director", "summary");
		 filmDao.addFilm(film);
		 // THEN
		 Connection connection = DataSourceFactory.getDataSource().getConnection();
		 Statement statement = connection.createStatement();
		 ResultSet resultSet = statement.executeQuery("SELECT * FROM film WHERE title='title 5'");
		 assertThat(resultSet.next()).isTrue();
		 assertThat(resultSet.getInt("idfilm")).isNotNull();
		 assertThat(resultSet.getString("title")).isEqualTo("title 5");
		 assertThat(resultSet.next()).isFalse();
		 resultSet.close();
		 statement.close();
		 connection.close();
	 }
}
