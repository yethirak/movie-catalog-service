package io.aki.moviecatalogservice.resources;

import com.netflix.discovery.DiscoveryClient;
import io.aki.moviecatalogservice.models.CatalogItem;
import io.aki.moviecatalogservice.models.Movie;
import io.aki.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;
    
//    @Autowired
//    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        //get all rated movieId's
        UserRating userRating = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/"+userId,UserRating.class);
        //for each movieId call movie info service and get details

        return userRating.getUserRating().stream().map(rating -> {
            //restTemplate Way
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(),Movie.class);

            //webClientBuilder Way
            /***
             * Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/"+rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
             ***/

            return new CatalogItem(movie.getName(),"Desc",rating.getRating());
        })
        .collect(Collectors.toList());
        //put all of them together
    }
}
