package elasticbeanstalk.dao;

import org.springframework.data.repository.CrudRepository;

import elasticbeanstalk.model.Post;

public interface PostRepository extends CrudRepository<Post, Integer> {

}
