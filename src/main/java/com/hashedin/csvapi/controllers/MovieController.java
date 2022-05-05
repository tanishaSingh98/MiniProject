package com.hashedin.csvapi.controllers;

import com.hashedin.csvapi.errorHandling.ApplicationException;
import com.hashedin.csvapi.models.Movie;
import com.hashedin.csvapi.services.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MovieService movieService;

    private HttpHeaders setTimeHeader(Instant startTime){
        long time = Duration.between(startTime, Instant.now()).toMillis();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-TIME-TO-EXECUTE", String.valueOf(time));
        return responseHeaders;
    }

    /**
     * Returns all movies from the database
     *
     * @return - All movies
     */
    @GetMapping
    public ResponseEntity<List<Movie>> findAll() {
        try {
            logger.info("findAll movies " + this.getClass().getName());
            Instant start = Instant.now();
            List<Movie> movies = movieService.findAll();
            logger.debug("returned all movies");
            return new ResponseEntity<>(movies,setTimeHeader(start), HttpStatus.OK);

        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }

    }

    @GetMapping("/populate")
    public ResponseEntity<List<Movie>> load(){
        return new ResponseEntity<>(movieService.loadData(),HttpStatus.OK);
    }


    /**
     * Used to save a new movie
     *
     * @param movie - new movie details
     * @return - Saved movie
     */
    @PostMapping
    public ResponseEntity<Movie> save(@RequestBody Movie movie) {
        try {
            logger.info("save movie " + movie);
            Instant start = Instant.now();

            Movie savedMovie = movieService.save(movie);
            logger.debug("Saved a new movie" + movie);
            return new ResponseEntity<>(savedMovie, setTimeHeader(start),HttpStatus.OK);

        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }


    /**
     * Used to fetch a movie by id
     *
     * @param id - Id of the movie being fetched
     * @return - movie details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> findById(@PathVariable(value = "id") String id) {
        try {
            logger.info("find movie by id" + id);
            Instant start = Instant.now();
            Movie dataMovie = movieService.findById(id);
            return new ResponseEntity<>(dataMovie,setTimeHeader(start), HttpStatus.OK);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    /**
     * Used to update a movie
     *
     * @param id - Id of the movie being updated
     * @return - updated movie id
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable(value = "id") String id,
                                         @RequestBody Movie movie) {
        try {
            logger.info("update movie " + this.getClass().getName());
            Instant start = Instant.now();

            String dataMovie = movieService.update(id, movie);
            return new ResponseEntity<>(dataMovie,setTimeHeader(start), HttpStatus.OK);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    /**
     * Used to delete a movie
     *
     * @param id - Id of the movie being deleted
     * @return - success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") String id) {
        try {
            logger.info("Edit Configurations… movie ");
            Instant start = Instant.now();
            movieService.delete(id);
            return new ResponseEntity<>("movie deleted successfully",setTimeHeader(start), HttpStatus.OK);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    /**
     * Used to fetch movie titles by providing director name and year range
     *
     * @param director  - director of the movie
     * @param startYear - start of year range
     * @param endYear   - end of year range
     * @return - Array of movie titles satisfying the condition
     */
    @GetMapping("/findByDirectorsAndYear/{director}/{startYear}/{endYear}")
    public ResponseEntity<List<String>> findByDirectors(HttpServletResponse response, @PathVariable(value = "director") String director, @PathVariable(value = "startYear") String startYear,
                                                        @PathVariable(value = "endYear") String endYear) {
        try {
            logger.info("find titles using directors and year range ");
            Instant start = Instant.now();
            List<String> returnMovies = movieService.findByDirectorsAndYear(director, startYear, endYear);
            return new ResponseEntity<>(returnMovies,setTimeHeader(start), HttpStatus.OK);

        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }


    /**
     * Used to fetch movie titles by providing minimum user reviews required and movie language
     *
     * @param language - language of the movie
     * @param reviews  - user reviews
     * @return - Array of movie titles satisfying the condition
     */
    @GetMapping("/findByUserReviewsAndLanguage/{language}/{reviews}")
    public ResponseEntity<List<String>> findByUserReviewsAndLanguage(@PathVariable(value = "language") String language, @PathVariable(value = "reviews") String reviews) {
        try {
            logger.info("Find movie titles using user reviews and language");
            Instant start = Instant.now();
            List<String> returnMovies = movieService.findByUserReviewsAndLanguage(language, reviews);
            return new ResponseEntity<>(returnMovies,setTimeHeader(start), HttpStatus.OK);

        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    /**
     * Used to fetch movie title which has the highest bidget given country and release year of moviw
     *
     * @param country - country of the movie
     * @param year    - release year
     * @return - Movie title with highest budget
     */
    @GetMapping("/getHighestBudgetTitle/{country}/{year}")
    public ResponseEntity<String> getHighestBudgetTitle(@PathVariable(value = "country") String country, @PathVariable(value = "year") String year) {
        try {
            logger.info("Get highest budget movie using country and year");
            Instant start = Instant.now();
            String title = movieService.getHighestBudgetTitle(country, year);
            return new ResponseEntity<>(title,setTimeHeader(start), HttpStatus.OK);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

}
