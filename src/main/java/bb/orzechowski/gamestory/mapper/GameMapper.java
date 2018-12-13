package bb.orzechowski.gamestory.mapper;

import bb.orzechowski.gamestory.commons.Mapper;
import bb.orzechowski.gamestory.dto.GameDto;
import bb.orzechowski.gamestory.model.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapper implements Mapper<Game, GameDto> {


    @Override
    public GameDto map(Game from) {
        return new GameDto(
                from.getTitle(),
                from.getIsbn(),
                from.getCompanyName(),
                from.getCategory().getTitle()
        );
    }

}

