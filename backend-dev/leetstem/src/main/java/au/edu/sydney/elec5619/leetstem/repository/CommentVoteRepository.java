package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, Integer> {
    int countByCommentIdAndDirection(int commentId, int direction);

    CommentVote findByVoterIdAndCommentId(int voterId, int commentId);

    @Transactional
    void deleteByCommentId(Integer commentId);

    boolean existsByCommentIdAndVoterIdAndDirection(Integer commentId, Integer voterId, int direction);

}
