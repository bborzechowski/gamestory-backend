package bb.orzechowski.gamestory.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class GameDto {

    private String title;
    private String isbn;
    private String companyName;
    private String category;
}
