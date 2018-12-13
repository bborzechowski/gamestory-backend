package bb.orzechowski.gamestory.controller;


import bb.orzechowski.gamestory.auth.security.JWTAuthorizationFilter;
import bb.orzechowski.gamestory.model.Game;
import bb.orzechowski.gamestory.repository.GameRepository;
import bb.orzechowski.gamestory.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
//łączenie front z backendem  uudostepnienie połączenia na front jak damy (value= ...) możemy wybrać po ip serv front itp
@RestController
@RequestMapping("/api/")
public class GameController {




    private GameRepository gameRepository;
    private CategoryRepository categoryRepository;

    @Autowired /*nie wymagane*/
    public GameController(GameRepository gameRepository, CategoryRepository categoryRepository) {
        this.gameRepository = gameRepository;
        this.categoryRepository = categoryRepository;
    }

//    @GetMapping("books")
//    public ResponseEntity<List<Book>> getBooks() {
//        return new ResponseEntity<>(bookRepository.findAll(), HttpStatus.OK);
//    }

    @GetMapping("games")
    public List<Game> getGames() {
        return gameRepository.findAll();
    }

    @PostMapping("games")
    public ResponseEntity<Game> addGame(@RequestParam (value = "title") String title,
                                        @RequestParam (value = "isbn")String isbn,
                                        @RequestParam(value = "category") String category,
                                        @RequestParam (value = "companyName") String companyName) {

        if (gameRepository.findByIsbn(isbn).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        } else if (categoryRepository.findByTitle(category).isPresent()) {

            //tworzenie oddzielnego konstruktora pod ten przypadek nie będzie wymagane.
            Game game = new Game();
            game.setTitle(title);
            game.setIsbn(isbn);
            game.setCompanyName(companyName);
            game.setCategory(categoryRepository.findByTitle(category).get());

            return new ResponseEntity<>(gameRepository.save(game), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping("games")
    public ResponseEntity<Game> updateGame(@RequestParam String isbn, @RequestBody Game game) {
        Optional<Game> gameOptional = gameRepository.findByIsbn(isbn);

        if (gameOptional.isPresent()) {
            gameOptional.get().setTitle(game.getTitle());
           gameOptional.get().setIsbn(game.getIsbn());
           gameOptional.get().setCompanyName(game.getCompanyName());


            return new ResponseEntity<>(gameRepository.save(gameOptional.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("games/{isbn}")
    public ResponseEntity<Game> deleteBook(@PathVariable("isbn") String isbn) {

        Optional<Game> gameOptional = gameRepository.findByIsbn(isbn);

        if (gameOptional.isPresent()) {
            gameRepository.delete(gameOptional.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}

