package bb.orzechowski.gamestory.controller;

import bb.orzechowski.gamestory.dto.GameDto;
import bb.orzechowski.gamestory.mapper.GameMapper;
import bb.orzechowski.gamestory.model.Category;
import bb.orzechowski.gamestory.model.Game;
import bb.orzechowski.gamestory.model.MyFile;
import bb.orzechowski.gamestory.repository.CategoryRepository;
import bb.orzechowski.gamestory.repository.GameRepository;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class GameControllerDto {

    private static String UPLOADED_FOLDER = new File("").getAbsolutePath() + "//uploads//";


    private GameRepository gameRepository;
    private CategoryRepository categoryRepository;
    private GameMapper mapper;

    @Autowired /*nie wymagane*/
    public GameControllerDto(GameRepository gameRepository, CategoryRepository categoryRepository, GameMapper mapper) {
        this.gameRepository = gameRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }


    @GetMapping("games")
    public ResponseEntity<?> getBooks(
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "category", required = false) String category) {

        if (isbn != null) {
            return getGameByIsbn(isbn);
        } else if (category != null) {
            return getGamesByCategory(category);
        } else if (companyName != null) {
            return new ResponseEntity<>(getGamesByCompanyName(companyName), HttpStatus.OK);
        }
        return new ResponseEntity<>(getAllGames(), HttpStatus.OK);
    }

    private List<GameDto> getAllGames() {
        List<Game> games = gameRepository.findAll();
        List<GameDto> gamesDto = new ArrayList<>();
        for (Game b : games) {
            gamesDto.add(mapper.map(b));
        }
        return gamesDto;
    }

    private ResponseEntity<GameDto> getGameByIsbn(String isbn) {
        Optional<Game> gameOpt = gameRepository.findByIsbn(isbn);
        if (gameOpt.isPresent()) {
            GameDto gameDto = mapper.map(gameOpt.get());
            return new ResponseEntity<>(gameDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private List<GameDto> getGamesByCompanyName(String companyName) {

        Optional<List<Game>> games = gameRepository.findByCompanyName(companyName);
        List<GameDto> gameDtos = new ArrayList<>();
        games.ifPresent(game -> game.forEach(b -> gameDtos.add(mapper.map(b))));

        return gameDtos;
    }

    private ResponseEntity<List<GameDto>> getGamesByCategory(String category) {

        Optional<Category> categoryOpt = categoryRepository.findByTitle(category);

        if (categoryOpt.isPresent()) {
            List<Game> games = gameRepository.findGamesByCategoryId(categoryOpt.get().getId());
            List<GameDto> gameDtos = new ArrayList<>();

            games.forEach(game -> {
                GameDto gameDto = mapper.map(game);
                gameDtos.add(gameDto);
            });

            return new ResponseEntity<>(gameDtos, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("games/companyNames")
    public List<String> getCompanyNames() {
        return gameRepository.getCompanyNames();

    }

    @PostMapping("games")
    public ResponseEntity<Game> addGame(@RequestBody GameDto gameDto) {

        if (gameRepository.findByIsbn(gameDto.getIsbn()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Optional<Category> categoryOptional = categoryRepository.findByTitle(gameDto.getCategory());

        if (categoryOptional.isPresent()) {

            Game game = new Game();
            game.setTitle(gameDto.getTitle());
            game.setIsbn(gameDto.getIsbn());
            game.setCompanyName(gameDto.getCompanyName());
            game.setCategory(categoryOptional.get());

            return new ResponseEntity<>(gameRepository.save(game), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping("games")
    public ResponseEntity<Game> updateGame(@RequestParam("isbn") String isbn, @RequestBody GameDto gameDto) {

        Optional<Category> categoryOptional = categoryRepository.findByTitle(gameDto.getCategory());

        if (categoryOptional.isPresent()) {

            Optional<Game> gameOpt = gameRepository.findByIsbn(isbn);

            if (gameOpt.isPresent()) {

                gameOpt.get().setTitle(gameDto.getTitle());
                gameOpt.get().setCompanyName(gameDto.getCompanyName());
                gameOpt.get().setIsbn(gameDto.getIsbn());
                gameOpt.get().setCategory(categoryOptional.get());
                gameRepository.save(gameOpt.get());

                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("games/{isbn}")
    public ResponseEntity<Game> deleteGame(@PathVariable("isbn") String isbn) {

        Optional<Game> gameOptional = gameRepository.findByIsbn(isbn);

        if (gameOptional.isPresent()) {
            gameRepository.delete(gameOptional.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    private List<GameDto> getGamesList() {

        List<Game> games = gameRepository.findAll();
        List<GameDto> gamesDto = new ArrayList<>();
        games.forEach(b -> gamesDto.add(mapper.map(b)));

        return gamesDto;
    }


    @PostMapping(value = "games/file/add")
    public MyFile createXLS(@RequestBody List<GameDto> games) {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("games");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        String[] columns = {"Title", "ISBN", "CompanyName", "Category"};

        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        AtomicInteger counter = new AtomicInteger();

        games.forEach(b -> {

            counter.getAndIncrement();

            HSSFRow row = sheet.createRow(counter.get());

            HSSFCell cell1 = row.createCell(0);
            HSSFCell cell2 = row.createCell(1);
            HSSFCell cell3 = row.createCell(2);
            HSSFCell cell4 = row.createCell(3);

            cell1.setCellValue(b.getTitle());
            cell2.setCellValue(b.getIsbn());
            cell3.setCellValue(b.getCompanyName());
            cell4.setCellValue(b.getCategory());
        });


        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }


        long time = System.currentTimeMillis();
        String file = UPLOADED_FOLDER + "books" + time + ".xls";

        try {
            //   FileOutputStream fos = new FileOutputStream(fileName + ".xls");
            //    workbook.write(fos);

            byte[] bytes = workbook.getBytes();
            Path path = Paths.get(file);
            Files.write(path, bytes);

            workbook.close();
            //   fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File newFile = new File(UPLOADED_FOLDER + file);

        return new MyFile(newFile.getName(), file);

    }


    @PostMapping("games/file/open")
    public List<GameDto> openXLSFile(@RequestParam("upload") MultipartFile upload) throws IOException {

        InputStream inputStream = new BufferedInputStream(upload.getInputStream());
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

        Sheet sheet = workbook.getSheetAt(0);

        List<GameDto> games = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {

            List<String> props = new ArrayList<>();

            for (int collIndex = 0; collIndex < 4; collIndex++) {

                Cell cell = sheet.getRow(rowIndex).getCell(collIndex);
                props.add(cell.toString());
                //    System.out.println(cell);
            }
            GameDto game = new GameDto(
                    props.get(0),
                    props.get(1),
                    props.get(2),
                    props.get(3)
            );
            games.add(game);
        }
        workbook.close();
        inputStream.close();

        games.forEach(System.out::println);

        addGames(games);

        return games;
    }


    public void addGames(List<GameDto> gameDtos) {

        List<Game> games = new ArrayList<>();

        gameDtos.forEach(bd -> {
            Optional<Category> cat = categoryRepository.findByTitle(bd.getCategory());

            cat.ifPresent(category -> games.add(
                    new Game(
                            bd.getTitle(),
                            bd.getIsbn(),
                            bd.getCompanyName(),
                            category
                    )
            ));
        });

        gameRepository.saveAll(games);
    }
}
