package instagram.com.backend.Entity.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private String content;
    private Long postId;
    private Long parentCommentId;
    
    public CommentRequest(String content, Long postId) {
        this.content = content;
        this.postId = postId;
    }
    
}
