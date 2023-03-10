package instagram.com.backend.Service.ServiceImplement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import instagram.com.backend.Entity.Choice;
import instagram.com.backend.Entity.Poll;
import instagram.com.backend.Entity.Post;
import instagram.com.backend.Entity.PostNotification;
import instagram.com.backend.Entity.Users;
import instagram.com.backend.Entity.Response.ChoiceResponse;
import instagram.com.backend.Entity.Response.PollResponse;
import instagram.com.backend.Entity.Response.PostNotificationResponse;
import instagram.com.backend.Entity.Response.PostResponse;
import instagram.com.backend.Entity.Response.UserResponse;
import instagram.com.backend.Exception.EntityNotFountException;
import instagram.com.backend.Repository.PostNotificationRepos;
import instagram.com.backend.Repository.PostRepos;
import instagram.com.backend.Repository.UsersRepos;
import instagram.com.backend.Service.PostNotificationService;

@Service
public class PostNotificationServiceIml implements PostNotificationService {
    @Autowired
    UsersRepos usersRepos;
    @Autowired
    PostRepos postRepos;
    @Autowired
    PostNotificationRepos postNotificationRepos;

    

    @Override
    public List<PostNotificationResponse> getAllReceivedPostNotificationByAuth() {
        Users authUser = getAuthUser();
       List<PostNotification> postNotifications = postNotificationRepos.findByReceiver(authUser);
       
       List<PostNotificationResponse> responses = postNotifications.stream().map(postNotification -> mapPostNotificationToResponse(postNotification)).collect(Collectors.toList());
       responses.sort((a, b) -> a.getDateCreated().compareTo(b.getDateCreated()));
       return responses;
    }

    private Post isCheckPost(Long postId) {
        Optional<Post> entity = postRepos.findById(postId);
        if(entity.isPresent()) {
            return entity.get();
        }
        throw new EntityNotFoundException("the post not found");
    }
    
    private Users isCheckUser(Long userId) {
        Optional<Users> entity = usersRepos.findById(userId);
        if(entity.isPresent()) {
            return entity.get();
        }
        throw new EntityNotFountException("the user not found");
    }

    private UserResponse mapUserToUserResponse(Users user) {
        UserResponse userresResponse = new UserResponse(user.getId(), user.getUsername(), user.getUsername(), user.getRole(), user.getActive(), user.getIntroduction(), user.getFollowersCount(), user.getFollowingsCount(), user.getAvatarUrl(), user.getPostCounts());

        return userresResponse;

    }

    private Users getAuthUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Users> entity = usersRepos.findByUsername(username);
        if(entity.isPresent()) {
            return entity.get();
        }
        throw new EntityNotFountException("the auth user not found");
        
    }


  

    private PostNotificationResponse mapPostNotificationToResponse(PostNotification postNotification) {
        PostNotificationResponse response = new PostNotificationResponse(postNotification.getId(), postNotification.getType(), mapUserToUserResponse(postNotification.getCreator()), mapUserToUserResponse(postNotification.getReceiver()), mapPostToResponse(postNotification.getPost()), postNotification.getDateCreated());
        return response;
    }

    private PostResponse mapPostToResponse(Post post) {
        PostResponse postResponse = new PostResponse(post.getId(), post.getContent(), post.getDateCreated(), post.getDateUpdated(), post.getCommentCount(), post.getLikeCount(), mapUserToUserResponse(post.getOwner()));
        if( post.getImageUrls() != null && post.getImageUrls().size() > 0) {
            postResponse.getImageUrls().addAll(post.getImageUrls());
        }

        if(post.getTags() != null && post.getTags().size() > 0) {
            List<String> tagResponses = post.getTags().stream().map(tag -> tag.getContent()).collect(Collectors.toList());
            postResponse.setTags(tagResponses);
        }

        if(post.getPoll() != null) {
            Poll poll = post.getPoll();
            PollResponse pollResponse = new PollResponse(poll.getId(), poll.getQuestion(), poll.getExpireDays(), poll.getPost().getId());
            List<ChoiceResponse> choiceResponses = poll.getChoices().stream().map(choice -> mapChoiceToResponse(choice)).collect(Collectors.toList());
            pollResponse.setChoices(choiceResponses);
            postResponse.setPoll(pollResponse);
        }
        return postResponse;
        
    }

    private ChoiceResponse mapChoiceToResponse(Choice choice) {
        ChoiceResponse response = new ChoiceResponse(choice.getId(), choice.getAnswer(), choice.getVoteCount(), choice.getPoll().getId());
        return response;
    }
}
