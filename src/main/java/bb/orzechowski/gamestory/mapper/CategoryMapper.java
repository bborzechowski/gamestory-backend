package bb.orzechowski.gamestory.mapper;

import bb.orzechowski.gamestory.commons.Mapper;
import bb.orzechowski.gamestory.dto.CategoryDto;
import bb.orzechowski.gamestory.model.Category;
import bb.orzechowski.gamestory.model.Game;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CategoryMapper implements Mapper<Category, CategoryDto> {


    @Override
    public CategoryDto map(Category from) {

        List<String> games = from.getGames()
                .stream()
                .map(GamesToString.INSTANCE)
                .collect(Collectors.toList());

        return new CategoryDto(
                from.getTitle(),
                games
        );

    }

    private enum GamesToString implements Function<Game, String> {
        INSTANCE;

        @Override
        public String apply(Game game) {
            return game.getTitle();
        }
    }
}

