package bb.orzechowski.gamestory.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CategoryDto {

    private String title;
    private List<String> games;
}
