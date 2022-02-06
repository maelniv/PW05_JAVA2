package fr.isen.java2.db.daos;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class FilmDao {
	/**
	 *   idfilm INT NOT NULL AUTO_INCREMENT,
	 *   title VARCHAR(100) NOT NULL,
	 *   release_date DATETIME NULL,
	 *   genre_id INT NOT NULL,
	 *   duration INT NULL,
	 *   director VARCHAR(100) NOT NULL,
	 *   summary MEDIUMTEXT NULL,
	 * @return
	 */
	public List<Film> listFilms() {
		List<Film> listOfFilms = new ArrayList<>();
		try (Connection connection = getDataSource().getConnection()) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet results = statement.executeQuery("SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre")) {
					while (results.next()) {
						Film film = new Film(results.getInt("idfilm"),
								results.getString("title"),
								results.getDate("release_date").toLocalDate(),
								new Genre(results.getInt("idgenre"),results.getString("name")),
								results.getInt("duration"),
								results.getString("director"),
								results.getString("summary"));
						listOfFilms.add(film);
					}
				}
			}
		} catch (SQLException e) {
			// Manage Exception
			e.printStackTrace();
		}
		return listOfFilms;
	}

	public List<Film> listFilmsByGenre(String genreName) {
		List<Film> listOfFilmsByGenre = new ArrayList<>();
		try (Connection connection = getDataSource().getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(
					"SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre WHERE genre.name = ?")) {
				statement.setString(1, genreName);
				try (ResultSet results = statement.executeQuery()) {
					while (results.next()) {
						Film film = new Film(results.getInt("idfilm"),
								results.getString("title"),
								results.getDate("release_date").toLocalDate(),
								new Genre(results.getInt("idgenre"),results.getString("name")),
								results.getInt("duration"),
								results.getString("director"),
								results.getString("summary"));
						listOfFilmsByGenre.add(film);
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listOfFilmsByGenre;
	}

	/**
	 * 	private Integer id;
	 * 	private String title;
	 * 	private LocalDate releaseDate;
	 * 	private Genre genre;
	 * 	private Integer duration;
	 * 	private String director;
	 * 	private String summary;
	 */
	public Film addFilm(Film film) {
		try (Connection connection = getDataSource().getConnection()) {
			String sqlQuery = "INSERT INTO film(title,release_date,genre_id,duration,director,summary)" + "VALUES(?,?,?,?,?,?)";
			try (PreparedStatement statement = connection.prepareStatement(
					sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, film.getTitle());
				statement.setDate(2, Date.valueOf(film.getReleaseDate()));
				statement.setInt(3, film.getGenre().getId());
				statement.setInt(4, film.getDuration());
				statement.setString(5, film.getDirector());
				statement.setString(6, film.getSummary());
				statement.executeUpdate();
				ResultSet ids = statement.getGeneratedKeys();
				if (ids.next()) {
					film.setId(ids.getInt(1));
					return film;
				}
 			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
