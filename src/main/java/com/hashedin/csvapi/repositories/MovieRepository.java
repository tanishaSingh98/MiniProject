package com.hashedin.csvapi.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.hashedin.csvapi.errorHandling.ApplicationException;
import com.hashedin.csvapi.models.Movie;
import com.hashedin.csvapi.services.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MovieRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    public Movie save(Movie movie) {
        dynamoDBMapper.save(movie);
        return movie;
    }

    public List<Movie> saveAll(List<Movie> movieList) {
        dynamoDBMapper.batchSave(movieList);
        return movieList;
    }

    public Movie findById(String id) {
        return dynamoDBMapper.load(Movie.class, id);
    }

    public List<Movie> findAll() {
        return dynamoDBMapper.scan(Movie.class, new DynamoDBScanExpression());
    }

    public String update(String id, Movie movie) {
        dynamoDBMapper.save(movie,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(id)
                                )));
        logger.debug("Movie details updated" + id);

        return id;
    }

    public String delete(String id) {
        Movie movie = dynamoDBMapper.load(Movie.class, id);
        dynamoDBMapper.delete(movie);
        return "Movie deleted successfully:: " + id;
    }

    public List<String> findByDirectorsAndYear(String director, String startYear, String endYear)  {
        if(Integer.parseInt(startYear) > Integer.parseInt(endYear)){
            throw new ApplicationException("Start year cannot be greater than end year");
        }
        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(director));
        eav.put(":v2", new AttributeValue().withS(String.valueOf(startYear)));
        eav.put(":v3", new AttributeValue().withS(String.valueOf(endYear)));
        DynamoDBScanExpression scanExpression  = new DynamoDBScanExpression()
                .withFilterExpression("director = :v1 and yearOfRelease BETWEEN :v2 and :v3 ")
                .withExpressionAttributeValues(eav);
        List<Movie> movieList = dynamoDBMapper.scan(Movie.class, scanExpression);
        logger.debug("movie titles using director name and year range" );

        return movieList.stream().map(m ->m.getTitle()).collect(Collectors.toList());
    }
    public List<String> findByUserReviewsAndLanguage(String language, String reviews) {
//        if(Integer.parseInt(reviews) <0){
//            throw new ApplicationException("Review cant be less than 0");
//        }
        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1", new AttributeValue().withN(reviews));
        eav.put(":v2", new AttributeValue().withS(language));
        DynamoDBScanExpression scanExpression  = new DynamoDBScanExpression()
                .withFilterExpression("movie_language = :v2 and reviews_from_users > :v1")
                .withExpressionAttributeValues(eav);
        List<Movie> movieList = dynamoDBMapper.scan(Movie.class, scanExpression);
        logger.debug("Movie titles using user review and language");

        return movieList.stream().sorted(Comparator.comparing(Movie::getReviews_from_users).reversed())
                .map(i->i.getTitle()).collect(Collectors.toList());
    }

    public String getHighestBudgetTitle(String country, String year) {
        System.out.println("HERE 1");
        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1",  new AttributeValue().withS(country));
        eav.put(":v2",  new AttributeValue().withS(year));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("country = :v1 and yearOfRelease = :v2")
                .withExpressionAttributeValues(eav);
        System.out.println("HERE 2");
        List<Movie> movieList = dynamoDBMapper.scan(Movie.class, scanExpression);
        System.out.println(movieList);
        logger.debug("Highest budget movie title using user country and year");
        return  movieList.stream().max(Comparator.comparing(Movie::getBudget)).get().getTitle();
    }
}