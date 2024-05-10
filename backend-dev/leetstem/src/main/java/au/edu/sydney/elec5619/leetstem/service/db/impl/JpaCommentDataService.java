package au.edu.sydney.elec5619.leetstem.service.db.impl;

import au.edu.sydney.elec5619.leetstem.dto.CommentDTO;
import au.edu.sydney.elec5619.leetstem.dto.PaginatedComment;
import au.edu.sydney.elec5619.leetstem.entity.Comment;
import au.edu.sydney.elec5619.leetstem.entity.CommentVote;
import au.edu.sydney.elec5619.leetstem.repository.CommentRepository;
import au.edu.sydney.elec5619.leetstem.repository.CommentVoteRepository;
import au.edu.sydney.elec5619.leetstem.service.db.CommentDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JpaCommentDataService implements CommentDataService {

    private CommentRepository commentRepository;
    private CommentVoteRepository commentVoteRepository;

    public JpaCommentDataService(CommentRepository commentRepository, CommentVoteRepository commentVoteRepository) {
        this.commentRepository = commentRepository;
        this.commentVoteRepository = commentVoteRepository;
    }

    @Override
    public PaginatedComment getCommentsByQuestionId(int userId, int questionId, int pageSize, int pageNo) {
        // fetch comments
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Comment> comments = commentRepository.findByQuestionId(questionId, pageable);

        // check is last page
        boolean isLastPage = comments.isLast();

        // convert comment to dto
        List<CommentDTO> commentDTOList = comments.stream()
                .map(comment -> mapCommentToDTO(comment, userId))
                .collect(Collectors.toList());

        // Page
        PaginatedComment paginatedComment = new PaginatedComment();
        paginatedComment.setPageNo(pageNo);
        paginatedComment.setPageSize(pageSize);
        paginatedComment.setLastPage(isLastPage);
        paginatedComment.setCommentDTOList(commentDTOList);

        return paginatedComment;
    }

    @Override
    public void addComment(Integer userId, int questionIdInt, int commentType, String commentBody) {
        Comment comment = new Comment();
        comment.setCommenterId(userId);
        comment.setQuestionId(questionIdInt);
        comment.setContentType(commentType);
        comment.setContent(commentBody);
        comment.setTotalVotes(0);
        commentRepository.save(comment);
    }

    @Override
    public Comment getCommentById(int commentIdInt) {
        return commentRepository.findById(commentIdInt).orElse(null);
    }

    @Override
    public void voteComment(Integer userId, Comment comment, int direction) {
        // check comment_vote existence
        CommentVote commentVote = commentVoteRepository.findByVoterIdAndCommentId(userId, comment.getId());

        boolean isNewVote = false;
        if (commentVote != null) {
            // remove comment vote
            if (direction == 0) {
                comment.setTotalVotes(comment.getTotalVotes() - commentVote.getDirection());
                commentVoteRepository.delete(commentVote);
                return;
            }
        } else {
            commentVote = new CommentVote();
            commentVote.setVoterId(userId);
            commentVote.setCommentId(comment.getId());
            isNewVote = true;
        }

        // update direction
        boolean isReversed = false;
        if (direction >= 1) {
            if (!isNewVote) {
                isReversed = commentVote.getDirection() < 0;
            }
            commentVote.setDirection(1);
        } else if (direction <= -1) {
            if (!isNewVote) {
                isReversed = commentVote.getDirection() > 0;
            }
            commentVote.setDirection(-1);
        } else {
            commentVote.setDirection(0);
        }
        if (isNewVote) {
            comment.setTotalVotes(comment.getTotalVotes() + commentVote.getDirection());
        } else if (isReversed) {
            comment.setTotalVotes(comment.getTotalVotes() + commentVote.getDirection() * 2);
        }

        // update timestamp
        commentVote.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        commentVoteRepository.save(commentVote);
    }

    @Override
    public void endorseComment(Comment comment, boolean endorsed) {
        comment.setEndorsed(endorsed);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Comment comment) {
        // delete all the comment vote
        commentVoteRepository.deleteByCommentId(comment.getId());
        // delete comment
        commentRepository.delete(comment);
    }

    private CommentDTO mapCommentToDTO(Comment comment, int userId) {
        CommentDTO commentDTO = new CommentDTO();
        // map
        commentDTO.setCommentId(comment.getId().toString());
        commentDTO.setCommenterId(comment.getCommenterId().toString());
        commentDTO.setEndorsed(comment.isEndorsed());
        commentDTO.setCommentType(comment.getContentType());
        commentDTO.setCommentedAt(comment.getCreatedAt().toInstant().toEpochMilli());
        commentDTO.setCommentBody(comment.getContent());

        // get likes(dislikes)
        int likes = commentVoteRepository.countByCommentIdAndDirection(comment.getId(), 1);
        commentDTO.setLikes(likes);
        // int dislikes = commentVoteRepository.countByCommentIdAndDirection(comment.getId(), -1);
        // commentDTO.setDislikes(dislikes);

        // check current user has upvote
        boolean userIsLiked = commentVoteRepository
                .existsByCommentIdAndVoterIdAndDirection(comment.getId(), userId, 1);
        commentDTO.setLiked(userIsLiked);

        // get commenter info
        String commenterDisplayName = comment.getCommenter().getDisplayName();
        String commenterAvatar = comment.getCommenter().getAvatar();
        commentDTO.setCommenterDisplayName(commenterDisplayName);
        commentDTO.setCommenterAvatar(commenterAvatar);

        return commentDTO;
    }
}
