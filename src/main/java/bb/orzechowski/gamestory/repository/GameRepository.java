package bb.orzechowski.gamestory.repository;

import bb.orzechowski.gamestory.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {

    String BY_COMPANYNAME = "SELECT * FROM games WHERE companyname like (?1%)";
    //  String BY_CATEGORY = "SELECT * FROM categories WHERE title like ?1%";
    String COMPANYNAMES = "SELECT distinct companyname FROM games";


    Optional<Game> findByIsbn(String isbn);

    List<Game> findGamesByCategoryId(Long fk_category);

    // @Async
    @Query(value = BY_COMPANYNAME, nativeQuery = true)
    Optional<List<Game>> findByCompanyName(String companyName);

    //  @Async
    //  @Query(value = BY_CATEGORY, nativeQuery = true)
    //  Optional<Book> findByCategory(String category);

    @Query(value = COMPANYNAMES, nativeQuery = true)
    List<String> getCompanyNames();
}
