package com.hashedin.csvapi.services;

import com.hashedin.csvapi.errorHandling.ApplicationException;
import com.hashedin.csvapi.errorHandling.ResourceNotFoundException;
import com.hashedin.csvapi.models.Movie;
import com.hashedin.csvapi.repositories.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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

    public Movie findById(String id) throws ResourceNotFoundException {
        logger.info("find movie by id" + id);
        Movie movie = movieRepository.findById(id);
        if(!ObjectUtils.isEmpty(movie)){
            return movie;

        } else {
            logger.error("findAll {} Record not found", this.getClass().getName());
            throw new ResourceNotFoundException("Resource not Found");
        }
    }

    public List<Movie> findAll()throws ResourceNotFoundException{
        logger.info("findAll movies " + this.getClass().getName());
        List<Movie> movies = movieRepository.findAll();
        if(!movies.isEmpty()) {
            return movies;
        } else {
            logger.error("findAll {} Record not found", this.getClass().getName());
            throw new ResourceNotFoundException("Resource not Found");
        }
    }

    public List<String> findByDirectorsAndYear(String director, String startYear, String endYear)  throws ResourceNotFoundException {
        logger.info("find movies by director{} and year range{} {}" + director + startYear+ endYear);
        List<String> titles = movieRepository.findByDirectorsAndYear( director,  startYear,  endYear);;
        if(!titles.isEmpty()) {
            return titles;
        } else {
            logger.error("findByDirectorsAndYear {} Record not found", this.getClass().getName());
            throw new ResourceNotFoundException("Resource not Found");
        }
    }
    public List<String> findByUserReviewsAndLanguage(String language, String reviews)  throws ResourceNotFoundException {
        logger.info("find movie titles using user review greater than{} and movie language {} " + reviews + language);
        List<String> titles = movieRepository.findByUserReviewsAndLanguage(language,reviews);
        if(!titles.isEmpty()) {
            return titles;
        } else {
            logger.error("findByUserReviewsAndLanguage {} Record not found", this.getClass().getName());
            throw new ResourceNotFoundException("Resource not Found");
        }
    }

    public String update(String id, Movie movie){
        logger.info("update movie " + this.getClass().getName());
        return movieRepository.update(id, movie);
    }

    public String delete(String id){
        logger.info("delete movie " + this.getClass().getName());
        return movieRepository.delete(id);
    }
    public String  getHighestBudgetTitle(String country, String year)  throws ResourceNotFoundException {
        logger.info("get highest budget movie title given country{} and year{} " + country + year);
        String title = movieRepository.getHighestBudgetTitle(country, year);
        if(title !="") {
            return title;
        } else {
            logger.error("getHighestBudgetTitle {} Record not found", this.getClass().getName());
            throw new ResourceNotFoundException("Resource not Found");
        }
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
                movie = new Movie(movie_row[1], movie_row[3], movie_row[5], movie_row[6], movie_row[7], movie_row[8], movie_row[9], Integer.parseInt(movie_row[16]), Integer.parseInt(movie_row[16]));
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
