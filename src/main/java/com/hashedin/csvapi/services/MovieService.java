package com.hashedin.csvapi.services;

import com.hashedin.csvapi.errorHandling.ApplicationException;
import com.hashedin.csvapi.models.Movie;
import com.hashedin.csvapi.repositories.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {

    private static Logger logger = LoggerFactory.getLogger(MovieService.class);

    @Autowired
    private MovieRepository movieRepository;

    public Movie save(Movie movie){
        logger.info("save movie " + movie);
        return movieRepository.save(movie);
    }

    public Movie findById(String id){
        logger.info("find movie by id" + id);
        return movieRepository.findById(id);
    }

    public List<Movie> findAll(){
        logger.info("findAll movies " + this.getClass().getName());
        return movieRepository.findAll();
    }

    public List<String> findByDirectorsAndYear(String director, String startYear, String endYear) {
        logger.info("find movies by director{} and year range{} {}" + director + startYear+ endYear);
        return movieRepository.findByDirectorsAndYear( director,  startYear,  endYear);
    }
    public List<String> findByUserReviewsAndLanguage(String language, String reviews){
        logger.info("find movie titles using user review greater than{} and movie language {} " + reviews + language);
        return movieRepository.findByUserReviewsAndLanguage(language,reviews);
    }

    public String update(String id, Movie movie){
        logger.info("update movie " + this.getClass().getName());
        return movieRepository.update(id, movie);
    }

    public String delete(String id){
        logger.info("delete movie " + this.getClass().getName());
        return movieRepository.delete(id);
    }
    public String  getHighestBudgetTitle(String country, String year){
        logger.info("get highest budget movie title given country{} and year{} " + country + year);
        return movieRepository.getHighestBudgetTitle(country, year);
    }
    public List<Movie> loadData() {
        List<Movie> movieList = new ArrayList<>();
        logger.info("Loading Process Started " + this.getClass().getName());
        try {
            Movie movie;
            logger.info("Reading the CSV file from directory");
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/movies.csv"));
            String line = "";
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                logger.info("Splitting the records based on regex");
//              String[] movie_row = line.split(",");
                String[] movie_row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);// use comma as separator
                System.out.println(movie_row[20]);
                movie = new Movie(movie_row[1], movie_row[3], movie_row[5], movie_row[6], movie_row[7], movie_row[8], movie_row[9], movie_row[16], movie_row[20]);
                movieList.add(movie);
            }

            logger.info("Data Mapped successfully from csv to Model");
            return movieRepository.saveAll(movieList);
//            return ResponseEntity.status(HttpStatus.OK).body("Movies table updated in DB");
        } catch (FileNotFoundException e) {
            throw new ApplicationException(e.getMessage());
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage());
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

}
